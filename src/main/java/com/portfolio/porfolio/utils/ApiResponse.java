package com.portfolio.porfolio.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @description Standardized API response wrapper.
 * @param <T> The type of the response data.
 * @success Indicates whether the API response represents a successful operation.
 * @message The message associated with the API response.
 * @data The response data of type T.
 */
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;

    public ApiResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * @description Creates a successful API response with the given message and data.
     * @param <T> The type of the response data.
     * @param message The success message.
     * @param data The response data.
     * @return A successful ApiResponse instance containing the message and data.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), message, data);
    }

    /**
     * @description Creates an error API response with the given message.
     * @param <T> The type of the response data.
     * @param message The error message.
     * @return An ApiResponse instance representing the error.
     */
    public static <T> ApiResponse<Object> error(String message) {
        return new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

        /**
     * @description Creates a successful API response with the given message and data.
     * @param <T> The type of the response data.
     * @param message The success message.
     * @param data The response data.
     * @return A successful ApiResponse instance containing the message and data.
     */
    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }

    /**
     * @description Creates an error API response with the given message.
     * @param <T> The type of the response data.
     * @param message The error message.
     * @return An ApiResponse instance representing the error.
     */
    public static <T> ApiResponse<Object> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    /**
     * @description Converts the ApiResponse instance to a ResponseEntity.
     * @return A ResponseEntity containing the ApiResponse instance.
     */
    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return ResponseEntity.status(this.code).body(this);
    }

    public boolean isSuccess() { return success; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
