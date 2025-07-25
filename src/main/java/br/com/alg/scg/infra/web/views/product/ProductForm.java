package br.com.alg.scg.infra.web.views.product;

import br.com.alg.scg.domain.product.entity.Product;
import br.com.alg.scg.domain.product.valueobject.ProductType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.math.BigDecimal;

/**
 * Formulário para edição de produtos
 */
public class ProductForm extends FormLayout {

    private TextField name = new TextField("Nome");
    private Select<ProductType> type = new Select<>();
    private BigDecimalField stock = new BigDecimalField("Estoque");

    private Button save = new Button("Salvar");
    private Button delete = new Button("Excluir");
    private Button close = new Button("Fechar");

    private Binder<Product> binder = new BeanValidationBinder<>(Product.class);
    private Product product;

    public ProductForm() {
        addClassName("product-form");

        configureFields();
        add(name, type, stock, createButtonsLayout());
        binder.bindInstanceFields(this);
    }

    private void configureFields() {
        name.setPlaceholder("Digite o nome do produto");
        name.setRequired(true);
        
        type.setLabel("Tipo");
        type.setItems(ProductType.values());
        type.setItemLabelGenerator(productType -> 
            productType == ProductType.RAW_MATERIAL ? "Matéria-Prima" : "Produto Final");
        type.setRequiredIndicatorVisible(true);
        
        stock.setLabel("Estoque Inicial");
        stock.setValue(BigDecimal.ZERO);
        //stock.setMin(BigDecimal.ZERO);
        stock.setHelperText("Quantidade em estoque");
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, product)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void setProduct(Product product) {
        this.product = product;
        
        if (product != null) {
            // Carregar dados do produto existente
            name.setValue(product.getName());
            type.setValue(product.getType());
            stock.setValue(product.getStock());
        } else {
            // Limpar campos para novo produto
            name.clear();
            type.clear();
            stock.setValue(BigDecimal.ZERO);
        }
        
        // Configurar visibilidade do botão delete
        delete.setVisible(product != null && product.getId() != null);
    }

    private void validateAndSave() {
        try {
            if (product == null || product.getId() == null) {
                // Novo produto - usar factory method
                product = Product.createFromForm(
                    name.getValue(),
                    type.getValue(),
                    stock.getValue()
                );
            } else {
                // Produto existente - usar setters
                product.setName(name.getValue());
                product.setType(type.getValue());
                product.setStock(stock.getValue());
            }
            
            fireEvent(new SaveEvent(this, product));
        } catch (Exception e) {
            // TODO: Mostrar notificação de erro
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class ProductFormEvent extends ComponentEvent<ProductForm> {
        private Product product;

        protected ProductFormEvent(ProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }

    public static class SaveEvent extends ProductFormEvent {
        SaveEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class DeleteEvent extends ProductFormEvent {
        DeleteEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class CloseEvent extends ProductFormEvent {
        CloseEvent(ProductForm source) {
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