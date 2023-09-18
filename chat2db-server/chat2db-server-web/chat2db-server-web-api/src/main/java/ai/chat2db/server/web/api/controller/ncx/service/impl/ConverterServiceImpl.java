package ai.chat2db.server.web.api.controller.ncx.service.impl;

import ai.chat2db.server.domain.core.util.DesUtil;
import ai.chat2db.server.domain.repository.entity.DataSourceDO;
import ai.chat2db.server.domain.repository.mapper.DataSourceMapper;
import ai.chat2db.server.web.api.controller.ncx.cipher.CommonCipher;
import ai.chat2db.server.web.api.controller.ncx.enums.DataBaseType;
import ai.chat2db.server.web.api.controller.ncx.enums.VersionEnum;
import ai.chat2db.server.web.api.controller.ncx.factory.CipherFactory;
import ai.chat2db.server.web.api.controller.ncx.service.ConverterService;
import ai.chat2db.server.web.api.controller.ncx.vo.UploadVO;
import ai.chat2db.spi.model.SSHInfo;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.fastjson2.JSON;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ConverterServiceImpl
 *
 * @author lzy
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ConverterServiceImpl implements ConverterService {

    private static final double NAVICAT11 = 1.1D;

    private static CommonCipher cipher;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Override
    public UploadVO uploadFile(File file) {

        UploadVO vo = new UploadVO();
        try {
            // List<Map <连接名，Map<属性名，值>>> 要导入的连接
            List<Map<String, Map<String, String>>> configMap = new ArrayList<>();
            //1、创建一个DocumentBuilderFactory的对象
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //2、创建一个DocumentBuilder的对象
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //3、通过DocumentBuilder对象的parser方法加载xml文件到当前项目下
            Document document = db.parse(file);
            //获取所有Connections节点的集合
            NodeList connectList = document.getElementsByTagName("Connection");

            NodeList nodeList = document.getElementsByTagName("Connections");
            //选中第一个节点
            NamedNodeMap verMap = nodeList.item(0).getAttributes();
            double version = Double.parseDouble((verMap.getNamedItem("Ver").getNodeValue()));
            if (version <= NAVICAT11) {
                cipher = CipherFactory.get(VersionEnum.native11.name());
            } else {
                cipher = CipherFactory.get(VersionEnum.navicat12more.name());
            }
            //配置map
            Map<String, Map<String, String>> connectionMap = new HashMap<>();
            //遍历每一个Connections节点
            for (int i = 0; i < connectList.getLength(); i++) {
                //通过 item(i)方法 获取一个Connection节点，nodeList的索引值从0开始
                Node connect = connectList.item(i);
                //获取Connection节点的所有属性集合
                NamedNodeMap attrs = connect.getAttributes();
                //遍历Connection的属性
                Map<String, String> map = new HashMap<>(0);
                for (int j = 0; j < attrs.getLength(); j++) {
                    //通过item(index)方法获取connect节点的某一个属性
                    Node attr = attrs.item(j);
                    map.put(attr.getNodeName(), attr.getNodeValue());
                }
                connectionMap.put(map.get("ConnectionName") + map.get("ConnType"), map);
            }
            configMap.add(connectionMap);
            // 将获取到navicat导入的链接，写入chat2db的h2数据库
            insertDBConfig(configMap);
            //删除临时文件
            FileUtils.delete(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return vo;
    }

    /**
     * 写入到数据库
     *
     * @param list 读取ncx文件的数据
     */
    @SneakyThrows
    public void insertDBConfig(List<Map<String, Map<String, String>>> list) {
        for (Map<String, Map<String, String>> map : list) {
            for (Map.Entry<String, Map<String, String>> valueMap : map.entrySet()) {
                Map<String, String> resultMap = valueMap.getValue();
                // 解密密码
                String password = cipher.decryptString(resultMap.getOrDefault("Password", ""));
                DataSourceDO dataSourceDO = new DataSourceDO();
                LocalDateTime dateTime = LocalDateTime.now();
                dataSourceDO.setGmtCreate(dateTime);
                dataSourceDO.setGmtModified(dateTime);
                dataSourceDO.setAlias(resultMap.get("ConnectionName"));
                dataSourceDO.setHost(resultMap.get("Host"));
                dataSourceDO.setPort(resultMap.get("Port"));
                dataSourceDO.setUserName(resultMap.get("UserName"));
                dataSourceDO.setType(resultMap.get("ConnType"));
                // mysql的版本还无法区分
                dataSourceDO.setUrl(String.format(Objects.requireNonNull(DataBaseType.matchType(dataSourceDO.getType())).getUrlString(), dataSourceDO.getHost(), dataSourceDO.getPort()));
                //password 为解密出来的密文，再使用chat2db的加密
                DesUtil desUtil = new DesUtil(DesUtil.DES_KEY);
                String encryptStr = desUtil.encrypt(password, "CBC");
                dataSourceDO.setPassword(encryptStr);
                SSHInfo sshInfo = new SSHInfo();
                if ("false".equals(resultMap.get("SSH"))) {
                    sshInfo.setUse(false);
                } else {
                    sshInfo.setUse(true);
                    sshInfo.setHostName(resultMap.get("SSH_Host"));
                    sshInfo.setPort(resultMap.get("SSH_Port"));
                    sshInfo.setUserName(resultMap.get("SSH_UserName"));
                    // 目前chat2DB只支持 password 和 Private key
                    boolean passwordType = "password".equalsIgnoreCase(resultMap.get("SSH_AuthenMethod"));
                    sshInfo.setAuthenticationType(passwordType ? "password" : "Private key");
                    if (passwordType) {
                        // 解密密码
                        String ssh_password = cipher.decryptString(resultMap.getOrDefault("SSH_Password", ""));
                        sshInfo.setPassword(ssh_password);
                    } else {
                        sshInfo.setKeyFile(resultMap.get("SSH_PrivateKey"));
                        sshInfo.setPassphrase(resultMap.get("SSH_Passphrase"));
                    }
                }
                dataSourceDO.setSsh(JSON.toJSONString(sshInfo));
                dataSourceMapper.insert(dataSourceDO);
            }
        }
    }
}
