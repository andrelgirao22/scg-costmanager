package br.com.alg.scg.infra.web.views.purchase;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.PurchaseService;
import br.com.alg.scg.application.service.SupplierService;
import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.Supplier;
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
import com.vaadin.flow.data.binder.Binder;
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
import java.util.UUID;

/**
 * View para operação de compras - criação de novas compras
 */
@PageTitle("Nova Compra")
@Route(value = "purchase-operation", layout = MainLayout.class)
public class PurchaseOperationView extends Main {

    private final PurchaseService purchaseService;
    private final SupplierService supplierService;
    private final ProductService productService;

    private ComboBox<Supplier> supplierComboBox;
    private DatePicker purchaseDatePicker;
    private ComboBox<Product> productComboBox;
    private BigDecimalField quantityField;
    private ComboBox<UnitMeasurement> unitMeasurementComboBox;
    private BigDecimalField unitCostField;
    private Button addItemButton;
    private Grid<PurchaseItemData> itemsGrid;
    private Span totalLabel;
    private Button savePurchaseButton;
    private Button cancelButton;
    private SupplierDialog supplierDialog;

    private List<PurchaseItemData> purchaseItems = new ArrayList<>();
    private Binder<PurchaseItemData> itemBinder = new Binder<>(PurchaseItemData.class);

    @Autowired
    public PurchaseOperationView(PurchaseService purchaseService, 
                                SupplierService supplierService,
                                ProductService productService) {
        this.purchaseService = purchaseService;
        this.supplierService = supplierService;
        this.productService = productService;
        
        addClassName("purchase-operation-view");
        setSizeFull();
        getStyle().set("padding", "var(--lumo-space-l)");
        
        add(createHeader(), createContent());
        setupEventHandlers();
        loadData();
        createSupplierDialog();
    }

    private Component createHeader() {
        H2 header = new H2("Nova Compra");
        header.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);
        return header;
    }

    private Component createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(true);

        content.add(
            createPurchaseInfoSection(),
            new Hr(),
            createItemsSection(),
            new Hr(),
            createTotalSection(),
            createButtonsSection()
        );

        return content;
    }

    private Component createPurchaseInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H2 sectionTitle = new H2("Informações da Compra");
        sectionTitle.addClassName(LumoUtility.FontSize.MEDIUM);

        supplierComboBox = new ComboBox<>("Fornecedor");
        supplierComboBox.setItemLabelGenerator(Supplier::getName);
        supplierComboBox.setRequired(true);
        supplierComboBox.setWidthFull();
        
        Button newSupplierButton = new Button("Novo Fornecedor");
        newSupplierButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        newSupplierButton.setIcon(VaadinIcon.PLUS.create());
        newSupplierButton.addClickListener(e -> supplierDialog.openDialog());

        purchaseDatePicker = new DatePicker("Data da Compra");
        purchaseDatePicker.setValue(LocalDate.now());
        purchaseDatePicker.setRequired(true);
        purchaseDatePicker.setMax(LocalDate.now());

        HorizontalLayout supplierLayout = new HorizontalLayout(supplierComboBox, newSupplierButton);
        supplierLayout.setAlignItems(HorizontalLayout.Alignment.END);
        supplierLayout.setFlexGrow(1, supplierComboBox);
        
        HorizontalLayout fieldsLayout = new HorizontalLayout(supplierLayout, purchaseDatePicker);
        fieldsLayout.setWidthFull();
        fieldsLayout.setFlexGrow(2, supplierLayout);
        fieldsLayout.setFlexGrow(1, purchaseDatePicker);

        section.add(sectionTitle, fieldsLayout);
        return section;
    }

    private Component createItemsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H2 sectionTitle = new H2("Itens da Compra");
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

        productComboBox = new ComboBox<>("Produto");
        productComboBox.setItemLabelGenerator(Product::getName);
        productComboBox.setWidthFull();

        quantityField = new BigDecimalField("Quantidade");
        quantityField.setValue(BigDecimal.ONE);

        unitMeasurementComboBox = new ComboBox<>("Unidade");
        unitMeasurementComboBox.setItems(UnitMeasurement.values());
        unitMeasurementComboBox.setItemLabelGenerator(UnitMeasurement::getUnit);
        unitMeasurementComboBox.setValue(UnitMeasurement.KILOGRAM);

        unitCostField = new BigDecimalField("Custo Unitário (R$)");
        unitCostField.setPrefixComponent(new Span("R$"));

        addItemButton = new Button("Adicionar Item");
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout fieldsRow1 = new HorizontalLayout(productComboBox, quantityField);
        fieldsRow1.setWidthFull();
        fieldsRow1.setFlexGrow(2, productComboBox);
        fieldsRow1.setFlexGrow(1, quantityField);

        HorizontalLayout fieldsRow2 = new HorizontalLayout(unitMeasurementComboBox, unitCostField, addItemButton);
        fieldsRow2.setWidthFull();
        fieldsRow2.setAlignItems(HorizontalLayout.Alignment.END);
        fieldsRow2.setFlexGrow(1, unitMeasurementComboBox);
        fieldsRow2.setFlexGrow(2, unitCostField);

        formLayout.add(fieldsRow1, fieldsRow2);
        return formLayout;
    }

    private Component createItemsGrid() {
        itemsGrid = new Grid<>(PurchaseItemData.class, false);
        itemsGrid.addClassName("purchase-items-grid");
        itemsGrid.setHeight("300px");

        itemsGrid.addColumn(item -> item.product.getName())
                .setHeader("Produto")
                .setFlexGrow(2);

        itemsGrid.addColumn(PurchaseItemData::getQuantity)
                .setHeader("Quantidade")
                .setFlexGrow(1);

        itemsGrid.addColumn(item -> item.getUnitMeasurement().getUnit())
                .setHeader("Unidade")
                .setFlexGrow(1);

        itemsGrid.addColumn(item -> "R$ " + item.getUnitCost().setScale(2, RoundingMode.HALF_UP))
                .setHeader("Custo Unit.")
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

        savePurchaseButton = new Button("Salvar Compra");
        savePurchaseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonsLayout.add(cancelButton, savePurchaseButton);
        return buttonsLayout;
    }

    private void setupEventHandlers() {
        addItemButton.addClickListener(e -> addItem());
        savePurchaseButton.addClickListener(e -> savePurchase());
        cancelButton.addClickListener(e -> clearForm());
    }

    private void loadData() {
        supplierComboBox.setItems(supplierService.findAll());
        
        List<Product> rawMaterials = productService.findByType(ProductType.RAW_MATERIAL);
        productComboBox.setItems(rawMaterials);
    }
    
    private void createSupplierDialog() {
        supplierDialog = new SupplierDialog(supplierService, this::onSupplierCreated);
    }
    
    private void onSupplierCreated(Supplier supplier) {
        // Recarregar lista de fornecedores
        supplierComboBox.setItems(supplierService.findAll());
        // Selecionar o fornecedor recém-criado
        supplierComboBox.setValue(supplier);
        showSuccess("Fornecedor " + supplier.getName() + " criado e selecionado!");
    }

    private void addItem() {
        if (validateItemForm()) {
            PurchaseItemData item = new PurchaseItemData(
                productComboBox.getValue(),
                quantityField.getValue(),
                unitMeasurementComboBox.getValue(),
                unitCostField.getValue()
            );
            
            purchaseItems.add(item);
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
        if (unitMeasurementComboBox.getValue() == null) {
            showError("Selecione uma unidade de medida");
            return false;
        }
        if (unitCostField.getValue() == null || unitCostField.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            showError("Informe um custo unitário válido");
            return false;
        }
        return true;
    }

    private void removeItem(PurchaseItemData item) {
        purchaseItems.remove(item);
        refreshItemsGrid();
        updateTotal();
    }

    private void refreshItemsGrid() {
        itemsGrid.setItems(purchaseItems);
    }

    private void clearItemForm() {
        productComboBox.clear();
        quantityField.setValue(BigDecimal.ONE);
        unitMeasurementComboBox.setValue(UnitMeasurement.KILOGRAM);
        unitCostField.clear();
    }

    private void updateTotal() {
        BigDecimal total = purchaseItems.stream()
                .map(PurchaseItemData::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalLabel.setText("Total: R$ " + total.setScale(2, RoundingMode.HALF_UP));
    }

    private void savePurchase() {
        if (validatePurchaseForm()) {
            try {
                Purchase purchase = purchaseService.createPurchase(
                    supplierComboBox.getValue(),
                    purchaseDatePicker.getValue()
                );

                for (PurchaseItemData item : purchaseItems) {
                    Quantity quantity = new Quantity(item.quantity, item.unitMeasurement);
                    Money unitCost = new Money(item.unitCost);
                    purchaseService.addItem(
                        purchase.getId(),
                        item.product,
                        quantity,
                        unitCost
                    );
                }

                showSuccess("Compra salva com sucesso!");
                clearForm();

            } catch (Exception e) {
                showError("Erro ao salvar compra: " + e.getMessage());
            }
        }
    }

    private boolean validatePurchaseForm() {
        if (supplierComboBox.getValue() == null) {
            showError("Selecione um fornecedor");
            return false;
        }
        if (purchaseDatePicker.getValue() == null) {
            showError("Informe a data da compra");
            return false;
        }
        if (purchaseItems.isEmpty()) {
            showError("Adicione pelo menos um item à compra");
            return false;
        }
        return true;
    }

    private void clearForm() {
        supplierComboBox.clear();
        purchaseDatePicker.setValue(LocalDate.now());
        purchaseItems.clear();
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

    // Classe interna para representar itens da compra
    public static class PurchaseItemData {
        private Product product;
        private BigDecimal quantity;
        private UnitMeasurement unitMeasurement;
        private BigDecimal unitCost;

        public PurchaseItemData(Product product, BigDecimal quantity, UnitMeasurement unitMeasurement, BigDecimal unitCost) {
            this.product = product;
            this.quantity = quantity;
            this.unitMeasurement = unitMeasurement;
            this.unitCost = unitCost;
        }

        public Product getProduct() { return product; }
        public BigDecimal getQuantity() { return quantity; }
        public UnitMeasurement getUnitMeasurement() { return unitMeasurement; }
        public BigDecimal getUnitCost() { return unitCost; }

        public BigDecimal getSubtotal() {
            return quantity.multiply(unitCost);
        }
    }
}