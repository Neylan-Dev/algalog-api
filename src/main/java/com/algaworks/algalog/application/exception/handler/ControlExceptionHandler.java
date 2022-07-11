package com.algaworks.algalog.application.exception.handler;

import com.algaworks.algalog.application.exception.BusinessException;
import com.algaworks.algalog.domain.enums.DataForBusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@ControllerAdvice
public class ControlExceptionHandler {

    public static final String INVALID_INPUT = "Entrada de dados inválida";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> fieldErrorDtos = fieldErrors.stream()
                .map(f -> f.getField().concat(":").concat(requireNonNull(f.getDefaultMessage()))).map(String::new)
                .collect(Collectors.toList());

        var businessException = DataForBusinessException.INVALID_INPUT
                .asBusinessExceptionWithDescription(fieldErrorDtos.toString());

        return ResponseEntity.status(businessException.getHttpStatusCode()).body(businessException.getOnlyBody());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {

        var ex = DataForBusinessException.ILLEGAL_ARGUMENT_EXCEPTION
                .asBusinessExceptionWithDescription(illegalArgumentException.getLocalizedMessage());

        return ResponseEntity.status(ex.getHttpStatusCode()).body(ex.getOnlyBody());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException ex) {

        return ResponseEntity.status(ex.getHttpStatusCode()).body(ex.getOnlyBody());
    }


}