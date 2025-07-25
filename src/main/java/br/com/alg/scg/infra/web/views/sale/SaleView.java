package br.com.alg.scg.infra.web.views.sale;

import br.com.alg.scg.application.service.SaleService;
import br.com.alg.scg.domain.sales.entity.Sale;
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
 * View para visualização de vendas
 */
@PageTitle("Vendas")
@Route(value = "sales", layout = MainLayout.class)
public class SaleView extends Main {

    private final SaleService saleService;
    private Grid<Sale> grid = new Grid<>(Sale.class, false);

    @Autowired
    public SaleView(SaleService saleService) {
        this.saleService = saleService;
        
        addClassName("sale-view");
        setSizeFull();
        
        configureGrid();
        add(createHeader(), createContent());
        updateList();
    }

    private Component createHeader() {
        H2 header = new H2("Histórico de Vendas");
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
        grid.addClassName("sale-grid");
        grid.setSizeFull();
        
        grid.addColumn(sale -> sale.getClient().getName()).setHeader("Cliente").setSortable(true);
        grid.addColumn(sale -> sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
             .setHeader("Data/Hora").setSortable(true);
        grid.addColumn(sale -> sale.getItems().size()).setHeader("Qtd Itens");
        grid.addColumn(sale -> "R$ " + sale.getTotalValue().value())
             .setHeader("Total").setSortable(true);
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(saleService.findAll());
    }
}