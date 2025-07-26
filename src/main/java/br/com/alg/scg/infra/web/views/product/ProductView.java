package br.com.alg.scg.infra.web.views.product;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * View para gerenciamento de produtos
 */
@PageTitle("Produtos")
@Route(value = "products", layout = MainLayout.class)
public class ProductView extends Main {

    private final ProductService productService;
    private Grid<Product> grid = new Grid<>(Product.class, false);
    private TextField filterText = new TextField();
    private ProductForm form;

    @Autowired
    public ProductView(ProductService productService) {
        this.productService = productService;
        
        addClassName("product-view");
        setSizeFull();
        
        configureGrid();
        configureForm();
        
        add(createHeader(), createToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private Component createHeader() {
        H2 header = new H2("Gerenciar Produtos");
        header.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);
        return header;
    }

    private Component createToolbar() {
        filterText.setPlaceholder("Filtrar por nome...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addProductButton = new Button("Novo Produto");
        addProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProductButton.setIcon(VaadinIcon.PLUS.create());
        addProductButton.addClickListener(click -> addProduct());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addProductButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("product-grid");
        grid.setSizeFull();
        
        grid.addColumn(Product::getName).setHeader("Nome").setSortable(true);
        grid.addColumn(product -> product.getType() == ProductType.RAW_MATERIAL ? "Matéria-Prima" : "Produto Final")
             .setHeader("Tipo").setSortable(true);
        grid.addColumn(product -> formatStock(product.getStock()))
             .setHeader("Estoque").setSortable(true);
        grid.addColumn(product -> product.getCurrentPrice()
                .map(price -> "R$ " + price.value())
                .orElse("Sem preço"))
             .setHeader("Preço Atual");
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        grid.asSingleSelect().addValueChangeListener(event -> editProduct(event.getValue()));
    }

    private String formatStock(BigDecimal stock) {
        if (stock == null) return "0";
        return stock.stripTrailingZeros().toPlainString();
    }

    private void configureForm() {
        form = new ProductForm();
        form.setWidth("25em");
        
        form.addSaveListener(this::saveProduct);
        form.addDeleteListener(this::deleteProduct);
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

    private void saveProduct(ProductForm.SaveEvent event) {
        try {
            productService.save(event.getProduct());
            updateList();
            closeEditor();
            
            // Notificação de sucesso
            Notification notification = Notification.show("Produto salvo com sucesso!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            // Notificação de erro
            Notification notification = Notification.show("Erro ao salvar produto: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void deleteProduct(ProductForm.DeleteEvent event) {
        try {
            productService.deleteById(event.getProduct().getId());
            updateList();
            closeEditor();
            
            // Notificação de sucesso
            Notification notification = Notification.show("Produto excluído com sucesso!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            // Notificação de erro
            Notification notification = Notification.show("Erro ao excluir produto: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    public void editProduct(Product product) {
        form.setProduct(product);
        form.setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        form.setProduct(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addProduct() {
        grid.asSingleSelect().clear();
        // Abrir form para criar novo produto (null = novo produto)
        editProduct(null);
    }

    private void updateList() {
        String filter = filterText.getValue().trim();
        if (filter.isEmpty()) {
            grid.setItems(productService.findAll());
        } else {
            grid.setItems(productService.findByNameContainingIgnoreCase(filter));
        }
    }
}