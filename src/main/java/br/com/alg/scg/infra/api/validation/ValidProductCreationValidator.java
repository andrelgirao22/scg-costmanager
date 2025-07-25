package br.com.alg.scg.infra.api.validation;

import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.infra.api.dto.product.CreateProductDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidProductCreationValidator implements ConstraintValidator<ValidProductCreation, CreateProductDTO> {
    
    @Override
    public void initialize(ValidProductCreation constraintAnnotation) {
        // Inicialização se necessário
    }
    
    @Override
    public boolean isValid(CreateProductDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Deixa a validação @NotNull cuidar disso
        }
        
        // Desabilita a mensagem padrão
        context.disableDefaultConstraintViolation();
        
        boolean isValid = true;
        
        // Validar se initial_stock é obrigatório para RAW_MATERIAL
        if (dto.type() == ProductType.RAW_MATERIAL) {
            if (dto.initialStock() == null) {
                context.buildConstraintViolationWithTemplate(
                    "Estoque inicial é obrigatório para matérias-primas")
                    .addPropertyNode("initialStock")
                    .addConstraintViolation();
                isValid = false;
            }
        }
        
        // Validar se initial_stock não deve ser informado para FINAL_PRODUCT  
        if (dto.type() == ProductType.FINAL_PRODUCT) {
            if (dto.initialStock() != null && dto.initialStock().compareTo(BigDecimal.ZERO) != 0) {
                context.buildConstraintViolationWithTemplate(
                    "Produtos finais sempre têm estoque zero (são feitos sob demanda)")
                    .addPropertyNode("initialStock")
                    .addConstraintViolation();
                isValid = false;
            }
        }
        
        return isValid;
    }
}