package com.thushan.motorcycleservice.constant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private int status;
    private String message;
    private T body;

    public static <T> CommonResponse<T> success(int status, String message, T body) {
        return new CommonResponse<>(status, message, body);
    }

    public static <T> CommonResponse<T> error(int status, String message) {
        return new CommonResponse<>(status, message, null);
    }
}
