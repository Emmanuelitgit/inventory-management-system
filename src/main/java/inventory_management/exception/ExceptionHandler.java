package inventory_management.exception;

import inventory_management.dto.ResponseDTO;
import inventory_management.utils.AppUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class ExceptionHandler {
    // global exception for not found cases
    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    ResponseEntity<Object> handleNotFoundException(NotFoundException exception){
        ResponseDTO response = AppUtils.getResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(404));
    }

    // global exception for bad request cases
    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    ResponseEntity<Object> handleBadRequestException(BadRequestException exception){
        ResponseDTO response = AppUtils.getResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(400));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnAuthorizeException.class)
    ResponseEntity<ResponseDTO> handleUnAuthorizeException(UnAuthorizeException exception){
        ResponseDTO response = AppUtils.getResponseDto(exception.getMessage(), HttpStatus.valueOf(401));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(401));
    }

}
