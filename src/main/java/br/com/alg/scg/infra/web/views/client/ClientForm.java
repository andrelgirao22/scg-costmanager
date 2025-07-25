package br.com.alg.scg.infra.web.views.client;

import br.com.alg.scg.domain.common.valueobject.Address;
import br.com.alg.scg.domain.common.valueobject.Contact;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.ClientStatus;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

/**
 * Formulário para edição de clientes
 */
public class ClientForm extends FormLayout {

    private TextField name = new TextField("Nome");
    private EmailField email = new EmailField("Email");
    private TextField phone = new TextField("Telefone");
    private Select<ClientStatus> status = new Select<>();
    
    // Campos do endereço
    private TextField street = new TextField("Rua");
    private TextField city = new TextField("Cidade");
    private TextField postalCode = new TextField("CEP");

    private Button save = new Button("Salvar");
    private Button delete = new Button("Excluir");
    private Button close = new Button("Fechar");

    private Binder<ClientWrapper> binder = new BeanValidationBinder<>(ClientWrapper.class);
    private Client client;

    public ClientForm() {
        addClassName("client-form");

        configureFields();
        add(
            name, 
            email, 
            phone, 
            status,
            new H3("Endereço de Entrega"),
            street,
            city,
            postalCode,
            createButtonsLayout()
        );
        configureBinder();
    }

    private void configureFields() {
        name.setPlaceholder("Digite o nome do cliente");
        name.setRequired(true);
        
        email.setPlaceholder("cliente@email.com");
        email.setRequired(true);
        
        phone.setPlaceholder("(11) 99999-9999");
        phone.setRequired(true);
        
        status.setLabel("Status");
        status.setItems(ClientStatus.values());
        status.setItemLabelGenerator(clientStatus -> switch (clientStatus) {
            case ACTIVE -> "Ativo";
            case INACTIVE -> "Inativo";
            case BLOCKED -> "Bloqueado";
        });
        status.setValue(ClientStatus.ACTIVE);
        
        // Campos de endereço opcionais
        street.setPlaceholder("Rua, número, complemento");
        city.setPlaceholder("Cidade");
        postalCode.setPlaceholder("00000-000");
    }

    private void configureBinder() {
        binder.forField(name).bind(ClientWrapper::getName, ClientWrapper::setName);
        binder.forField(email).bind(ClientWrapper::getEmail, ClientWrapper::setEmail);
        binder.forField(phone).bind(ClientWrapper::getPhone, ClientWrapper::setPhone);
        binder.forField(status).bind(ClientWrapper::getStatus, ClientWrapper::setStatus);
        binder.forField(street).bind(ClientWrapper::getStreet, ClientWrapper::setStreet);
        binder.forField(city).bind(ClientWrapper::getCity, ClientWrapper::setCity);
        binder.forField(postalCode).bind(ClientWrapper::getPostalCode, ClientWrapper::setPostalCode);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, client)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void setClient(Client client) {
        this.client = client;
        
        ClientWrapper wrapper = new ClientWrapper();
        if (client != null) {
            wrapper.setName(client.getName());
            wrapper.setStatus(client.getStatus());
            
            if (client.getContact() != null) {
                wrapper.setEmail(client.getContact().email());
                wrapper.setPhone(client.getContact().phone());
            }
            
            if (client.getDeliveryAddress() != null) {
                wrapper.setStreet(client.getDeliveryAddress().street());
                wrapper.setCity(client.getDeliveryAddress().city());
                wrapper.setPostalCode(client.getDeliveryAddress().postalCode());
            }
        }
        
        binder.readBean(wrapper);
        delete.setVisible(client != null && client.getId() != null);
    }

    private void validateAndSave() {
        try {
            ClientWrapper wrapper = new ClientWrapper();
            binder.writeBean(wrapper);
            
            // Criar Contact
            Contact contact = new Contact(wrapper.getEmail(), wrapper.getPhone());
            
            // Criar Address (opcional)
            Address address = null;
            if (wrapper.getStreet() != null && !wrapper.getStreet().trim().isEmpty()) {
                address = new Address(wrapper.getStreet(), wrapper.getCity(), wrapper.getPostalCode());
            }
            
            // Converter wrapper para Client
            if (client == null || client.getId() == null) {
                // Novo cliente - usar factory method
                client = Client.createFromForm(
                    wrapper.getName(), 
                    contact, 
                    address, 
                    wrapper.getStatus()
                );
            } else {
                // Cliente existente - atualizar campos
                client.setName(wrapper.getName());
                client.setContact(contact);
                client.setStatus(wrapper.getStatus());
                client.setDeliveryAddress(address);
            }
            
            fireEvent(new SaveEvent(this, client));
        } catch (ValidationException e) {
            // Validação já é mostrada nos campos
        }
    }

    // Wrapper class para binding
    public static class ClientWrapper {
        private String name;
        private String email;
        private String phone;
        private ClientStatus status;
        private String street;
        private String city;
        private String postalCode;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public ClientStatus getStatus() { return status; }
        public void setStatus(ClientStatus status) { this.status = status; }
        
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }

    // Events
    public static abstract class ClientFormEvent extends ComponentEvent<ClientForm> {
        private Client client;

        protected ClientFormEvent(ClientForm source, Client client) {
            super(source, false);
            this.client = client;
        }

        public Client getClient() {
            return client;
        }
    }

    public static class SaveEvent extends ClientFormEvent {
        SaveEvent(ClientForm source, Client client) {
            super(source, client);
        }
    }

    public static class DeleteEvent extends ClientFormEvent {
        DeleteEvent(ClientForm source, Client client) {
            super(source, client);
        }
    }

    public static class CloseEvent extends ClientFormEvent {
        CloseEvent(ClientForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}