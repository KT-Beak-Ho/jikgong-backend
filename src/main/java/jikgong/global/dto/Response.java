package jikgong.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private T data;
    private String message;

    public Response(String message) {
        this.message = message;
    }
}
