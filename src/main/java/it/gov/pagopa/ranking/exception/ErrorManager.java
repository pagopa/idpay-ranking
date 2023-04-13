package it.gov.pagopa.ranking.exception;

import it.gov.pagopa.ranking.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
@Slf4j
public class ErrorManager {
    private static final ErrorDTO defaultErrorDTO;
    static {
        defaultErrorDTO =new ErrorDTO("Error", "Something gone wrong");
    }
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorDTO> handleException(RuntimeException error, HttpServletRequest request) {
        if(!(error instanceof ClientException clientException) || clientException.isPrintStackTrace() || clientException.getCause() != null){
            log.error("Something gone wrong handling request {}", getRequestDetails(request), error);
        } else {
            log.info("A {} occurred handling request {}: {} at {}", clientException.getClass().getSimpleName(), getRequestDetails(request), error.getMessage(), error.getStackTrace().length > 0 ? error.getStackTrace()[0] : "UNKNOWN");
        }

        if(error instanceof ClientExceptionNoBody clientExceptionNoBody){
            return ResponseEntity.status(clientExceptionNoBody.getHttpStatus()).build();
        }
        else {
            ErrorDTO errorDTO;
            HttpStatus httpStatus;
            if (error instanceof ClientExceptionWithBody clientExceptionWithBody){
                httpStatus=(clientExceptionWithBody).getHttpStatus();
                errorDTO = new ErrorDTO(clientExceptionWithBody.getCode(),  clientExceptionWithBody.getMessage());
            }
            else {
                httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
                errorDTO = defaultErrorDTO;
            }
            return ResponseEntity.status(httpStatus)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorDTO);
        }
    }

    private String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
