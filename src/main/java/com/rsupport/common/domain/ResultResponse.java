package com.rsupport.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ResultResponse<T> {
    private HttpStatus statusCode;
    private String resultMessage;
    private T resultObject;

    public ResultResponse(HttpStatus statusCode, String resultMessage) {
        this.statusCode = statusCode;
        this.resultMessage = resultMessage;
        this.resultObject = null;
    }

    public static<T> ResultResponse<T> res(final HttpStatus statusCode, final String resultMessage) {
        return res(statusCode, resultMessage, null);
    }

    public static<T> ResultResponse<T> res(final HttpStatus statusCode, final String resultMessage, final T resultObject) {
        return new ResultResponse<>(statusCode, resultMessage, resultObject);
    }
}
