package cn.parzulpan.exception;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义异常类
 */

public class CustomException extends Exception{
    private String message; // 异常信息

    public CustomException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
