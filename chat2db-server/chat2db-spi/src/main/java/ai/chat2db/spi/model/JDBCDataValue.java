package ai.chat2db.spi.model;

import ai.chat2db.spi.util.ResultSetUtils;
import com.google.common.io.BaseEncoding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.tika.Tika;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Objects;

/**
 * @author: zgq
 * @date: 2024年05月30日 20:48
 */
@Data
@AllArgsConstructor
public class JDBCDataValue {
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int columnIndex;
    private boolean limitSize;

    public Object getObject() {
        try {
            return resultSet.getObject(columnIndex);
        } catch (Exception e) {
            try {
                return resultSet.getString(columnIndex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String getString() {
        return ResultSetUtils.getString(resultSet, columnIndex);
    }

    public String getType() {
        return ResultSetUtils.getColumnDataTypeName(metaData, columnIndex);
    }

    public InputStream getBinaryStream() {
        return ResultSetUtils.getBinaryStream(resultSet, columnIndex);
    }

    public int getPrecision() {
        return ResultSetUtils.getColumnPrecision(metaData, columnIndex);
    }

    public byte[] getBytes() {
        return ResultSetUtils.getBytes(resultSet, columnIndex);
    }

    public boolean getBoolean() {
        return ResultSetUtils.getBoolean(resultSet, columnIndex);
    }

    public int getScale() {
        return ResultSetUtils.getColumnScale(metaData, columnIndex);
    }

    public int getInt() {

        return ResultSetUtils.getInt(resultSet, columnIndex);
    }

    public Date getDate() {
        return ResultSetUtils.getDate(resultSet, columnIndex);
    }

    public Timestamp getTimestamp() {
        return ResultSetUtils.getTimestamp(resultSet, columnIndex);
    }

    public Clob getClob() {
        return ResultSetUtils.getClob(resultSet, columnIndex);
    }

    public Blob getBlob() {
        return ResultSetUtils.getBlob(resultSet, columnIndex);
    }

    public String getBlobString() {
        Blob blob = getBlob();
        LOBInfo blobInfo = getBlobInfo(blob);
        String unit = blobInfo.getUnit();
        if (blobInfo.getSize() == 0) {
            return "";
        }

        try (InputStream binaryStream = blob.getBinaryStream()) {
            Tika tika = new Tika();
            String contentType = tika.detect(binaryStream);
            FileTypeEnum fileTypeEnum = FileTypeEnum.fromDescription(contentType);
            if (Objects.isNull(fileTypeEnum)) {
                if (limitSize && isBigSize(unit)) {
                    return String.format("[%s] %d %s", getType(), blobInfo.getSize(), unit);
                }
                return getBlobHexString();
            }
            switch (fileTypeEnum) {
                case IMAGE:
                    if (limitSize) {
                        try (InputStream imageStream = blob.getBinaryStream()) {
                            BufferedImage bufferedImage = ImageIO.read(imageStream);
                            return String.format("[%s] %dx%d JPEG image %d %s",
                                                 getType(), bufferedImage.getWidth(),
                                                 bufferedImage.getHeight(), blobInfo.getSize(), unit);
                        }
                    } else {
                        return getBlobHexString();
                    }
                case STRING:
                    if (isBigSize(unit) && limitSize) {
                        return String.format("[%s] %d %s", getType(), blobInfo.getSize(), unit);
                    } else {
                        return new String(binaryStream.readAllBytes());
                    }
                default:
                    if (isBigSize(unit) && limitSize) {
                        return String.format("[%s] %d %s", getType(), blobInfo.getSize(), unit);
                    }
                    return getBlobHexString();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private LOBInfo getBlobInfo(Blob blob) {
        try {
            long size = blob.length();
            return getLobInfo(size);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClobString() {
        Clob clob = getClob();
        LOBInfo cLobInfo = getCLobInfo(clob);
        int size = cLobInfo.getSize();
        if (size == 0) {
            return "";
        }
        String unit = cLobInfo.getUnit();
        if (limitSize && isBigSize(unit)) {
            return String.format("[%s] %d %s", getType(), size, unit);
        }
        StringBuilder builder = new StringBuilder(size);
        String line;
        try (BufferedReader reader = new BufferedReader(clob.getCharacterStream())) {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private boolean isBigSize(String unit) {
        return LobUnit.G.unit.equals(unit) || LobUnit.M.unit.equals(unit);
    }

    public LOBInfo getCLobInfo(Clob clob) {
        try {
            long size = clob.length();
            return getLobInfo(size);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @NotNull
    private LOBInfo getLobInfo(long size) {
        if (size == 0) {
            return new LOBInfo(LobUnit.B.unit, 0);
        }
        return calculateSizeAndUnit(size);
    }

    @NotNull
    private LOBInfo calculateSizeAndUnit(long size) {
        if (size > LobUnit.G.size) {
            return new LOBInfo(LobUnit.G.unit, (int) (size / LobUnit.G.size));
        } else if (size > LobUnit.M.size) {
            return new LOBInfo(LobUnit.M.unit, (int) (size / LobUnit.M.size));
        } else if (size > LobUnit.K.size) {
            return new LOBInfo(LobUnit.K.unit, (int) (size / LobUnit.K.size));
        } else {
            return new LOBInfo(LobUnit.B.unit, (int) size);
        }
    }

    public String getBlobHexString() {
        return "0x" + BaseEncoding.base16().encode(getBytes());
    }

    public BigDecimal getBigDecimal() {
        return ResultSetUtils.getBigDecimal(resultSet, columnIndex);
    }

    public String getBigDecimalString() {
        BigDecimal bigDecimal = getBigDecimal();
        return bigDecimal == null ? new String(getBytes()) : bigDecimal.toPlainString();
    }

    @Data
    @AllArgsConstructor
    public static class LOBInfo {
        private String unit;
        private int size;
    }

    @Getter
    public enum LobUnit {
        B("B", 1),
        K("KB", 1024),
        M("MB", 1024 * 1024),
        G("GB", 1024 * 1024 * 1024);
        private final String unit;
        private final int size;

        LobUnit(String unit, int size) {
            this.unit = unit;
            this.size = size;
        }
    }

    @Getter
    public enum FileTypeEnum {
        IMAGE("image/jpeg"),
        STRING("text/plain"),
        ;
        private final String description;

        FileTypeEnum(String description) {
            this.description = description;
        }

        public static FileTypeEnum fromDescription(String description) {
            for (FileTypeEnum fileType : values()) {
                if (fileType.description.equals(description)) {
                    return fileType;
                }
            }
            return null;
        }
    }
}
