package ai.chat2db.spi.util;

import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortUtils {

    public static List<Database> sortDatabase(List<Database> databases, List<String> list, Connection connection) {
        if (CollectionUtils.isEmpty(databases)) {
            return databases;
        }
        List<Database> databaseList = new ArrayList<>();
        List<Database> systemDatabases = databases.stream()
                .filter(database -> list.contains(database.getName())).collect(Collectors.toList());
        List<Database> userDatabases = databases.stream()
                .filter(database -> !list.contains(database.getName())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(userDatabases)) {
            databaseList = databases;
        }else if (CollectionUtils.isEmpty(systemDatabases)) {
            databaseList = userDatabases;
        }else {
            databaseList = Stream.concat(userDatabases.stream(), systemDatabases.stream())
                    .collect(Collectors.toList());
        }
        // If the database name contains the name of the current database, the current database is placed in the first place

        String ulr;
        try {
            ulr = connection.getMetaData().getURL();
        } catch (SQLException e) {
            return databaseList;
        }
        // If the database name contains the name of the current database, the current database is placed in the first place
        int no = -1;
        for (int i = 0; i < databases.size(); i++) {
            if (StringUtils.isNotBlank(ulr)
                    && StringUtils.isNotBlank(databases.get(i).getName())
                    && ulr.contains(databases.get(i).getName())
                    && !"mysql".equalsIgnoreCase(databases.get(i).getName())) {
                no = i;
                break;
            }
        }
        if (no != -1 && no != 0) {
            Collections.swap(databaseList, no, 0);
        }
        return databaseList;
    }

    public static List<Schema> sortSchema(List<Schema> schemas, List<String> systemSchemas) {
        if (CollectionUtils.isEmpty(schemas)) {
            return schemas;
        }
        List<Schema> systemSchema = schemas.stream()
                .filter(schema -> systemSchemas.contains(schema.getName()) || "APEX_".startsWith(schema.getName())).collect(Collectors.toList());
        List<Schema> userSchema = schemas.stream()
                .filter(schema -> !systemSchemas.contains(schema.getName()) && !"APEX_".startsWith(schema.getName())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(userSchema)) {
            return schemas;
        }
        if (CollectionUtils.isEmpty(systemSchema)) {
            return userSchema;
        }
        return Stream.concat(userSchema.stream(), systemSchema.stream())
                .collect(Collectors.toList());
    }
}
