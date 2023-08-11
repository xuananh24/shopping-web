package com.example.userservice.common.constant;

public class PathConstant {
    public static final String API_BASE_URL = "/api/user-service";
    public static final String API_USER_CREATE_URL = API_BASE_URL + "/user/register";
    public static final String API_USER_SEND_VERIFY_CODE_URL = API_BASE_URL + "/user/send-code/{userId}";
    public static final String API_USER_VERIFY_CODE_URL = API_BASE_URL + "/user/verify-code/{userId}/{code}";

    public static final String API_USER_LOGIN_URL = API_BASE_URL + "/user/login";
    public static final String API_USER_GET_TOKEN_URL = API_BASE_URL + "/get-token";
}
