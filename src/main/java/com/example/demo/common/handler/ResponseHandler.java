package com.example.demo.common.handler;

import com.example.demo.common.dto.Response;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Getter
@Setter
public class ResponseHandler {
    public static <T> Response<T> createSuccessResponse(String message, T data) {
        return new Response<>(true, message, data);
    }

    public static <T> Mono<Response<T>> handleErrorResponse(String action, Throwable e) {
        return Mono.just(new Response<T>(false, "Error " + action + ": " + e.getMessage(), null));
    }
}
