package br.com.alg.scg.infra.web.views.product;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.common.valueobject.Quantity;
import br.com.alg.scg.domain.common.valueobject.RecipeIngredient;
import br.com.alg.scg.domain.common.valueobject.UnitMeasurement;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.infra.web.layout.MainLayout;
import br.com.alg.scg.infra.web.views.components.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Route(value = "/recipes", layout = MainLayout.class)
@PageTitle("Receitas")
@SpringComponent
@UIScope
public class RecipesView extends VerticalLayout {

    private final ProductService productService;
    
    private Grid<Product> productGrid;
    private Grid<RecipeIngredient> ingredientGrid;
    private TextField searchField;
    private Product selectedProduct;

    @Autowired
    public RecipesView(ProductService productService) {
        this.productService = productService;
        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Buscar produtos finais...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterProductGrid());

        // Products grid
        productGrid = new Grid<>(Product.class, false);
        setupProductGrid();

        // Ingredients grid
        ingredientGrid = new Grid<>(RecipeIngredient.class, false);
        setupIngredientGrid();
    }

    private void setupProductGrid() {
        productGrid.addColumn(Product::getName).setHeader("Produto Final").setSortable(true);
        productGrid.addColumn(product -> product.getProductRecipe().isPresent() ? "✅ Tem receita" : "❌ Sem receita")
            .setHeader("Status").setSortable(true);
        productGrid.addColumn(product -> getIngredientCount(product)).setHeader("Ingredientes").setSortable(true);
        productGrid.addColumn(product -> getYieldQuantity(product)).setHeader("Rendimento").setSortable(true);

        productGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        productGrid.setHeight("300px");
        
        productGrid.asSingleSelect().addValueChangeListener(e -> {
            selectedProduct = e.getValue();
            loadRecipeIngredients();
        });
    }

    private void setupIngredientGrid() {
        ingredientGrid.addColumn(ingredient -> getRawMaterialName(ingredient.getRawMaterialId()))
            .setHeader("Matéria-Prima").setSortable(true);
        ingredientGrid.addColumn(ingredient -> formatQuantity(ingredient.getQuantity()))
            .setHeader("Quantidade").setSortable(true);

        ingredientGrid.addComponentColumn(ingredient -> {
            HorizontalLayout actions = new HorizontalLayout();
            
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> editIngredient(ingredient));
            
            Button removeButton = new Button(VaadinIcon.TRASH.create());
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            removeButton.addClickListener(e -> removeIngredient(ingredient));
            
            actions.add(editButton, removeButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Ações").setWidth("120px").setFlexGrow(0);

        ingredientGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        ingredientGrid.setHeight("300px");
    }

    private void setupLayout() {
        ComponentUtil.configureFullSize(this);
        
        // Header layout
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);
        
        headerLayout.add(searchField);

        // Products section
        H2 productsTitle = new H2("Produtos Finais");
        productsTitle.addClassName("view-title");

        // Ingredients section
        HorizontalLayout ingredientsHeader = new HorizontalLayout();
        ingredientsHeader.setWidthFull();
        ingredientsHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
        ingredientsHeader.setAlignItems(Alignment.CENTER);
        
        H3 ingredientsTitle = new H3("Receita");
        Button addIngredientButton = new Button("Adicionar Ingrediente", VaadinIcon.PLUS.create());
        addIngredientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addIngredientButton.addClickListener(e -> openAddIngredientDialog());
        addIngredientButton.setEnabled(false);

        Button editYieldButton = new Button("Rendimento", VaadinIcon.EDIT.create());
        editYieldButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editYieldButton.addClickListener(e -> openEditYieldDialog());
        editYieldButton.setEnabled(false);
        
        HorizontalLayout buttonLayout = new HorizontalLayout(editYieldButton, addIngredientButton);
        buttonLayout.setSpacing(true);
        ingredientsHeader.add(ingredientsTitle, buttonLayout);

        add(headerLayout, productsTitle, productGrid, ingredientsHeader, ingredientGrid);
        ComponentUtil.addStandardMargins(this);

        // Update button state when product is selected
        productGrid.asSingleSelect().addValueChangeListener(e -> {
            boolean hasSelection = e.getValue() != null;
            addIngredientButton.setEnabled(hasSelection);
            editYieldButton.setEnabled(hasSelection);
        });
    }

    private void loadData() {
        List<Product> finalProducts = productService.findAll().stream()
                .filter(product -> product.getType() == ProductType.FINAL_PRODUCT)
                .toList();
        productGrid.setItems(finalProducts);
    }

    private void filterProductGrid() {
        String searchTerm = searchField.getValue().toLowerCase();
        List<Product> filteredProducts = productService.findAll().stream()
                .filter(product -> product.getType() == ProductType.FINAL_PRODUCT)
                .filter(product -> product.getName().toLowerCase().contains(searchTerm))
                .toList();
        productGrid.setItems(filteredProducts);
    }

    private void loadRecipeIngredients() {
        if (selectedProduct == null) {
            ingredientGrid.setItems();
            return;
        }

        Optional<Recipe> recipe = selectedProduct.getProductRecipe();
        if (recipe.isPresent()) {
            ingredientGrid.setItems(recipe.get().getRecipes());
        } else {
            ingredientGrid.setItems();
        }
    }

    private void openAddIngredientDialog() {
        if (selectedProduct == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Adicionar Ingrediente");

        VerticalLayout layout = new VerticalLayout();

        // Raw material selection
        ComboBox<Product> rawMaterialCombo = new ComboBox<>("Matéria-Prima");
        List<Product> rawMaterials = productService.findAll().stream()
                .filter(product -> product.getType() == ProductType.RAW_MATERIAL)
                .toList();
        rawMaterialCombo.setItems(rawMaterials);
        rawMaterialCombo.setItemLabelGenerator(Product::getName);
        rawMaterialCombo.setRequired(true);

        // Quantity field
        BigDecimalField quantityField = new BigDecimalField("Quantidade");
        quantityField.setRequired(true);

        // Unit selection
        ComboBox<UnitMeasurement> unitCombo = new ComboBox<>("Unidade");
        unitCombo.setItems(UnitMeasurement.values());
        unitCombo.setItemLabelGenerator(unit -> unit.getUnit());
        unitCombo.setRequired(true);

        layout.add(rawMaterialCombo, quantityField, unitCombo);

        // Buttons
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button saveButton = new Button("Adicionar", e -> {
            if (rawMaterialCombo.getValue() != null && 
                quantityField.getValue() != null && 
                unitCombo.getValue() != null) {
                
                addIngredientToRecipe(rawMaterialCombo.getValue(), 
                                    quantityField.getValue(), 
                                    unitCombo.getValue());
                dialog.close();
            } else {
                Notification.show("Preencha todos os campos!");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        buttonsLayout.add(cancelButton, saveButton);
        layout.add(buttonsLayout);

        dialog.add(layout);
        dialog.open();
    }

    private void addIngredientToRecipe(Product rawMaterial, BigDecimal quantity, UnitMeasurement unit) {
        try {
            // Create or get recipe
            Recipe recipe = selectedProduct.getProductRecipe().orElse(new Recipe());
            
            // Check if ingredient already exists
            if (recipe.hasIngredient(rawMaterial.getId())) {
                // Ask user if they want to update existing ingredient
                com.vaadin.flow.component.confirmdialog.ConfirmDialog confirmDialog = 
                    new com.vaadin.flow.component.confirmdialog.ConfirmDialog();
                confirmDialog.setHeader("Ingrediente já existe");
                confirmDialog.setText("O ingrediente '" + rawMaterial.getName() + 
                                    "' já está na receita. Deseja atualizar a quantidade?");
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText("Atualizar");
                confirmDialog.setCancelText("Cancelar");
                
                confirmDialog.addConfirmListener(e -> {
                    try {
                        Quantity ingredientQuantity = new Quantity(quantity, unit);
                        recipe.updateIngredient(rawMaterial.getId(), ingredientQuantity);
                        
                        // Set recipe to product if it's new
                        if (selectedProduct.getProductRecipe().isEmpty()) {
                            selectedProduct.defineRecipe(recipe);
                        }
                        
                        productService.save(selectedProduct);
                        loadData();
                        loadRecipeIngredients();
                        Notification.show("Ingrediente atualizado com sucesso!");
                    } catch (Exception ex) {
                        Notification.show("Erro ao atualizar ingrediente: " + ex.getMessage());
                    }
                });
                
                confirmDialog.open();
                return;
            }
            
            // Add new ingredient
            Quantity ingredientQuantity = new Quantity(quantity, unit);
            recipe.addIngredient(rawMaterial, ingredientQuantity);
            
            // Set recipe to product if it's new
            if (selectedProduct.getProductRecipe().isEmpty()) {
                selectedProduct.defineRecipe(recipe);
            }
            
            // Save product
            productService.save(selectedProduct);
            
            // Refresh grids
            loadData();
            loadRecipeIngredients();
            
            Notification.show("Ingrediente adicionado com sucesso!");
            
        } catch (Exception e) {
            Notification.show("Erro ao adicionar ingrediente: " + e.getMessage());
        }
    }

    private void editIngredient(RecipeIngredient ingredient) {
        if (selectedProduct == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Ingrediente");

        VerticalLayout layout = new VerticalLayout();

        // Current ingredient info
        Product rawMaterial = productService.findById(ingredient.getRawMaterialId()).orElse(null);
        if (rawMaterial == null) {
            Notification.show("Erro: Matéria-prima não encontrada");
            return;
        }

        // Display current ingredient
        layout.add(new com.vaadin.flow.component.html.Span("Matéria-Prima: " + rawMaterial.getName()));

        // Quantity field
        BigDecimalField quantityField = new BigDecimalField("Nova Quantidade");
        quantityField.setValue(ingredient.getQuantity().value());
        quantityField.setRequired(true);

        // Unit selection
        ComboBox<UnitMeasurement> unitCombo = new ComboBox<>("Unidade");
        unitCombo.setItems(UnitMeasurement.values());
        unitCombo.setItemLabelGenerator(unit -> unit.getUnit());
        unitCombo.setValue(ingredient.getQuantity().unitMeasurement());
        unitCombo.setRequired(true);

        layout.add(quantityField, unitCombo);

        // Buttons
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button saveButton = new Button("Salvar", e -> {
            if (quantityField.getValue() != null && 
                unitCombo.getValue() != null) {
                
                updateIngredientInRecipe(ingredient.getRawMaterialId(), 
                                       quantityField.getValue(), 
                                       unitCombo.getValue());
                dialog.close();
            } else {
                Notification.show("Preencha todos os campos!");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        buttonsLayout.add(cancelButton, saveButton);
        layout.add(buttonsLayout);

        dialog.add(layout);
        dialog.open();
    }

    private void updateIngredientInRecipe(UUID rawMaterialId, BigDecimal quantity, UnitMeasurement unit) {
        try {
            Recipe recipe = selectedProduct.getProductRecipe().orElse(null);
            if (recipe == null) {
                Notification.show("Erro: Produto não possui receita");
                return;
            }
            
            // Update ingredient
            Quantity newQuantity = new Quantity(quantity, unit);
            recipe.updateIngredient(rawMaterialId, newQuantity);
            
            // Save product
            productService.save(selectedProduct);
            
            // Refresh grids
            loadData();
            loadRecipeIngredients();
            
            Notification.show("Ingrediente atualizado com sucesso!");
            
        } catch (Exception e) {
            Notification.show("Erro ao atualizar ingrediente: " + e.getMessage());
        }
    }

    private void removeIngredient(RecipeIngredient ingredient) {
        try {
            Recipe recipe = selectedProduct.getProductRecipe().orElse(null);
            if (recipe == null) {
                Notification.show("Erro: Produto não possui receita");
                return;
            }
            
            // Remove ingredient
            recipe.removeIngredient(ingredient.getRawMaterialId());
            
            // Save product
            productService.save(selectedProduct);
            
            // Refresh grids
            loadData();
            loadRecipeIngredients();
            
            Notification.show("Ingrediente removido com sucesso!");
            
        } catch (Exception e) {
            Notification.show("Erro ao remover ingrediente: " + e.getMessage());
        }
    }

    private String getRawMaterialName(UUID rawMaterialId) {
        return productService.findById(rawMaterialId)
                .map(Product::getName)
                .orElse("Produto não encontrado");
    }

    private String formatQuantity(Quantity quantity) {
        return quantity.value().stripTrailingZeros().toPlainString() + " " + quantity.unitMeasurement().getUnit();
    }

    private void openEditYieldDialog() {
        if (selectedProduct == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Definir Rendimento da Receita");

        VerticalLayout layout = new VerticalLayout();

        // Current yield display
        Optional<Recipe> currentRecipe = selectedProduct.getProductRecipe();
        BigDecimal currentYield = currentRecipe.map(Recipe::getYieldQuantity).orElse(BigDecimal.ONE);

        // Yield field
        BigDecimalField yieldField = new BigDecimalField("Quantas unidades esta receita produz?");
        yieldField.setValue(currentYield);
        yieldField.setRequired(true);
        yieldField.setHelperText("Ex: 7 brownies, 12 cupcakes, 1 bolo grande");

        layout.add(yieldField);

        // Buttons
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button saveButton = new Button("Salvar", e -> {
            if (yieldField.getValue() != null && yieldField.getValue().compareTo(BigDecimal.ZERO) > 0) {
                updateRecipeYield(yieldField.getValue());
                dialog.close();
            } else {
                Notification.show("Rendimento deve ser maior que zero!");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        buttonsLayout.add(cancelButton, saveButton);
        layout.add(buttonsLayout);

        dialog.add(layout);
        dialog.open();
    }

    private void updateRecipeYield(BigDecimal newYield) {
        try {
            // Create or get recipe
            Recipe recipe = selectedProduct.getProductRecipe().orElse(new Recipe());
            
            // Set yield
            recipe.setYieldQuantity(newYield);
            
            // Set recipe to product if it's new
            if (selectedProduct.getProductRecipe().isEmpty()) {
                selectedProduct.defineRecipe(recipe);
            }
            
            // Save product
            productService.save(selectedProduct);
            
            // Refresh grids
            loadData();
            loadRecipeIngredients();
            
            Notification.show("Rendimento atualizado com sucesso!");
            
        } catch (Exception e) {
            Notification.show("Erro ao atualizar rendimento: " + e.getMessage());
        }
    }

    private String getYieldQuantity(Product product) {
        return product.getProductRecipe()
                .map(recipe -> recipe.getYieldQuantity().stripTrailingZeros().toPlainString() + " unidades")
                .orElse("Não definido");
    }

    private int getIngredientCount(Product product) {
        return product.getProductRecipe()
                .map(recipe -> recipe.getRecipes().size())
                .orElse(0);
    }
}