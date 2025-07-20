package br.com.alg.scg.domain.common.valueobject;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Embeddable
public record Contact(String email, String phone) {

    // Regex simples para validação de e-mail. Pode ser mais complexa se necessário.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Construtor canônico é chamado automaticamente.
     * Ideal para validar os dados na criação do objeto.
     */
    public Contact {
        if (email != null && !email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        // Validações para telefone podem ser adicionadas aqui também.
        // Ex: if (phone != null && !isValidPhone(phone)) { ... }
    }

    /**
     * Construtor de conveniência para permitir a criação apenas com e-mail ou telefone.
     */
    public static Contact withEmail(String email) {
        return new Contact(email, null);
    }

    public static Contact withPhone(String phone) {
        return new Contact(null, phone);
    }
}
