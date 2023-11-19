package jikgong.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Response<T> {
    private T data;
    private String message;

    public Response(String message) {
        this.message = message;
    }
}
