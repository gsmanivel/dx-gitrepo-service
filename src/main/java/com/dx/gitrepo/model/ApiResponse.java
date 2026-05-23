package com.dx.gitrepo.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all dx-gitrepo-service endpoints.
 *
 * @param <T> the type of data in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Response status: success or error. */
    private String status;

    /** Human-readable message. */
    private String message;

    /** Response payload. */
    private T data;

    /** Total count for list responses. */
    private Integer count;

    /**
     * Creates a successful response with data.
     *
     * @param <T>  data type
     * @param data response payload
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(final T data) {
        return new ApiResponse<>("success", null, data, null);
    }

    /**
     * Creates a successful list response with count.
     *
     * @param <T>  list item type
     * @param data list of items
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<List<T>> successList(final List<T> data) {
        return new ApiResponse<>("success", null, data, data.size());
    }

    /**
     * Creates an error response.
     *
     * @param <T>     data type
     * @param message error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(final String message) {
        return new ApiResponse<>("error", message, null, null);
    }

    /**
     * Creates a custom response with all fields.
     *
     * @param <T>     data type
     * @param status  response status
     * @param message response message
     * @param data    response payload
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> of(final String status, final String message, final T data) {
        return new ApiResponse<>(status, message, data, null);
    }

}
