package com.portfolio.porfolio.utils;

public class HttpStatuses {
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_ACCEPTABLE = 406;
    /** HTTP 429 Too Many Requests status code for rate limiting */
    public static final int TOO_MANY_REQUESTS = 429;
}
