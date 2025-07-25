package br.com.alg.scg.infra.web.views.purchase;

import br.com.alg.scg.application.service.PurchaseService;
import br.com.alg.scg.domain.purchases.entity.Purchase;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(purchaseService.findAll());
    }
}