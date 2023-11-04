package ai.chat2db.server.domain.core.util;


import org.apache.commons.lang3.StringUtils;

public class MetaNameUtils {

    public static String getMetaName(String tableName) {
        if(StringUtils.isBlank(tableName)){
            return tableName;
        }
        if(tableName.startsWith("`") && tableName.endsWith("`")){
            return tableName.substring(1,tableName.length()-1);
        }
        if(tableName.startsWith("\"") && tableName.endsWith("\"")){
            return tableName.substring(1,tableName.length()-1);
        }
        if(tableName.startsWith("'") && tableName.endsWith("'")){
            return tableName.substring(1,tableName.length()-1);
        }
        if(tableName.startsWith("[") && tableName.endsWith("]")){
            return tableName.substring(1,tableName.length()-1);
        }
        return tableName;
    }
}
