package ai.chat2db.plugin.mysql.value;

import ai.chat2db.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import org.apache.tika.Tika;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author: zgq
 * @date: 2024年06月03日 20:48
 */
public class MysqlVarBinaryProcessor extends MysqlBinaryProcessor {
    private final static int KB = 1024;
    private final static int MB = KB * 1024;
    private final static int GB = MB * 1024;

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        // TODO: insert file
        return super.convertSQLValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        //TODO: Identify more file types
        InputStream binaryStream = dataValue.getBinaryStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = binaryStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            baos.flush();

            InputStream copiedStream = new ByteArrayInputStream(baos.toByteArray());

            Tika tika = new Tika();
            String fileType = tika.detect(copiedStream);

            if ("image/jpeg".equals(fileType)) {
                BufferedImage bufferedImage = ImageIO.read(copiedStream);
                copiedStream.reset();
                long size = baos.size();
                String unit = "B";
                if (size > GB) {
                    size /= GB;
                    unit = "GB";
                } else if (size > MB) {
                    size /= MB;
                    unit = "MB";
                } else if (size > KB) {
                    size /= KB;
                    unit = "KB";
                }
                return String.format(MysqlDmlValueTemplate.IMAGE_TEMPLATE, dataValue.getType(),
                                     bufferedImage.getWidth(), bufferedImage.getHeight(), size, unit);
            } else if ("text/plain".equals(fileType)) {
                return baos.toString(StandardCharsets.UTF_8);
            }
            return super.convertJDBCValueByType(dataValue);
        } catch (IOException e) {
            return super.convertJDBCValueByType(dataValue);
        }
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return super.convertJDBCValueStrByType(dataValue);
    }
}

