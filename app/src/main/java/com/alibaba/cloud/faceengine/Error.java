package com.alibaba.cloud.faceengine;

public class Error {
    public static final int OK = 0;
    public static final int FAILED = -1;
    public static final int ERROR_EXPIRE = -2;
    public static final int ERROR_AUTH_FAIL = -3;
    public static final int ERROR_INVALID_ARGUMENT = -4;
    public static final int ERROR_DB_EXEC = -5;
    public static final int ERROR_EXISTED = -6;
    public static final int ERROR_NOT_EXIST = -7;
    public static final int ERROR_NETWORK_FAIL = -8;
    public static final int ERROR_NETWORK_RECV_JSON_WRONG = -9;
    public static final int ERROR_NO_FACE = -10;
    public static final int ERROR_FORMAT_NOT_SUPPORT = -11;
    public static final int ERROR_NO_ID = -12;

    public static final int ERROR_CLOUD_OK = OK;
    public static final int ERROR_CLOUD_ACCOUT_WRONG = -20;
    public static final int ERROR_CLOUD_REQUEST_DATA_ERROR = -21;
    public static final int ERROR_CLOUD_DB_EXEC_ERROR = -22;
    public static final int ERROR_CLOUD_EXISTED_ERROR = -23;
    public static final int ERROR_CLOUD_NOT_EXIST_ERROR = -24;
    public static final int ERROR_CLOUD_NO_AUTHORIZE = -25;
    public static final int ERROR_CLOUD_ALGORITHOM_ERROR = -26;
    public static final int ERROR_CLOUD_NO_FACE = -27;
    public static final int ERROR_CLOUD_FAILED = -28;
    public static final int ERROR_CLOUD_NOT_SUPPORT = -29;
}
