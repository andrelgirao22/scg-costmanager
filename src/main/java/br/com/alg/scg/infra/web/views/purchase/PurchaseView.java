package br.com.alg.scg.infra.web.views.purchase;

import br.com.alg.scg.application.service.PurchaseService;
import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.domain.purchases.entity.PurchaseItem;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

/**
 * View para visualização de compras
 */
@PageTitle("Compras")
@Route(value = "purchases", layout = MainLayout.class)
public class PurchaseView extends Main {

    private final PurchaseService purchaseService;
    private Grid<Purchase> grid = new Grid<>(Purchase.class, false);

    @Autowired
    public PurchaseView(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
        
        addClassName("purchase-view");
        setSizeFull();
        getStyle().set("padding", "var(--lumo-space-l)");
        
        configureGrid();
        add(createHeader(), createContent());
        updateList();
    }

    private Component createHeader() {
        H2 header = new H2("Histórico de Compras");
        header.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.LARGE);
        return header;
    }

    private Component createContent() {
        VerticalLayout content = new VerticalLayout(grid);
        content.setSizeFull();
        content.setPadding(false);
        return content;
    }

    private void configureGrid() {
        grid.addClassName("purchase-grid");
        grid.setSizeFull();
        
        grid.addColumn(purchase -> purchase.getSupplier().getName()).setHeader("Fornecedor").setSortable(true);
        grid.addColumn(purchase -> purchase.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
             .setHeader("Data/Hora").setSortable(true);
        grid.addColumn(purchase -> purchase.getItems().size()).setHeader("Qtd Itens");
        grid.addColumn(purchase -> "R$ " + purchase.getTotalCost().value())
             .setHeader("Total").setSortable(true);
        
        // Configurar detalhes expandíveis dos itens
        grid.setItemDetailsRenderer(createItemDetailsRenderer());
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private ComponentRenderer<Component, Purchase> createItemDetailsRenderer() {
        return new ComponentRenderer<>(this::createPurchaseDetails);
    }

    private Component createPurchaseDetails(Purchase purchase) {
        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.addClassName("purchase-details");
        detailsLayout.setPadding(true);
        detailsLayout.setSpacing(true);
        detailsLayout.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        detailsLayout.getStyle().set("border-radius", "var(--lumo-border-radius-m)");

        // Cabeçalho dos detalhes
        H4 detailsHeader = new H4("Itens da Compra");
        detailsHeader.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL);
        
        detailsLayout.add(detailsHeader);

        // Grid dos itens
        Grid<PurchaseItem> itemsGrid = new Grid<>(PurchaseItem.class, false);
        itemsGrid.addClassName("purchase-items-grid");
        itemsGrid.setHeight("200px");
        itemsGrid.setAllRowsVisible(true);

        // Colunas do grid de itens
        itemsGrid.addColumn(item -> item.getProduct().getName())
                .setHeader("Produto")
                .setFlexGrow(2);

        itemsGrid.addColumn(item -> item.getQuantity().value() + " " + item.getQuantity().unitMeasurement().getUnit())
                .setHeader("Quantidade")
                .setFlexGrow(1);

        itemsGrid.addColumn(item -> "R$ " + item.getUnitCost().value())
                .setHeader("Custo Unit.")
                .setFlexGrow(1);

        itemsGrid.addColumn(item -> "R$ " + item.getSubtotal().value())
                .setHeader("Subtotal")
                .setFlexGrow(1);

        // Definir os itens
        itemsGrid.setItems(purchase.getItems());

        detailsLayout.add(itemsGrid);

        // Informações adicionais
        HorizontalLayout summaryLayout = new HorizontalLayout();
        summaryLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        summaryLayout.setWidthFull();

        Span itemCountInfo = new Span("Total de itens: " + purchase.getItems().size());
        itemCountInfo.addClassNames(LumoUtility.FontWeight.MEDIUM);

        Span totalInfo = new Span("Total da compra: R$ " + purchase.getTotalCost().value());
        totalInfo.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);

        summaryLayout.add(itemCountInfo, totalInfo);
        detailsLayout.add(summaryLayout);

        return detailsLayout;
    }

    private void updateList() {
        grid.setItems(purchaseService.findAll());
    }
}