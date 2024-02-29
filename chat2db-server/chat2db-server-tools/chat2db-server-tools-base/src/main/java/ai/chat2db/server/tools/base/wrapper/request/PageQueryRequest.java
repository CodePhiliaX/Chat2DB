package ai.chat2db.server.tools.base.wrapper.request;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

/**
 * Parameters of paging query
 *
 * @author zhuangjiaju
 * @date 2021/06/26
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class PageQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * page number
     *
     * @mock 1
     */
    @NotNull(message = "Pagination page number cannot be empty")
    private Integer pageNo;
    /**
     * Number of pagination items
     *
     * @demo 10
     */
    @NotNull(message = "Paging size cannot be empty")
    @Range(min = 1, max = EasyToolsConstant.MAX_PAGE_SIZE,
        message = "Paging size must be between 1-" + EasyToolsConstant.MAX_PAGE_SIZE)
    private Integer pageSize;

    public PageQueryRequest() {
        this.pageNo = 1;
        this.pageSize = 10;
    }
}
