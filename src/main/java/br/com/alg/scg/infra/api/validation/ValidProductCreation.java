package br.com.alg.scg.infra.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidProductCreationValidator.class)
@Documented
public @interface ValidProductCreation {
    String message() default "Dados de criação do produto inválidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}