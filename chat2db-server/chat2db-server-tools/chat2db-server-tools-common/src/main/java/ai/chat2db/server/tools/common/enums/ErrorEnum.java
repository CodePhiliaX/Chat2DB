package ai.chat2db.server.tools.common.enums;

import ai.chat2db.server.tools.base.enums.BaseErrorEnum;

import lombok.Getter;

/**
 * 错误编码枚举
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum ErrorEnum implements BaseErrorEnum {
    /**
     * 当前数据源已经被关闭请重新打开数据源
     */
    DATA_SOURCE_NOT_FOUND,

    /**
     * 请先创建一个控制台服务
     */
    CONSOLE_NOT_FOUND,

    /**
     * 需要登录
     */
    NEED_LOGGED_IN,

    /**
     * 未登录
     */
    NOT_LOGGED_IN,

    /**
     * 重定向
     */
    REDIRECT,

    ;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.name();
    }

}
