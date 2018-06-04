package media.dee.dcms.core.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * GraphDatabase Service to connect to graph database
 */
public interface GraphDatabaseService<R extends Record> {

    void run(String query, Map<String, Object> parameters, Consumer<R> consumer);
    List<R> run(String query, Map<String, Object> parameters);
    void fetchOne(String query, Map<String, Object> parameters, Consumer<R> consumer) throws NoSuchRecordException;
    <T> T fetchOne(String query, Map<String, Object> parameters, Function<R, T> mapper) throws NoSuchRecordException;
    R fetchOne(String query, Map<String, Object> parameters) throws NoSuchRecordException;

    /**
     * GraphNode an abstraction of GraphDatabase to get a node information retrieved by a query
     */
    interface GraphNode extends Map<String, Object> {
        Collection<String> getLabels();
        long getRawId();
        <T> T get(String key);
        boolean contains(String key);
        Set<String> keySet();
        Collection<Object> values();
    }
}
