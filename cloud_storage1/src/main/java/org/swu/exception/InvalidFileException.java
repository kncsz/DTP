package org.swu.exception;

/**
 * 自定义异常：文件不合法
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException() {
        super("文件不合法，请检查文件格式或内容。");
    }

    public InvalidFileException(String message) {
        super(message);
    }

    // 异常链
    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
