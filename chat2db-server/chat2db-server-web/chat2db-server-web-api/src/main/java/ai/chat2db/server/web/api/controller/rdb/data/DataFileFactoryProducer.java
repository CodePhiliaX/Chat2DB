package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.web.api.controller.rdb.data.csv.CSVImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.json.JSONImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.observer.LoggingObserver;
import ai.chat2db.server.web.api.controller.rdb.data.observer.Observer;
import ai.chat2db.server.web.api.controller.rdb.data.sql.SQLImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.xls.XLSImportExportFactory;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.XLSXImportExportFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:51
 */
public class DataFileFactoryProducer {

    private static final ThreadLocal<Observer> observerThreadLocal = ThreadLocal.withInitial(LoggingObserver::new);

    public static void removeObserver() {
        observerThreadLocal.remove();
    }

    public static void notifyInfo(String log) {
        getObserver().notifyInfo(log);
    }

    public static void notifyError(String log) {
        getObserver().notifyError(log);
    }

    public static Observer getObserver() {
        return observerThreadLocal.get();
    }


    public static final Map<String, DataFileImportExportFactory> factories = new HashMap<>();

    static {
        factories.put("CSV", new CSVImportExportFactory());
        factories.put("XLSX", new XLSXImportExportFactory());
        factories.put("XLS", new XLSImportExportFactory());
        factories.put("JSON", new JSONImportExportFactory());
        factories.put("SQL", new SQLImportExportFactory());
    }

    public static DataFileImportExportFactory getFactory(String type) {
        return factories.get(type);
    }
}

