package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.web.api.controller.rdb.data.csv.CSVImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.json.JSONImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.observer.DataExportObserver;
import ai.chat2db.server.web.api.controller.rdb.data.observer.LoggingDataExportObserver;
import ai.chat2db.server.web.api.controller.rdb.data.sql.SQLImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.XLSXImportExportFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:51
 */
public class DataFileFactoryProducer {

    private static final ThreadLocal<DataExportObserver> observerThreadLocal = ThreadLocal.withInitial(LoggingDataExportObserver::new);

    public static void addObserver(DataExportObserver observer) {
        observerThreadLocal.set(observer);
    }

    public static void removeObserver() {
        observerThreadLocal.remove();
    }

    public static void notifyObservers(String log) {
        DataExportObserver observer = observerThreadLocal.get();
        observer.onDataExported(log);
    }

    public static final Map<String, DataFileImportExportFactory> factories = new HashMap<>();

    static {
        factories.put("CSV", new CSVImportExportFactory());
        factories.put("XLSX", new XLSXImportExportFactory());
        factories.put("JSON", new JSONImportExportFactory());
        factories.put("SQL", new SQLImportExportFactory());
    }

    public static DataFileImportExportFactory getFactory(String type) {
        return factories.get(type);
    }
}

