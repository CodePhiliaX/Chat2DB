package ai.chat2db.server.tools.base.wrapper;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Service layer pagination wrapper
 * Contains only core pagination fields without Result semantics
 *
 * @param <T> data type
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class ServicePage<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Data list
     */
    private List<T> data;

    /**
     * Total count
     */
    private Long total;

    /**
     * Page number
     */
    private Integer pageNo;

    /**
     * Page size
     */
    private Integer pageSize;

    /**
     * Last document ID for cursor-based pagination
     */
    private Integer lastDocId;

    public ServicePage(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        this.data = data;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public ServicePage(List<T> data, Long total, Integer pageNo, Integer pageSize, Integer lastDocId) {
        this.data = data;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.lastDocId = lastDocId;
    }

    /**
     * Create a ServicePage from data, total and query params
     */
    public static <T> ServicePage<T> of(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        return new ServicePage<>(data, total, pageNo, pageSize);
    }

    /**
     * Create a ServicePage with lastDocId support
     */
    public static <T> ServicePage<T> of(List<T> data, Long total, Integer pageNo, Integer pageSize, Integer lastDocId) {
        return new ServicePage<>(data, total, pageNo, pageSize, lastDocId);
    }

    /**
     * Create an empty ServicePage
     */
    public static <T> ServicePage<T> empty(Integer pageNo, Integer pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    /**
     * Check if data is not empty
     */
    public boolean isNotEmpty() {
        return data != null && !data.isEmpty();
    }
}
