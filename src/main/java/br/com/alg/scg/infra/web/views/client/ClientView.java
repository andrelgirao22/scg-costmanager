package br.com.alg.scg.infra.web.views.client;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.ClientStatus;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

/**
 * View para gerenciamento de clientes
 */
@PageTitle("Clientes")
@Route(value = "clients", layout = MainLayout.class)
public class ClientView extends Main {

    private final ClientService clientService;
    private Grid<Client> grid = new Grid<>(Client.class, false);
    private TextField filterText = new TextField();
    private ClientForm form;

    @Autowired
    public ClientView(ClientService clientService) {
        this.clientService = clientService;
        
        addClassName("client-view");
        setSizeFull();
        
        configureGrid();
        configureForm();
        
        add(createHeader(), createToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private Component createHeader() {
        H2 header = new H2("Gerenciar Clientes");
        header.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);
        return header;
    }

    private Component createToolbar() {
        filterText.setPlaceholder("Filtrar por nome ou email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addClientButton = new Button("Novo Cliente");
        addClientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClientButton.setIcon(VaadinIcon.PLUS.create());
        addClientButton.addClickListener(click -> addClient());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addClientButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("client-grid");
        grid.setSizeFull();
        
        grid.addColumn(Client::getName).setHeader("Nome").setSortable(true);
        grid.addColumn(client -> client.getContact().email()).setHeader("Email").setSortable(true);
        grid.addColumn(client -> client.getContact().phone()).setHeader("Telefone");
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge)).setHeader("Status");
        grid.addColumn(client -> client.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
             .setHeader("Cadastro").setSortable(true);
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        grid.asSingleSelect().addValueChangeListener(event -> editClient(event.getValue()));
    }

    private Component createStatusBadge(Client client) {
        Span badge = new Span(getStatusText(client.getStatus()));
        badge.getElement().getThemeList().add("badge");
        
        if (client.getStatus() == ClientStatus.ACTIVE) {
            badge.getElement().getThemeList().add("success");
        } else {
            badge.getElement().getThemeList().add("error");
        }
        
        return badge;
    }

    private String getStatusText(ClientStatus status) {
        return switch (status) {
            case ACTIVE -> "Ativo";
            case INACTIVE -> "Inativo";
            case BLOCKED -> "Bloqueado";
        };
    }

    private void configureForm() {
        form = new ClientForm();
        form.setWidth("25em");
        
        form.addSaveListener(this::saveClient);
        form.addDeleteListener(this::deleteClient);
        form.addCloseListener(e -> closeEditor());
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void saveClient(ClientForm.SaveEvent event) {
        try {
            clientService.save(event.getClient());
            updateList();
            closeEditor();
        } catch (Exception e) {
            // TODO: Implementar notificação de erro
            e.printStackTrace();
        }
    }

    private void deleteClient(ClientForm.DeleteEvent event) {
        try {
            clientService.deleteById(event.getClient().getId());
            updateList();
            closeEditor();
        } catch (Exception e) {
            // TODO: Implementar notificação de erro
            e.printStackTrace();
        }
    }

    public void editClient(Client client) {
        if (client == null) {
            closeEditor();
        } else {
            form.setClient(client);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setClient(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addClient() {
        grid.asSingleSelect().clear();
        // Passar null para o form criar novo cliente
        editClient(null);
    }

    private void updateList() {
        String filter = filterText.getValue().trim();
        if (filter.isEmpty()) {
            grid.setItems(clientService.findAll());
        } else {
            // Por enquanto, buscar apenas por nome
            grid.setItems(clientService.findByNameContaining(filter));
        }
    }
}