package br.com.alg.scg.infra.web.views.sale;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.domain.common.valueobject.Address;
import br.com.alg.scg.domain.common.valueobject.Contact;
import br.com.alg.scg.domain.sales.entity.Client;
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
 * Dialog para cadastro de novos clientes
 */
public class ClientDialog extends Dialog {

    private final ClientService clientService;
    private final Consumer<Client> onClientCreated;

    private TextField nameField;
    private EmailField emailField;
    private TextField phoneField;
    private TextField streetField;
    private TextField cityField;
    private TextField postalCodeField;
    private Button saveButton;
    private Button cancelButton;

    private Binder<ClientData> binder = new Binder<>(ClientData.class);

    public ClientDialog(ClientService clientService, Consumer<Client> onClientCreated) {
        this.clientService = clientService;
        this.onClientCreated = onClientCreated;
        
        createDialog();
        configureForm();
        bindFields();
    }

    private void createDialog() {
        setModal(true);
        setDraggable(true);
        setResizable(false);
        setWidth("600px");
        setHeaderTitle("Novo Cliente");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        H3 title = new H3("Cadastrar Novo Cliente");
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

        nameField = new TextField("Nome do Cliente");
        nameField.setRequired(true);
        nameField.setPlaceholder("Ex: João Silva");

        emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setPlaceholder("joao@email.com");

        phoneField = new TextField("Telefone");
        phoneField.setRequired(true);
        phoneField.setPlaceholder("(11) 99999-9999");

        // Campos de endereço
        streetField = new TextField("Rua/Endereço");
        streetField.setPlaceholder("Rua das Flores, 123");

        cityField = new TextField("Cidade");
        cityField.setPlaceholder("São Paulo");

        postalCodeField = new TextField("CEP");
        postalCodeField.setPlaceholder("00000-000");
        postalCodeField.setMaxLength(9);

        formLayout.add(nameField, 2);
        formLayout.add(emailField, 1);
        formLayout.add(phoneField, 1);
        formLayout.add(streetField, 2);
        formLayout.add(cityField, 1);
        formLayout.add(postalCodeField, 1);

        return formLayout;
    }

    private HorizontalLayout createButtons() {
        cancelButton = new Button("Cancelar");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Salvar");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveClient());

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
        buttons.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        buttons.setWidthFull();
        
        return buttons;
    }

    private void configureForm() {
        // Configurar campos com validações em tempo real
        nameField.addValueChangeListener(e -> validateForm());
        emailField.addValueChangeListener(e -> validateForm());
        phoneField.addValueChangeListener(e -> validateForm());
    }

    private void bindFields() {
        binder.forField(nameField)
            .withValidator(new StringLengthValidator("Nome deve ter entre 2 e 100 caracteres", 2, 100))
            .bind(ClientData::getName, ClientData::setName);

        binder.forField(emailField)
            .withValidator(new EmailValidator("Email deve ser válido"))
            .bind(ClientData::getEmail, ClientData::setEmail);

        binder.forField(phoneField)
            .withValidator(new StringLengthValidator("Telefone deve ter entre 8 e 15 caracteres", 8, 15))
            .bind(ClientData::getPhone, ClientData::setPhone);

        binder.forField(streetField)
            .bind(ClientData::getStreet, ClientData::setStreet);

        binder.forField(cityField)
            .bind(ClientData::getCity, ClientData::setCity);

        binder.forField(postalCodeField)
            .bind(ClientData::getPostalCode, ClientData::setPostalCode);
    }

    private void validateForm() {
        boolean isValid = !nameField.getValue().trim().isEmpty() &&
                         !emailField.getValue().trim().isEmpty() &&
                         !phoneField.getValue().trim().isEmpty();
        
        saveButton.setEnabled(isValid);
    }

    private void saveClient() {
        try {
            ClientData clientData = new ClientData();
            binder.writeBean(clientData);

            Contact contact = new Contact(clientData.getEmail(), clientData.getPhone());
            
            Address address = null;
            if (!clientData.getStreet().trim().isEmpty() && 
                !clientData.getCity().trim().isEmpty() && 
                !clientData.getPostalCode().trim().isEmpty()) {
                address = new Address(clientData.getStreet(), clientData.getCity(), clientData.getPostalCode());
            }

            Client client = clientService.createClient(clientData.getName(), contact, address);

            showSuccess("Cliente cadastrado com sucesso!");
            onClientCreated.accept(client);
            close();

        } catch (ValidationException e) {
            showError("Por favor, corrija os erros no formulário");
        } catch (Exception e) {
            showError("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    public void openDialog() {
        clearForm();
        validateForm();
        open();
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        streetField.clear();
        cityField.clear();
        postalCodeField.clear();
        binder.setBean(new ClientData());
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
    public static class ClientData {
        private String name = "";
        private String email = "";
        private String phone = "";
        private String street = "";
        private String city = "";
        private String postalCode = "";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }
}