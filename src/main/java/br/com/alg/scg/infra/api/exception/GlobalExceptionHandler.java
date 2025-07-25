package br.com.alg.scg.infra.api.exception;

import br.com.alg.scg.infra.api.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        logger.error("Data integrity violation: {}", ex.getMessage(), ex);
        
        String message = "Erro de integridade dos dados";
        String path = request.getRequestURI();
        
        // Tratar duplicidade de chave única
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            if (ex.getMessage().contains("UK_product_name")) {
                message = "Já existe um produto com este nome";
            } else if (ex.getMessage().contains("UK_client_email")) {
                message = "Já existe um cliente com este e-mail";
            } else if (ex.getMessage().contains("UK_supplier_cnpj")) {
                message = "Já existe um fornecedor com este CNPJ";
            } else {
                message = "Registro duplicado não permitido";
            }
        }
        // Tratar violação de chave estrangeira
        else if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
            message = "Não é possível executar a operação. Existem registros relacionados";
        }
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            path
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.error("Validation error: {}", ex.getMessage(), ex);
        
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Dados de entrada inválidos",
            request.getRequestURI(),
            details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        logger.error("Constraint violation: {}", ex.getMessage(), ex);
        
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Violação de restrições de validação",
            request.getRequestURI(),
            details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        logger.error("Illegal argument: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        logger.error("Type mismatch: {}", ex.getMessage(), ex);
        
        String message = String.format("Parâmetro '%s' deve ser do tipo %s", 
            ex.getName(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido"
        );
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        logger.error("Message not readable: {}", ex.getMessage(), ex);
        
        String message = "Formato JSON inválido";
        if (ex.getMessage() != null && ex.getMessage().contains("enum")) {
            message = "Valor de enum inválido. Verifique os valores permitidos";
        }
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Erro interno do servidor. Tente novamente mais tarde",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}