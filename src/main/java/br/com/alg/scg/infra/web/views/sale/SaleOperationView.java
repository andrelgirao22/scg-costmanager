package br.com.alg.scg.infra.web.views.sale;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.SaleService;
import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.domain.sales.entity.Client;
import br.com.alg.scg.domain.sales.entity.Sale;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * View para operação de vendas - criação de novas vendas
 */
@PageTitle("Nova Venda")
@Route(value = "sale-operation", layout = MainLayout.class)
public class SaleOperationView extends Main {

    private final SaleService saleService;
    private final ClientService clientService;
    private final ProductService productService;

    private ComboBox<Client> clientComboBox;
    private DatePicker saleDatePicker;
    private ComboBox<Product> productComboBox;
    private BigDecimalField quantityField;
    private Span calculatedPriceLabel;
    private Button addItemButton;
    private Grid<SaleItemData> itemsGrid;
    private Span totalLabel;
    private Button saveSaleButton;
    private Button cancelButton;
    private ClientDialog clientDialog;

    private List<SaleItemData> saleItems = new ArrayList<>();

    @Autowired
    public SaleOperationView(SaleService saleService, 
                            ClientService clientService,
                            ProductService productService) {
        this.saleService = saleService;
        this.clientService = clientService;
        this.productService = productService;
        
        addClassName("sale-operation-view");
        setSizeFull();
        getStyle().set("padding", "var(--lumo-space-l)");
        
        add(createHeader(), createContent());
        setupEventHandlers();
        loadData();
        createClientDialog();
    }

    private Component createHeader() {
        H2 header = new H2("Nova Venda");
        header.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);
        return header;
    }

    private Component createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(true);

        content.add(
            createSaleInfoSection(),
            new Hr(),
            createItemsSection(),
            new Hr(),
            createTotalSection(),
            createButtonsSection()
        );

        return content;
    }

    private Component createSaleInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H2 sectionTitle = new H2("Informações da Venda");
        sectionTitle.addClassName(LumoUtility.FontSize.MEDIUM);

        clientComboBox = new ComboBox<>("Cliente");
        clientComboBox.setItemLabelGenerator(Client::getName);
        clientComboBox.setRequired(true);
        clientComboBox.setWidthFull();
        
        Button newClientButton = new Button("Novo Cliente");
        newClientButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        newClientButton.setIcon(VaadinIcon.PLUS.create());
        newClientButton.addClickListener(e -> clientDialog.openDialog());

        saleDatePicker = new DatePicker("Data da Venda");
        saleDatePicker.setValue(LocalDate.now());
        saleDatePicker.setRequired(true);
        saleDatePicker.setMax(LocalDate.now());

        HorizontalLayout clientLayout = new HorizontalLayout(clientComboBox, newClientButton);
        clientLayout.setAlignItems(HorizontalLayout.Alignment.END);
        clientLayout.setFlexGrow(1, clientComboBox);
        
        HorizontalLayout fieldsLayout = new HorizontalLayout(clientLayout, saleDatePicker);
        fieldsLayout.setWidthFull();
        fieldsLayout.setFlexGrow(2, clientLayout);
        fieldsLayout.setFlexGrow(1, saleDatePicker);

        section.add(sectionTitle, fieldsLayout);
        return section;
    }

    private Component createItemsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H2 sectionTitle = new H2("Itens da Venda");
        sectionTitle.addClassName(LumoUtility.FontSize.MEDIUM);

        Component itemForm = createItemForm();
        Component itemsGrid = createItemsGrid();

        section.add(sectionTitle, itemForm, itemsGrid);
        return section;
    }

    private Component createItemForm() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        productComboBox = new ComboBox<>("Produto Final");
        productComboBox.setItemLabelGenerator(Product::getName);
        productComboBox.setWidthFull();
        productComboBox.addValueChangeListener(e -> updateCalculatedPrice());

        quantityField = new BigDecimalField("Quantidade");
        quantityField.setValue(BigDecimal.ONE);
        quantityField.addValueChangeListener(e -> updateCalculatedPrice());

        calculatedPriceLabel = new Span("Preço: Selecione um produto");
        calculatedPriceLabel.addClassNames(
            LumoUtility.FontWeight.BOLD,
            LumoUtility.TextColor.PRIMARY,
            LumoUtility.FontSize.MEDIUM
        );

        addItemButton = new Button("Adicionar Item");
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout fieldsRow1 = new HorizontalLayout(productComboBox, quantityField);
        fieldsRow1.setWidthFull();
        fieldsRow1.setFlexGrow(2, productComboBox);
        fieldsRow1.setFlexGrow(1, quantityField);

        HorizontalLayout fieldsRow2 = new HorizontalLayout(calculatedPriceLabel, addItemButton);
        fieldsRow2.setWidthFull();
        fieldsRow2.setAlignItems(HorizontalLayout.Alignment.END);
        fieldsRow2.setFlexGrow(1, calculatedPriceLabel);

        formLayout.add(fieldsRow1, fieldsRow2);
        return formLayout;
    }

    private Component createItemsGrid() {
        itemsGrid = new Grid<>(SaleItemData.class, false);
        itemsGrid.addClassName("sale-items-grid");
        itemsGrid.setHeight("300px");

        itemsGrid.addColumn(item -> item.product.getName())
                .setHeader("Produto")
                .setFlexGrow(2);

        itemsGrid.addColumn(SaleItemData::getQuantity)
                .setHeader("Quantidade")
                .setFlexGrow(1);

        itemsGrid.addColumn(item -> "R$ " + item.getUnitPrice().setScale(2, RoundingMode.HALF_UP))
                .setHeader("Preço Unit.")
                .setFlexGrow(1);

        itemsGrid.addColumn(item -> "R$ " + item.getSubtotal().setScale(2, RoundingMode.HALF_UP))
                .setHeader("Subtotal")
                .setFlexGrow(1);

        itemsGrid.addColumn(new ComponentRenderer<>(item -> {
            Button removeButton = new Button("Remover");
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            removeButton.addClickListener(e -> removeItem(item));
            return removeButton;
        })).setHeader("Ações").setFlexGrow(1);

        return itemsGrid;
    }

    private Component createTotalSection() {
        HorizontalLayout totalSection = new HorizontalLayout();
        totalSection.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        totalSection.setAlignItems(HorizontalLayout.Alignment.CENTER);

        totalLabel = new Span("Total: R$ 0,00");
        totalLabel.addClassNames(
            LumoUtility.FontSize.LARGE, 
            LumoUtility.FontWeight.BOLD,
            LumoUtility.TextColor.PRIMARY
        );

        totalSection.add(totalLabel);
        return totalSection;
    }

    private Component createButtonsSection() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        buttonsLayout.setSpacing(true);

        cancelButton = new Button("Cancelar");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveSaleButton = new Button("Salvar Venda");
        saveSaleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonsLayout.add(cancelButton, saveSaleButton);
        return buttonsLayout;
    }

    private void setupEventHandlers() {
        addItemButton.addClickListener(e -> addItem());
        saveSaleButton.addClickListener(e -> saveSale());
        cancelButton.addClickListener(e -> clearForm());
    }

    private void loadData() {
        clientComboBox.setItems(clientService.findAll());
        
        List<Product> finalProducts = productService.findByType(ProductType.FINAL_PRODUCT);
        productComboBox.setItems(finalProducts);
    }
    
    private void createClientDialog() {
        clientDialog = new ClientDialog(clientService, this::onClientCreated);
    }
    
    private void onClientCreated(Client client) {
        // Recarregar lista de clientes
        clientComboBox.setItems(clientService.findAll());
        // Selecionar o cliente recém-criado
        clientComboBox.setValue(client);
        showSuccess("Cliente " + client.getName() + " criado e selecionado!");
    }

    private void updateCalculatedPrice() {
        Product selectedProduct = productComboBox.getValue();
        BigDecimal quantity = quantityField.getValue();
        
        if (selectedProduct != null && quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
            // TODO: Implementar cálculo real de preço baseado em receita + margem
            BigDecimal calculatedPrice = calculateProductPrice(selectedProduct);
            BigDecimal totalPrice = calculatedPrice.multiply(quantity);
            
            calculatedPriceLabel.setText(String.format("Preço: R$ %.2f (Unit: R$ %.2f)", 
                totalPrice, calculatedPrice));
        } else {
            calculatedPriceLabel.setText("Preço: Selecione um produto e quantidade");
        }
    }

    private BigDecimal calculateProductPrice(Product product) {
        // Implementação temporária - retorna um preço fixo
        // TODO: Implementar cálculo real baseado em receita e margem de lucro
        return new BigDecimal("25.00"); // Preço temporário
    }

    private void addItem() {
        if (validateItemForm()) {
            Product product = productComboBox.getValue();
            BigDecimal quantity = quantityField.getValue();
            BigDecimal unitPrice = calculateProductPrice(product);
            
            SaleItemData item = new SaleItemData(product, quantity, unitPrice);
            
            saleItems.add(item);
            refreshItemsGrid();
            clearItemForm();
            updateTotal();
        }
    }

    private boolean validateItemForm() {
        if (productComboBox.getValue() == null) {
            showError("Selecione um produto");
            return false;
        }
        if (quantityField.getValue() == null || quantityField.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            showError("Informe uma quantidade válida");
            return false;
        }
        return true;
    }

    private void removeItem(SaleItemData item) {
        saleItems.remove(item);
        refreshItemsGrid();
        updateTotal();
    }

    private void refreshItemsGrid() {
        itemsGrid.setItems(saleItems);
    }

    private void clearItemForm() {
        productComboBox.clear();
        quantityField.setValue(BigDecimal.ONE);
        calculatedPriceLabel.setText("Preço: Selecione um produto");
    }

    private void updateTotal() {
        BigDecimal total = saleItems.stream()
                .map(SaleItemData::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalLabel.setText("Total: R$ " + total.setScale(2, RoundingMode.HALF_UP));
    }

    private void saveSale() {
        if (validateSaleForm()) {
            try {
                // TODO: Implementar integração com SaleService
                showSuccess("Funcionalidade em desenvolvimento!");
                
            } catch (Exception e) {
                showError("Erro ao salvar venda: " + e.getMessage());
            }
        }
    }

    private boolean validateSaleForm() {
        if (clientComboBox.getValue() == null) {
            showError("Selecione um cliente");
            return false;
        }
        if (saleDatePicker.getValue() == null) {
            showError("Informe a data da venda");
            return false;
        }
        if (saleItems.isEmpty()) {
            showError("Adicione pelo menos um item à venda");
            return false;
        }
        return true;
    }

    private void clearForm() {
        clientComboBox.clear();
        saleDatePicker.setValue(LocalDate.now());
        saleItems.clear();
        refreshItemsGrid();
        clearItemForm();
        updateTotal();
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    // Classe interna para representar itens da venda
    public static class SaleItemData {
        private Product product;
        private BigDecimal quantity;
        private BigDecimal unitPrice;

        public SaleItemData(Product product, BigDecimal quantity, BigDecimal unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Product getProduct() { return product; }
        public BigDecimal getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }

        public BigDecimal getSubtotal() {
            return quantity.multiply(unitPrice);
        }
    }
}