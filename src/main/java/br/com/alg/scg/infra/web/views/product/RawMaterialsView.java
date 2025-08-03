package br.com.alg.scg.infra.web.views.product;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.infra.web.layout.MainLayout;
import br.com.alg.scg.infra.web.views.components.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Route(value = "/raw-materials", layout = MainLayout.class)
@PageTitle("Matérias-Primas")
@SpringComponent
@UIScope
public class RawMaterialsView extends VerticalLayout {

    private final ProductService productService;
    
    private Grid<Product> grid;
    private TextField searchField;
    private ProductForm productForm;

    @Autowired
    public RawMaterialsView(ProductService productService) {
        this.productService = productService;
        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        // Header
        H2 title = new H2("Matérias-Primas");
        title.addClassName("view-title");

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Buscar matérias-primas...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterGrid());

        // New button
        Button newButton = new Button("Nova Matéria-Prima", VaadinIcon.PLUS.create());
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> openProductForm(null));

        // Grid
        grid = new Grid<>(Product.class, false);
        setupGrid();

        // Form
        productForm = new ProductForm();
        productForm.addSaveListener(this::handleSave);
        productForm.addCloseListener(e -> closeForm());
        productForm.setVisible(false);
    }

    private void setupGrid() {
        grid.addColumn(Product::getName).setHeader("Nome").setSortable(true);
        grid.addColumn(product -> formatStock(product.getStock())).setHeader("Estoque").setSortable(true);
        grid.addColumn(product -> formatCurrentPrice(product)).setHeader("Preço Atual").setSortable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                openProductForm(e.getValue());
            }
        });
    }

    private void setupLayout() {
        ComponentUtil.configureFullSize(this);
        
        // Header layout
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);
        
        headerLayout.add(searchField, new Button("Nova Matéria-Prima", VaadinIcon.PLUS.create(), e -> openProductForm(null)));
        
        Button newButton = (Button) headerLayout.getComponentAt(1);
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Content layout
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidthFull();
        contentLayout.setFlexGrow(1);
        
        grid.setWidthFull();
        contentLayout.add(grid);
        contentLayout.add(productForm);
        
        add(headerLayout, contentLayout);
        ComponentUtil.addStandardMargins(this);
    }

    private void loadData() {
        List<Product> rawMaterials = productService.findAll().stream()
                .filter(product -> product.getType() == ProductType.RAW_MATERIAL)
                .toList();
        grid.setItems(rawMaterials);
    }

    private void filterGrid() {
        String searchTerm = searchField.getValue().toLowerCase();
        List<Product> filteredProducts = productService.findAll().stream()
                .filter(product -> product.getType() == ProductType.RAW_MATERIAL)
                .filter(product -> product.getName().toLowerCase().contains(searchTerm))
                .toList();
        grid.setItems(filteredProducts);
    }

    private void openProductForm(Product product) {
        if (product == null) {
            productForm.setProduct(Product.createRawMaterial("", BigDecimal.ZERO));
        } else {
            productForm.setProduct(product);
        }
        productForm.setVisible(true);
        grid.asSingleSelect().clear();
    }

    private void closeForm() {
        productForm.setVisible(false);
        grid.asSingleSelect().clear();
    }

    private void handleSave(ProductForm.SaveEvent event) {
        try {
            Product product = event.getProduct();
            product.setType(ProductType.RAW_MATERIAL); // Force raw material type
            productService.save(product);
            loadData();
            closeForm();
            Notification.show("Matéria-prima salva com sucesso!");
        } catch (Exception e) {
            Notification.show("Erro ao salvar matéria-prima: " + e.getMessage());
        }
    }

    private String formatStock(BigDecimal stock) {
        return stock != null ? stock.stripTrailingZeros().toPlainString() + " kg" : "0 kg";
    }

    private String formatCurrentPrice(Product product) {
        return product.getCurrentPrice()
                .map(price -> "R$ " + price.value().stripTrailingZeros().toPlainString())
                .orElse("Sem preço");
    }
}