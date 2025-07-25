package br.com.alg.scg.infra.api.exception;

import br.com.alg.scg.infra.api.controllers.ProductController;
import br.com.alg.scg.infra.api.dto.common.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice(assignableTypes = ProductController.class)
public class ProductControllerAdvice {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductControllerAdvice.class);
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleProductDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        logger.error("Product data integrity violation: {}", ex.getMessage(), ex);
        
        String message = "Erro de integridade dos dados do produto";
        
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("UK_product_name")) {
                message = "Já existe um produto cadastrado com este nome. Escolha um nome diferente.";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Não é possível excluir este produto pois ele está sendo usado em vendas, compras ou receitas.";
            }
        }
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleProductJsonError(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        logger.error("Product JSON parsing error: {}", ex.getMessage(), ex);
        
        String message = "Dados JSON inválidos para produto";
        
        Throwable cause = ex.getCause();
        if (cause instanceof JsonParseException) {
            message = "JSON malformado. Verifique a sintaxe (vírgulas, chaves, aspas).";
        } else if (cause instanceof MismatchedInputException mismatchedEx) {
            String fieldName = getFieldName(mismatchedEx);
            if ("type".equals(fieldName)) {
                message = "Campo 'type' é obrigatório. Valores permitidos: RAW_MATERIAL, FINAL_PRODUCT";
            } else if ("name".equals(fieldName)) {
                message = "Campo 'name' é obrigatório e deve ser uma string válida";
            } else if ("initial_stock".equals(fieldName)) {
                message = "Campo 'initial_stock' deve ser um número decimal válido";
            } else {
                message = String.format("Campo obrigatório ausente ou inválido: %s", fieldName);
            }
        } else if (cause instanceof InvalidFormatException invalidEx) {
            String fieldName = getFieldName(invalidEx);
            if ("type".equals(fieldName)) {
                message = "Tipo de produto inválido. Use: RAW_MATERIAL (matéria-prima) ou FINAL_PRODUCT (produto final)";
            } else if ("initial_stock".equals(fieldName)) {
                message = "Estoque inicial deve ser um número decimal (ex: 10.5, 100.0)";
            } else {
                message = String.format("Formato inválido para o campo '%s'", fieldName);
            }
        }
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleProductValidationError(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.error("Product validation error: {}", ex.getMessage(), ex);
        
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String field = error.getField();
                    String defaultMessage = error.getDefaultMessage();
                    
                    // Personalizar mensagens para campos específicos de produto
                    if ("name".equals(field)) {
                        return "Nome: " + (defaultMessage != null ? defaultMessage : "é obrigatório");
                    } else if ("type".equals(field)) {
                        return "Tipo: " + (defaultMessage != null ? defaultMessage : "é obrigatório (RAW_MATERIAL ou FINAL_PRODUCT)");
                    } else if ("initialStock".equals(field)) {
                        return "Estoque inicial: " + (defaultMessage != null ? defaultMessage : "deve ser maior ou igual a zero");
                    }
                    
                    return field + ": " + defaultMessage;
                })
                .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Dados do produto inválidos",
            request.getRequestURI(),
            details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleProductIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        logger.error("Product illegal argument: {}", ex.getMessage(), ex);
        
        String message = ex.getMessage();
        
        // Personalizar mensagens de erro específicas do ProductService
        if (message != null) {
            if (message.contains("Nome do produto não pode ser vazio")) {
                message = "Nome do produto é obrigatório e não pode estar vazio";
            } else if (message.contains("Tipo do produto não pode ser nulo")) {
                message = "Tipo do produto é obrigatório. Use RAW_MATERIAL ou FINAL_PRODUCT";
            } else if (message.contains("Estoque inicial não pode ser negativo")) {
                message = "Estoque inicial deve ser maior ou igual a zero";
            } else if (message.contains("Produto não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.create(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Produto não encontrado",
                        request.getRequestURI()
                    )
                );
            }
        }
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message != null ? message : "Erro nos dados do produto",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleProductIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        
        logger.error("Product illegal state: {}", ex.getMessage(), ex);
        
        String message = ex.getMessage();
        
        // Tratar erros de estado específicos de produtos
        if (message != null) {
            if (message.contains("Apenas matérias-primas controlam estoque")) {
                message = "Operação de estoque não permitida. Apenas matérias-primas controlam estoque.";
            } else if (message.contains("Apenas produtos finais podem ter uma receita")) {
                message = "Receitas só podem ser definidas para produtos finais, não para matérias-primas.";
            } else if (message.contains("margem de lucro só pode ser definida para produtos finais")) {
                message = "Margem de lucro só pode ser definida para produtos finais.";
            }
        }
        
        ErrorResponse error = ErrorResponse.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message != null ? message : "Estado inválido do produto",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    private String getFieldName(JsonMappingException ex) {
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            return ex.getPath().get(0).getFieldName();
        }
        return "campo desconhecido";
    }
}