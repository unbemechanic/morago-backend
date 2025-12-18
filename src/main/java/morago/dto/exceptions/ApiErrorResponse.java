package morago.dto.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static ApiErrorResponse of(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.status = status.value();
        response.error = status.name();
        response.message = message;
        response.path = path;
        return response;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

