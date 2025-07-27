package br.com.alg.scg.infra.web.views.purchase;

import br.com.alg.scg.application.service.SupplierService;
import br.com.alg.scg.domain.common.valueobject.Contact;
import br.com.alg.scg.domain.purchases.entity.Supplier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;

import java.util.function.Consumer;

/**
 * Dialog para cadastro de novos fornecedores
 */
public class SupplierDialog extends Dialog {

    private final SupplierService supplierService;
    private final Consumer<Supplier> onSupplierCreated;

    private TextField nameField;
    private TextField documentField;
    private EmailField emailField;
    private TextField phoneField;
    private Button saveButton;
    private Button cancelButton;

    private Binder<SupplierData> binder = new Binder<>(SupplierData.class);

    public SupplierDialog(SupplierService supplierService, Consumer<Supplier> onSupplierCreated) {
        this.supplierService = supplierService;
        this.onSupplierCreated = onSupplierCreated;
        
        createDialog();
        configureForm();
        bindFields();
    }

    private void createDialog() {
        setModal(true);
        setDraggable(true);
        setResizable(false);
        setWidth("500px");
        setHeaderTitle("Novo Fornecedor");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        H3 title = new H3("Cadastrar Novo Fornecedor");
        title.getStyle().set("margin", "0");

        FormLayout formLayout = createForm();
        HorizontalLayout buttons = createButtons();

        layout.add(formLayout, buttons);
        add(layout);
    }

    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("400px", 2)
        );

        nameField = new TextField("Nome do Fornecedor");
        nameField.setRequired(true);
        nameField.setPlaceholder("Ex: Distribuidora ABC");

        documentField = new TextField("CNPJ");
        documentField.setRequired(true);
        documentField.setPlaceholder("00.000.000/0000-00");
        documentField.setMaxLength(18);

        emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setPlaceholder("contato@fornecedor.com");

        phoneField = new TextField("Telefone");
        phoneField.setRequired(true);
        phoneField.setPlaceholder("(11) 99999-9999");

        formLayout.add(nameField, 2);
        formLayout.add(documentField, 1);
        formLayout.add(emailField, 1);
        formLayout.add(phoneField, 2);

        return formLayout;
    }

    private HorizontalLayout createButtons() {
        cancelButton = new Button("Cancelar");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Salvar");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveSupplier());

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
        buttons.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        buttons.setWidthFull();
        
        return buttons;
    }

    private void configureForm() {
        // Configurar campos com validações em tempo real
        nameField.addValueChangeListener(e -> validateForm());
        documentField.addValueChangeListener(e -> validateForm());
        emailField.addValueChangeListener(e -> validateForm());
        phoneField.addValueChangeListener(e -> validateForm());
    }

    private void bindFields() {
        binder.forField(nameField)
            .withValidator(new StringLengthValidator("Nome deve ter entre 2 e 100 caracteres", 2, 100))
            .bind(SupplierData::getName, SupplierData::setName);

        binder.forField(documentField)
            .withValidator(new StringLengthValidator("CNPJ deve ter entre 11 e 18 caracteres", 11, 18))
            .bind(SupplierData::getDocument, SupplierData::setDocument);

        binder.forField(emailField)
            .withValidator(new EmailValidator("Email deve ser válido"))
            .bind(SupplierData::getEmail, SupplierData::setEmail);

        binder.forField(phoneField)
            .withValidator(new StringLengthValidator("Telefone deve ter entre 8 e 15 caracteres", 8, 15))
            .bind(SupplierData::getPhone, SupplierData::setPhone);
    }

    private void validateForm() {
        boolean isValid = !nameField.getValue().trim().isEmpty() &&
                         !documentField.getValue().trim().isEmpty() &&
                         !emailField.getValue().trim().isEmpty() &&
                         !phoneField.getValue().trim().isEmpty();
        
        saveButton.setEnabled(isValid);
    }

    private void saveSupplier() {
        try {
            SupplierData supplierData = new SupplierData();
            binder.writeBean(supplierData);

            Contact contact = new Contact(supplierData.getEmail(), supplierData.getPhone());
            Supplier supplier = supplierService.createSupplier(
                supplierData.getName(),
                supplierData.getDocument(),
                contact
            );

            showSuccess("Fornecedor cadastrado com sucesso!");
            onSupplierCreated.accept(supplier);
            close();

        } catch (ValidationException e) {
            showError("Por favor, corrija os erros no formulário");
        } catch (Exception e) {
            showError("Erro ao cadastrar fornecedor: " + e.getMessage());
        }
    }

    public void openDialog() {
        clearForm();
        validateForm();
        open();
    }

    private void clearForm() {
        nameField.clear();
        documentField.clear();
        emailField.clear();
        phoneField.clear();
        binder.setBean(new SupplierData());
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    // Classe auxiliar para binding
    public static class SupplierData {
        private String name = "";
        private String document = "";
        private String email = "";
        private String phone = "";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDocument() { return document; }
        public void setDocument(String document) { this.document = document; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}