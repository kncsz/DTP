package org.swu.exception;

/**
 * 自定义异常：IP访问次数太频繁
 */
public class IpAccessTooFrequentException extends RuntimeException {

    public IpAccessTooFrequentException() {
        super("IP访问次数太频繁，请稍后再试。");
    }

    public IpAccessTooFrequentException(String message) {
        super(message);
    }

    // 异常链
    public IpAccessTooFrequentException(String message, Throwable cause) {
        super(message, cause);
    }
}
