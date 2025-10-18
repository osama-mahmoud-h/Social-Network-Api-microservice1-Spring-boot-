package semsem.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> MyApiResponse<T> success(T data, String message) {
        return MyApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> MyApiResponse<T> error(String message) {
        return MyApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}