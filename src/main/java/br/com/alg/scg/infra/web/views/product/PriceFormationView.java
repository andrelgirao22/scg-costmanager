package br.com.alg.scg.infra.web.views.product;

import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.domain.common.valueobject.Money;
import br.com.alg.scg.domain.common.valueobject.RecipeIngredient;
import br.com.alg.scg.domain.finance.service.SalePriceCalculatorService;
import br.com.alg.scg.domain.finance.valueobject.ProfitMargin;
import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.entity.Recipe;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import br.com.alg.scg.infra.web.layout.MainLayout;
import br.com.alg.scg.infra.web.views.components.ComponentUtil;
import br.com.alg.scg.infra.web.views.product.dto.RecipeIngredientDisplay;
import br.com.alg.scg.infra.web.views.product.dto.CostAnalysisDisplay;
import br.com.alg.scg.infra.web.views.product.dto.PricingInfoDisplay;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Route(value = "/price-formation", layout = MainLayout.class)
@PageTitle("Formação de Preços")
@SpringComponent
@UIScope
public class PriceFormationView extends VerticalLayout {

    private final ProductService productService;
    private final SalePriceCalculatorService priceCalculatorService;
    
    private Grid<Product> productGrid;
    private TextField searchField;
    private Div productDetailsPanel;
    private Product selectedProduct;

    @Autowired
    public PriceFormationView(ProductService productService, 
                              SalePriceCalculatorService priceCalculatorService) {
        this.productService = productService;
        this.priceCalculatorService = priceCalculatorService;
        
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
        
        // Product details panel
        productDetailsPanel = new Div();
        productDetailsPanel.addClassName("price-details-panel");
        productDetailsPanel.setVisible(false);
    }

    private void setupProductGrid() {
        productGrid.addColumn(Product::getName).setHeader("Produto Final").setSortable(true);
        
        productGrid.addColumn(product -> hasRecipe(product) ? "✅ Completa" : "❌ Sem receita")
            .setHeader("Receita").setSortable(true);
            
        productGrid.addColumn(product -> formatMoney(calculateProductionCost(product)))
            .setHeader("Custo Produção").setSortable(true);
            
        productGrid.addColumn(product -> formatProfitMargin(product.getProfitMargin()))
            .setHeader("Margem Lucro").setSortable(true);
            
        productGrid.addColumn(product -> formatMoney(calculateSalePrice(product)))
            .setHeader("Preço Venda").setSortable(true);
            
        productGrid.addComponentColumn(product -> {
            Button marginButton = new Button("Editar Margem", VaadinIcon.EDIT.create());
            marginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            marginButton.addClickListener(e -> editProfitMargin(product));
            marginButton.setEnabled(hasRecipe(product));
            
            return marginButton;
        }).setHeader("Ações").setWidth("120px").setFlexGrow(0);

        productGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        productGrid.setHeight("100%"); // Full height to use available space
        
        productGrid.asSingleSelect().addValueChangeListener(e -> {
            Product newSelection = e.getValue();
            
            // If clicking on the same product, deselect it
            if (selectedProduct != null && selectedProduct.equals(newSelection)) {
                productGrid.asSingleSelect().clear();
                selectedProduct = null;
                hideProductDetails();
            } else {
                selectedProduct = newSelection;
                if (selectedProduct != null) {
                    showProductDetails(selectedProduct);
                } else {
                    hideProductDetails();
                }
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
        
        H2 title = new H2("Formação de Preços");
        title.addClassName("view-title");
        
        headerLayout.add(title, searchField);

        // Main content layout with side panel
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setHeight("calc(100vh - 160px)"); // Adjust based on header height
        mainContent.setSpacing(false);
        mainContent.getStyle().set("gap", "0");
        
        // Left panel with grid
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setPadding(false);
        leftPanel.setSpacing(false);
        leftPanel.setMinWidth("400px");
        leftPanel.add(productGrid);
        
        // Right panel with details (initially hidden)
        //productDetailsPanel.setPadding(false);
        //productDetailsPanel.setSpacing(true);
        productDetailsPanel.setMinWidth("350px");
        productDetailsPanel.setMaxWidth("500px");
        productDetailsPanel.getStyle().set("border-left", "1px solid var(--lumo-contrast-10pct)");
        productDetailsPanel.getStyle().set("padding", "1rem");
        productDetailsPanel.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        productDetailsPanel.setVisible(false);
        
        mainContent.add(leftPanel, productDetailsPanel);
        mainContent.setFlexGrow(1, leftPanel);
        mainContent.setFlexGrow(0, productDetailsPanel); // Fixed width for details panel
        
        add(headerLayout, mainContent);
        ComponentUtil.addStandardMargins(this);
    }

    private void loadData() {
        List<Product> finalProducts = productService.findAllFinalProductsWithDependencies();
        productGrid.setItems(finalProducts);
    }

    private void filterProductGrid() {
        String searchTerm = searchField.getValue().toLowerCase();
        List<Product> filteredProducts = productService.findAllFinalProductsWithDependencies().stream()
                .filter(product -> product.getName().toLowerCase().contains(searchTerm))
                .toList();
        productGrid.setItems(filteredProducts);
    }

    private void showProductDetails(Product product) {
        productDetailsPanel.removeAll();
        productDetailsPanel.setVisible(true);
        
        // Product title with close button
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        titleLayout.setAlignItems(Alignment.CENTER);
        
        H3 productTitle = new H3("📊 " + product.getName());
        productTitle.addClassName("product-details-title");
        productTitle.getStyle().set("margin", "0");
        
        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        closeButton.addClickListener(e -> {
            productGrid.asSingleSelect().clear();
            selectedProduct = null;
            hideProductDetails();
        });
        closeButton.getElement().setAttribute("aria-label", "Fechar detalhes");
        
        titleLayout.add(productTitle, closeButton);
        
        // Create scrollable content for details
        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(true);
        contentLayout.getStyle().set("overflow-y", "auto");
        contentLayout.getStyle().set("max-height", "calc(100vh - 300px)");
        
        // Recipe details
        VerticalLayout recipeSection = createRecipeDetailsSection(product);
        
        // Cost breakdown
        VerticalLayout costSection = createCostBreakdownSection(product);
        
        // Pricing section
        VerticalLayout pricingSection = createPricingSection(product);
        
        contentLayout.add(recipeSection, costSection, pricingSection);
        
        productDetailsPanel.add(titleLayout, contentLayout);
    }
    
    private void hideProductDetails() {
        productDetailsPanel.setVisible(false);
        productDetailsPanel.removeAll();
    }

    private VerticalLayout createRecipeDetailsSection(Product product) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);
        
        H4 sectionTitle = new H4("📋 Receita");
        section.add(sectionTitle);
        
        Optional<Recipe> recipeOpt = product.getProductRecipe();
        if (recipeOpt.isEmpty()) {
            Paragraph noRecipe = new Paragraph("❌ Este produto não possui receita definida.");
            noRecipe.getStyle().set("color", "var(--lumo-error-text-color)");
            section.add(noRecipe);
            return section;
        }
        
        Recipe recipe = recipeOpt.get();
        
        // Yield info
        Paragraph yieldInfo = new Paragraph("🎯 Rendimento: " + recipe.getYieldQuantity() + " unidades");
        section.add(yieldInfo);
        
        // Ingredients grid
        if (recipe.getRecipes().isEmpty()) {
            Paragraph noIngredients = new Paragraph("⚠️ Receita não possui ingredientes.");
            noIngredients.getStyle().set("color", "var(--lumo-warning-text-color)");
            section.add(noIngredients);
        } else {
            Grid<RecipeIngredientDisplay> ingredientsGrid = new Grid<>(RecipeIngredientDisplay.class, false);
            ingredientsGrid.addColumn(RecipeIngredientDisplay::getIngredientName).setHeader("Ingrediente").setFlexGrow(2);
            ingredientsGrid.addColumn(RecipeIngredientDisplay::getQuantity).setHeader("Quantidade").setFlexGrow(1);
            ingredientsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
            ingredientsGrid.setAllRowsVisible(true);
            
            // Prepare data
            List<RecipeIngredientDisplay> ingredientsList = recipe.getRecipes().stream()
                .map(ingredient -> {
                    String ingredientName = getRawMaterialName(ingredient.getRawMaterialId());
                    String quantity = ingredient.getQuantity().value().stripTrailingZeros().toPlainString() + 
                                    " " + ingredient.getQuantity().unitMeasurement().getUnit();
                    return new RecipeIngredientDisplay(ingredientName, quantity);
                })
                .toList();
            
            ingredientsGrid.setItems(ingredientsList);
            section.add(ingredientsGrid);
        }
        
        return section;
    }

    private VerticalLayout createCostBreakdownSection(Product product) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);
        
        H4 sectionTitle = new H4("💰 Análise de Custos");
        section.add(sectionTitle);
        
        try {
            Money productionCost = calculateProductionCost(product);
            Optional<Recipe> recipeOpt = product.getProductRecipe();
            
            if (recipeOpt.isPresent() && !recipeOpt.get().getRecipes().isEmpty()) {
                Recipe recipe = recipeOpt.get();
                
                // Cost breakdown grid
                Grid<CostAnalysisDisplay> costGrid = new Grid<>(CostAnalysisDisplay.class, false);
                costGrid.addColumn(CostAnalysisDisplay::getIngredientName).setHeader("Ingrediente").setFlexGrow(2);
                costGrid.addColumn(CostAnalysisDisplay::getQuantity).setHeader("Quantidade").setFlexGrow(1);
                costGrid.addColumn(CostAnalysisDisplay::getUnitCost).setHeader("Preço Unit.").setFlexGrow(1);
                costGrid.addColumn(CostAnalysisDisplay::getTotalCost).setHeader("Custo Total").setFlexGrow(1);
                costGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
                costGrid.setAllRowsVisible(true);
                
                // Prepare cost data
                List<CostAnalysisDisplay> costList = recipe.getRecipes().stream()
                    .map(ingredient -> {
                        Optional<Product> rawMaterialOpt = productService.findById(ingredient.getRawMaterialId());
                        if (rawMaterialOpt.isPresent()) {
                            Product rawMaterial = rawMaterialOpt.get();
                            String ingredientName = rawMaterial.getName();
                            String quantity = ingredient.getQuantity().value().stripTrailingZeros().toPlainString() + 
                                            " " + ingredient.getQuantity().unitMeasurement().getUnit();
                            
                            if (rawMaterial.getCurrentPrice().isEmpty() || rawMaterial.getCurrentPrice().get().equals(Money.ZERO)) {
                                return new CostAnalysisDisplay(ingredientName, quantity, "❌ SEM PREÇO", "R$ 0,00");
                            } else {
                                String unitCost = formatMoney(rawMaterial.getCurrentPrice().get());
                                Money ingredientCost = rawMaterial.calculateIngredientCost(ingredient.getQuantity());
                                String totalCost = formatMoney(ingredientCost);
                                return new CostAnalysisDisplay(ingredientName, quantity, unitCost, totalCost);
                            }
                        }
                        return new CostAnalysisDisplay("Produto não encontrado", "0", "R$ 0,00", "R$ 0,00");
                    })
                    .toList();
                
                costGrid.setItems(costList);
                section.add(costGrid);
                
                // Summary information
                VerticalLayout summaryLayout = new VerticalLayout();
                summaryLayout.setPadding(false);
                summaryLayout.setSpacing(false);
                
                Paragraph totalCost = new Paragraph("🏭 Custo Total de Produção: " + formatMoney(productionCost));
                totalCost.getStyle().set("font-weight", "bold");
                totalCost.getStyle().set("font-size", "1.1em");
                summaryLayout.add(totalCost);
                
                // Unit cost
                if (recipe.getYieldQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    Money unitCost = productService.calculateRecipeUnitCost(recipe);
                    Paragraph unitCostInfo = new Paragraph("📦 Custo por Unidade: " + formatMoney(unitCost));
                    unitCostInfo.getStyle().set("font-weight", "bold");
                    unitCostInfo.getStyle().set("font-size", "1.1em");
                    unitCostInfo.getStyle().set("color", "var(--lumo-primary-text-color)");
                    summaryLayout.add(unitCostInfo);
                }
                
                section.add(summaryLayout);
                
            } else {
                Paragraph noCost = new Paragraph("❌ Não é possível calcular custos sem receita completa.");
                noCost.getStyle().set("color", "var(--lumo-error-text-color)");
                section.add(noCost);
            }
            
        } catch (Exception e) {
            Paragraph errorMsg = new Paragraph("⚠️ Erro no cálculo de custos: " + e.getMessage());
            errorMsg.getStyle().set("color", "var(--lumo-error-text-color)");
            section.add(errorMsg);
        }
        
        return section;
    }

    private VerticalLayout createPricingSection(Product product) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);
        
        H4 sectionTitle = new H4("🏷️ Precificação");
        section.add(sectionTitle);
        
        ProfitMargin margin = product.getProfitMargin();
        if (margin == null) {
            Paragraph noMargin = new Paragraph("❌ Margem de lucro não definida.");
            noMargin.getStyle().set("color", "var(--lumo-error-text-color)");
            
            Button defineMarginButton = new Button("Definir Margem", VaadinIcon.PLUS.create());
            defineMarginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            defineMarginButton.addClickListener(e -> editProfitMargin(product));
            defineMarginButton.setEnabled(hasRecipe(product));
            
            section.add(noMargin, defineMarginButton);
        } else {
            // Pricing information grid
            Grid<PricingInfoDisplay> pricingGrid = new Grid<>(PricingInfoDisplay.class, false);
            pricingGrid.addColumn(PricingInfoDisplay::getItem).setHeader("Item").setFlexGrow(2);
            pricingGrid.addColumn(PricingInfoDisplay::getValue).setHeader("Valor").setFlexGrow(1);
            pricingGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
            pricingGrid.setAllRowsVisible(true);
            
            // Prepare pricing data
            List<PricingInfoDisplay> pricingList = new ArrayList<>();
            
            try {
                // Margin
                String marginPercent = margin.percent().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
                pricingList.add(new PricingInfoDisplay("📈 Margem de Lucro", marginPercent));
                
                // Unit cost
                Money unitCost = productService.calculateRecipeUnitCost(product.getProductRecipe().get());
                pricingList.add(new PricingInfoDisplay("📦 Custo por Unidade", formatMoney(unitCost)));
                
                // Sale price
                Money salePrice = calculateSalePrice(product);
                pricingList.add(new PricingInfoDisplay("💵 Preço de Venda", formatMoney(salePrice)));
                
                // Profit
                Money profit = salePrice.minus(unitCost);
                pricingList.add(new PricingInfoDisplay("💰 Lucro por Unidade", formatMoney(profit)));
                
            } catch (Exception e) {
                pricingList.add(new PricingInfoDisplay("⚠️ Erro", e.getMessage()));
            }
            
            pricingGrid.setItems(pricingList);
            section.add(pricingGrid);
            
            // Action buttons
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setPadding(false);
            buttonLayout.setSpacing(true);
            
            Button editMarginButton = new Button("Editar Margem", VaadinIcon.EDIT.create());
            editMarginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            editMarginButton.addClickListener(e -> editProfitMargin(product));
            
            buttonLayout.add(editMarginButton);
            section.add(buttonLayout);
        }
        
        return section;
    }

    private void editProfitMargin(Product product) {
        if (!hasRecipe(product)) {
            Notification notification = Notification.show(
                "Este produto precisa de uma receita completa antes de definir margem de lucro!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Margem de Lucro - " + product.getName());
        
        VerticalLayout layout = new VerticalLayout();
        
        // Current cost display
        try {
            Money productionCost = calculateProductionCost(product);
            Money unitCost = productService.calculateRecipeUnitCost(product.getProductRecipe().get());
            
            Paragraph costInfo = new Paragraph("💰 Custo de Produção: " + formatMoney(productionCost));
            Paragraph unitCostInfo = new Paragraph("📦 Custo por Unidade: " + formatMoney(unitCost));
            layout.add(costInfo, unitCostInfo);
            
        } catch (Exception e) {
            Paragraph errorMsg = new Paragraph("⚠️ Erro ao calcular custos: " + e.getMessage());
            errorMsg.getStyle().set("color", "var(--lumo-error-text-color)");
            layout.add(errorMsg);
        }
        
        // Margin field
        BigDecimalField marginField = new BigDecimalField("Margem de Lucro (%)");
        marginField.setRequired(true);
        // Note: Vaadin BigDecimalField doesn't have setStep, setMin, setMax methods
        marginField.setHelperText("Ex: 50 para 50% de margem");
        
        if (product.getProfitMargin() != null) {
            BigDecimal currentMargin = product.getProfitMargin().percent().multiply(BigDecimal.valueOf(100));
            marginField.setValue(currentMargin);
        } else {
            marginField.setValue(BigDecimal.valueOf(50)); // Default 50%
        }
        
        // Preview section
        Div previewSection = new Div();
        
        // Live preview calculation
        marginField.addValueChangeListener(e -> {
            updatePreview(previewSection, product, e.getValue());
        });
        
        // Initial preview
        updatePreview(previewSection, product, marginField.getValue());
        
        layout.add(marginField, previewSection);
        
        // Buttons
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button saveButton = new Button("Salvar", saveEvent -> {
            if (marginField.getValue() != null && marginField.getValue().compareTo(BigDecimal.ZERO) >= 0) {
                try {
                    BigDecimal marginPercent = marginField.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    ProfitMargin newMargin = new ProfitMargin(marginPercent);
                    product.defineProfitMargin(newMargin);
                    productService.save(product);
                    
                    loadData(); // Refresh grid
                    showProductDetails(product); // Refresh details
                    
                    Notification notification = Notification.show("Margem de lucro atualizada com sucesso!");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    dialog.close();
                    
                } catch (Exception ex) {
                    Notification notification = Notification.show("Erro ao salvar margem: " + ex.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Por favor, insira uma margem válida (maior ou igual a 0%)!");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        
        buttonsLayout.add(cancelButton, saveButton);
        layout.add(buttonsLayout);
        
        dialog.add(layout);
        dialog.setWidth("500px");
        dialog.open();
    }

    private void updatePreview(Div previewSection, Product product, BigDecimal marginPercent) {
        previewSection.removeAll();
        
        if (marginPercent == null || marginPercent.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        
        try {
            BigDecimal marginDecimal = marginPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            ProfitMargin tempMargin = new ProfitMargin(marginDecimal);
            
            Money productionCost = calculateProductionCost(product);
            Money unitCost = productService.calculateRecipeUnitCost(product.getProductRecipe().get());
            BigDecimal marginFactor = BigDecimal.ONE.add(marginDecimal);
            Money salePrice = unitCost.multiply(marginFactor);
            Money profit = salePrice.minus(unitCost);
            
            H5 previewTitle = new H5("📋 Preview do Preço");
            Paragraph unitCostInfo = new Paragraph("📦 Custo por Unidade: " + formatMoney(unitCost));
            Paragraph salePriceInfo = new Paragraph("💵 Preço de Venda: " + formatMoney(salePrice));
            Paragraph profitInfo = new Paragraph("💰 Lucro por Unidade: " + formatMoney(profit));
            
            salePriceInfo.getStyle().set("font-weight", "bold");
            profitInfo.getStyle().set("color", "var(--lumo-success-text-color)");
            
            previewSection.add(previewTitle, unitCostInfo, salePriceInfo, profitInfo);
            
        } catch (Exception e) {
            Paragraph errorMsg = new Paragraph("⚠️ Erro no preview: " + e.getMessage());
            errorMsg.getStyle().set("color", "var(--lumo-error-text-color)");
            previewSection.add(errorMsg);
        }
    }

    // Helper methods
    private boolean hasRecipe(Product product) {
        Optional<Recipe> recipe = product.getProductRecipe();
        return recipe.isPresent() && !recipe.get().getRecipes().isEmpty();
    }

    private Money calculateProductionCost(Product product) {
        try {
            if (!hasRecipe(product)) {
                return Money.ZERO;
            }
            Recipe recipe = product.getProductRecipe().get();
            return productService.calculateRecipeTotalCost(recipe);
        } catch (Exception e) {
            return Money.ZERO;
        }
    }

    private Money calculateSalePrice(Product product) {
        try {
            if (product.getProfitMargin() == null) {
                System.err.println("DEBUG: Produto " + product.getName() + " não tem margem de lucro definida");
                return Money.ZERO;
            }
            if (!hasRecipe(product)) {
                System.err.println("DEBUG: Produto " + product.getName() + " não tem receita");
                return Money.ZERO;
            }
            
            // Debug detalhado
            System.err.println("DEBUG: Calculando preço para " + product.getName());
            System.err.println("DEBUG: Margem de lucro: " + product.getProfitMargin().percent());
            System.err.println("DEBUG: Rendimento da receita: " + product.getProductRecipe().get().getYieldQuantity());
            
            return priceCalculatorService.salePriceCalcule(product);
        } catch (Exception e) {
            // Log da exceção para debug
            System.err.println("Erro ao calcular preço de venda para produto " + product.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return Money.ZERO;
        }
    }

    private String getRawMaterialName(UUID rawMaterialId) {
        return productService.findById(rawMaterialId)
                .map(Product::getName)
                .orElse("Produto não encontrado");
    }

    private String formatMoney(Money money) {
        if (money == null || money == Money.ZERO) {
            return "R$ 0,00";
        }
        return "R$ " + money.value().setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
    }

    private String formatProfitMargin(ProfitMargin margin) {
        if (margin == null) {
            return "Não definida";
        }
        return margin.percent().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
    }
}