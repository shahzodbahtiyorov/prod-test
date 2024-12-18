package org.finance.helpers;

public  class NetworkError {
    private int code;
    private String message;
    private Object data;

    public NetworkError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
