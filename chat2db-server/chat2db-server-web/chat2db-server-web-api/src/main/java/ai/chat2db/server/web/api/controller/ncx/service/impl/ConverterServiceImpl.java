package ai.chat2db.server.web.api.controller.ncx.service.impl;

import ai.chat2db.server.domain.core.util.DesUtil;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.domain.repository.entity.DataSourceDO;
import ai.chat2db.server.domain.repository.mapper.ChartMapper;
import ai.chat2db.server.domain.repository.mapper.DataSourceMapper;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.controller.ncx.cipher.CommonCipher;
import ai.chat2db.server.web.api.controller.ncx.dbeaver.DefaultValueEncryptor;
import ai.chat2db.server.web.api.controller.ncx.enums.DataBaseType;
import ai.chat2db.server.web.api.controller.ncx.enums.ExportConstants;
import ai.chat2db.server.web.api.controller.ncx.enums.VersionEnum;
import ai.chat2db.server.web.api.controller.ncx.factory.CipherFactory;
import ai.chat2db.server.web.api.controller.ncx.service.ConverterService;
import ai.chat2db.server.web.api.controller.ncx.vo.UploadVO;
import ai.chat2db.server.web.api.util.XMLUtils;
import ai.chat2db.spi.model.SSHInfo;
import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ConverterServiceImpl
 *
 * @author lzy
 **/
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ConverterServiceImpl implements ConverterService {

    private static final double NAVICAT11 = 1.1D;

    private static CommonCipher cipher;

    /**
     * Connection information header
     **/
    private static final String DATASOURCE_SETTINGS = "#DataSourceSettings#";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    /**
     * xml connection information start flag
     **/
    private static final String BEGIN = "#BEGIN#";
    /**
     * Password json key
     **/
    private static final String connection = "#connection";


    private DataSourceMapper getDataSourceMapper(){
        return Dbutils.getMapper(DataSourceMapper.class);
    }
    /**
     * jdbc universal matching ip and port
     */
    public static final Pattern IP_PORT = Pattern.compile("jdbc:(?<type>[a-z]+)://(?<host>[a-zA-Z0-9-//.]+):(?<port>[0-9]+)");
    /**
     * oracle matching ip and port
     */
    public static final Pattern ORACLE_IP_PORT = Pattern.compile("jdbc:(?<type>[a-z]+):(?<child>[a-z]+):@(?<host>[a-zA-Z0-9-//.]+):(?<port>[0-9]+)");

    @Override
    public UploadVO uploadFile(File file) {
        UploadVO vo = new UploadVO();
        try {
            // List<Map <connection name, Map<property name, value>>> The connection to be imported
            List<Map<String, Map<String, String>>> configMap = new ArrayList<>();
            //1、Create a DocumentBuilderFactory object
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //2、Create a DocumentBuilder object
            //Create DocumentBuilder object
            DocumentBuilder db = dbf.newDocumentBuilder();
            //3、Load the xml file into the current project through the parser method of the DocumentBuilder object
            Document document = db.parse(file);
            //Get the collection of all Connections nodes
            NodeList connectList = document.getElementsByTagName("Connection");

            NodeList nodeList = document.getElementsByTagName("Connections");
            //Select the first node
            NamedNodeMap verMap = nodeList.item(0).getAttributes();
            double version = Double.parseDouble((verMap.getNamedItem("Ver").getNodeValue()));
            if (version <= NAVICAT11) {
                cipher = CipherFactory.get(VersionEnum.native11.name());
            } else {
                cipher = CipherFactory.get(VersionEnum.navicat12more.name());
            }
            //Configure map
            Map<String, Map<String, String>> connectionMap = new HashMap<>();
            //Traverse each Connections node
            for (int i = 0; i < connectList.getLength(); i++) {
                //Get a Connection node through the item(i) method, the index value of nodeList starts from 0
                Node connect = connectList.item(i);
                //Get the collection of all properties of the Connection node
                NamedNodeMap attrs = connect.getAttributes();
                //Traverse the properties of Connection
                Map<String, String> map = new HashMap<>(0);
                for (int j = 0; j < attrs.getLength(); j++) {
                    //Obtain a certain attribute of the connect node through the item(index) method
                    Node attr = attrs.item(j);
                    map.put(attr.getNodeName(), attr.getNodeValue());
                }
                connectionMap.put(map.get("ConnectionName") + map.get("ConnType"), map);
            }
            configMap.add(connectionMap);
            log.info("insert to db, param:{}", JSON.toJSONString(configMap));
            // Get the link imported from navicat and write it into the h2 database of chat2db
            insertDBConfig(configMap);
            log.info("insert to h2 success");
            //Delete temporary files
            FileUtils.delete(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return vo;
    }

    @SneakyThrows
    @Override
    public UploadVO dbpUploadFile(File file) {
        UploadVO vo = new UploadVO();
        Document metaTree;
        //Projects waiting to be deleted
        List<String> projects = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ)) {
            ZipEntry metaEntry = zipFile.getEntry(ExportConstants.META_FILENAME);
            if (metaEntry == null) {
                throw new RuntimeException("Cannot find meta file");
            }
            try (InputStream metaStream = zipFile.getInputStream(metaEntry)) {
                metaTree = XMLUtils.parseDocument(metaStream);
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse meta file: " + e.getMessage());
            }
            Element projectsElement = XMLUtils.getChildElement(metaTree.getDocumentElement(), ExportConstants.TAG_PROJECTS);
            if (projectsElement != null) {
                final Collection<Element> projectList = XMLUtils.getChildElementList(projectsElement, ExportConstants.TAG_PROJECT);
                for (Element projectElement : projectList) {
                    //Get project name
                    String projectName = projectElement.getAttribute(ExportConstants.ATTR_NAME);
                    //Import matching file directory
                    String config = ConfigUtils.CONFIG_BASE_PATH + File.separator + projectName + File.separator + ExportConstants.CONFIG_FILE;
                    importDbeaverConfig(new File(config),
                            projectElement,
                            //Cannot be replaced by File.separator
                            ExportConstants.DIR_PROJECTS + "/" + projectName + "/",
                            zipFile);
                    //Add to delete list
                    projects.add(projectName);
                    //Configured json file
                    File json = new File(config + File.separator + ExportConstants.CONFIG_DATASOURCE_FILE);
                    JSONObject jsonObject = JSON.parseObject(new FileInputStream(json));
                    JSONObject connections = jsonObject.getJSONObject(ExportConstants.DIR_CONNECTIONS);
                    Set<String> keys = connections.keySet();
                    for (String key : keys) {
                        JSONObject configurations = connections.getJSONObject(key);
                        JSONObject configuration = configurations.getJSONObject(ExportConstants.DIR_CONFIGURATION);
                        //Match database type
                        String provider = configurations.getString("provider");
                        if (provider.equals(ExportConstants.GENERIC)) {
                            //Custom driverCustom driver
                            JSONObject drivers = jsonObject.getJSONObject(ExportConstants.DIR_DRIVERS);
                            //Get driver id
                            String driverId = configurations.getString("driver");
                            //Get all generic
                            JSONObject generics = drivers.getJSONObject(provider);
                            //Get your own driver
                            JSONObject generic = generics.getJSONObject(driverId);
                            //If it does not exist, it will not be imported.
                            if (null == generic) {
                                continue;
                            }
                            //Assign driver name to determine the type of database
                            provider = generic.getString("name");
                        }
                        DataBaseType dataBaseType = DataBaseType.matchType(provider.toUpperCase());
                        DataSourceDO dataSourceDO;
                        //The database type is not matched. For example: dbeaver supports custom drivers, etc., but chat2DB does not support it yet.
                        if (null != dataBaseType) {
                            //Password information
                            File credentials = new File(config + File.separator + ExportConstants.CONFIG_CREDENTIALS_FILE);
                            DefaultValueEncryptor defaultValueEncryptor = new DefaultValueEncryptor(DefaultValueEncryptor.getLocalSecretKey());
                            JSONObject credentialsJson = JSON.parseObject(defaultValueEncryptor.decryptValue(Files.readAllBytes(credentials.toPath())));
                            dataSourceDO = new DataSourceDO();
                            Date dateTime = new Date();
                            dataSourceDO.setGmtCreate(dateTime);
                            dataSourceDO.setGmtModified(dateTime);
                            //Insert user id
                            dataSourceDO.setUserId(ContextUtils.getUserId());
                            dataSourceDO.setAlias(configurations.getString("name"));
                            dataSourceDO.setHost(configuration.getString("host"));
                            dataSourceDO.setPort(configuration.getString("port"));
                            dataSourceDO.setUrl(configuration.getString("url"));
                            //ssh is set to false
                            SSHInfo sshInfo = new SSHInfo();
                            sshInfo.setUse(false);
                            dataSourceDO.setSsh(JSON.toJSONString(sshInfo));
                            if (null != credentialsJson) {
                                JSONObject userInfo = credentialsJson.getJSONObject(key);
                                JSONObject userPassword = userInfo.getJSONObject(connection);
                                dataSourceDO.setUserName(userPassword.getString("user"));
                                DesUtil desUtil = new DesUtil(DesUtil.DES_KEY);
                                String password = userPassword.getString("password");
                                String encryptStr = desUtil.encrypt(Optional.ofNullable(password).orElse(""), "CBC");
                                dataSourceDO.setPassword(encryptStr);
                            }
                            dataSourceDO.setType(dataBaseType.name());
                            getDataSourceMapper().insert(dataSourceDO);
                        }
                    }
                }
            }
        }
        //Delete temporary files
        FileUtils.delete(file);
        //Delete the temporary configuration file generated by dbp when importing dbeaver
        projects.forEach(v -> FileUtils.delete(new File(ConfigUtils.CONFIG_BASE_PATH + File.separator + v)));
        return vo;
    }

    @SneakyThrows
    private static void importDbeaverConfig(File resource, Element resourceElement, String containerPath, ZipFile zipFile) {
        for (Element childElement : XMLUtils.getChildElementList(resourceElement, ExportConstants.TAG_RESOURCE)) {
            String childName = childElement.getAttribute(ExportConstants.ATTR_NAME);
            String entryPath = containerPath + childName;
            ZipEntry resourceEntry = zipFile.getEntry(entryPath);
            if (resourceEntry == null) {
                continue;
            }
            boolean isDirectory = resourceEntry.isDirectory();
            if (isDirectory) {
                File folder = new File(resource.getPath());
                if (!folder.exists()) {
                    FileUtil.mkdir(folder);
                }
                importDbeaverConfig(folder, childElement, entryPath + "/", zipFile);
            } else {
                File file = new File(resource.getPath() + File.separator + childName);
                FileUtil.writeFromStream(zipFile.getInputStream(resourceEntry), file, true);
            }
        }
    }

    @SneakyThrows
    @Override
    public UploadVO datagripUploadFile(String text) {
        UploadVO vo = new UploadVO();
        if (!text.startsWith(DATASOURCE_SETTINGS)) {
            throw new RuntimeException("连接信息的头部不正确！");
        }
        String[] items = text.split("\n");
        List<String> configs = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(BEGIN)) {
                configs.add(XML_HEADER + items[i + 1]);
            }
        }
        for (String config : configs) {
            //1、Create a DocumentBuilderFactory object
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //2、Create a DocumentBuilder object
            //Create DocumentBuilder object
            DocumentBuilder db = dbf.newDocumentBuilder();
            //3、Load the xml file into the current project through the parser method of the DocumentBuilder object
            try (InputStream inputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8))) {
                Document document = db.parse(inputStream);
                // Get the root element
                Element rootElement = document.getDocumentElement();
                //Create datasource
                DataSourceDO dataSourceDO = new DataSourceDO();
                Date dateTime = new Date();
                dataSourceDO.setGmtCreate(dateTime);
                dataSourceDO.setGmtModified(dateTime);
                dataSourceDO.setAlias(rootElement.getAttribute("name"));
                //Insert user id
                dataSourceDO.setUserId(ContextUtils.getUserId());
                // Get child elements database-info
                Element databaseInfoElement = (Element) rootElement.getElementsByTagName("database-info").item(0);

                // Get connection related information
                String type = databaseInfoElement.getAttribute("dbms");
                String jdbcUrl = rootElement.getElementsByTagName("jdbc-url").item(0).getTextContent();
                String username = rootElement.getElementsByTagName("user-name").item(0).getTextContent();
                String driverName = rootElement.getElementsByTagName("jdbc-driver").item(0).getTextContent();
                String host = "";
                String port = "";
                if (type.equals(DataBaseType.ORACLE.name())) {
                    // Create Matcher object
                    Matcher matcher = ORACLE_IP_PORT.matcher(jdbcUrl);
                    // Find matching IP address and port number
                    if (matcher.find()) {
                        host = matcher.group("host");
                        port = matcher.group("port");
                    }
                } else {
                    // Create Matcher object
                    Matcher matcher = IP_PORT.matcher(jdbcUrl);
                    // Find matching IP address and port number
                    if (matcher.find()) {
                        host = matcher.group("host");
                        port = matcher.group("port");

                    }
                }
                //ssh is set to false
                SSHInfo sshInfo = new SSHInfo();
                sshInfo.setUse(false);
                dataSourceDO.setSsh(JSON.toJSONString(sshInfo));
                dataSourceDO.setHost(host);
                dataSourceDO.setPort(port);
                dataSourceDO.setUrl(jdbcUrl);
                dataSourceDO.setUserName(username);
                dataSourceDO.setDriver(driverName);
                dataSourceDO.setType(type);
                getDataSourceMapper().insert(dataSourceDO);
            }
        }
        return vo;
    }

    /**
     * Write to database
     *
     * @param list Read data from ncx file
     */
    @SneakyThrows
    public void insertDBConfig(List<Map<String, Map<String, String>>> list) {
        for (Map<String, Map<String, String>> map : list) {
            for (Map.Entry<String, Map<String, String>> valueMap : map.entrySet()) {
                Map<String, String> resultMap = valueMap.getValue();
                // The version of mysql cannot be distinguished yet
                DataBaseType dataBaseType = DataBaseType.matchType(resultMap.get("ConnType"));
                DataSourceDO dataSourceDO;
                if (null == dataBaseType) {
                    //The database type is not matched. For example: navicat supports MongoDB, etc., but chat2DB does not support it yet.
                    continue;
                } else {
                    dataSourceDO = new DataSourceDO();
                    dataSourceDO.setHost(resultMap.get("Host"));
                    dataSourceDO.setPort(resultMap.get("Port"));
                    dataSourceDO.setUrl(String.format(dataBaseType.getUrlString(), dataSourceDO.getHost(), dataSourceDO.getPort()));
                }
                // Decrypt password
                String password = cipher.decryptString(resultMap.getOrDefault("Password", ""));
                Date dateTime =new Date();
                dataSourceDO.setGmtCreate(dateTime);
                dataSourceDO.setGmtModified(dateTime);
                dataSourceDO.setAlias(resultMap.get("ConnectionName"));
                dataSourceDO.setUserName(resultMap.get("UserName"));
                dataSourceDO.setType(resultMap.get("ConnType"));
                //Insert user id
                dataSourceDO.setUserId(ContextUtils.getUserId());
                //Password is the decrypted ciphertext, and then uses chat2db for encryption.
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
                    // Currently chat2DB only supports password and Private key
                    boolean passwordType = "password".equalsIgnoreCase(resultMap.get("SSH_AuthenMethod"));
                    sshInfo.setAuthenticationType(passwordType ? "password" : "Private key");
                    if (passwordType) {
                        // Decrypt password
                        String ssh_password = cipher.decryptString(resultMap.getOrDefault("SSH_Password", ""));
                        sshInfo.setPassword(ssh_password);
                    } else {
                        sshInfo.setKeyFile(resultMap.get("SSH_PrivateKey"));
                        sshInfo.setPassphrase(resultMap.get("SSH_Passphrase"));
                    }
                }
                dataSourceDO.setSsh(JSON.toJSONString(sshInfo));
                log.info("begin insert:{}", JSON.toJSONString(dataSourceDO));
                getDataSourceMapper().insert(dataSourceDO);
            }
        }
    }
}
