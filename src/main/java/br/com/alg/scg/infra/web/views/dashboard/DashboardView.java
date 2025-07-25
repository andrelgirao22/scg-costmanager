package br.com.alg.scg.infra.web.views.dashboard;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.PurchaseService;
import br.com.alg.scg.application.service.SaleService;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dashboard principal da aplicação
 */
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DashboardView extends Main {

    private final ProductService productService;
    private final ClientService clientService;
    private final SaleService saleService;
    private final PurchaseService purchaseService;

    @Autowired
    public DashboardView(ProductService productService, ClientService clientService, 
                        SaleService saleService, PurchaseService purchaseService) {
        this.productService = productService;
        this.clientService = clientService;
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        
        addClassName("dashboard-view");
        
        add(createHeader());
        add(createStatsBoard());
        add(createChartsSection());
    }

    private Component createHeader() {
        H2 header = new H2("Dashboard - SCG Cost Manager");
        header.addClassNames(LumoUtility.Margin.Bottom.XLARGE, LumoUtility.Margin.Top.XLARGE);
        return header;
    }

    private Component createStatsBoard() {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        
        statsLayout.add(
            createStatCard("Produtos", String.valueOf(productService.count()), VaadinIcon.PACKAGE, "primary"),
            createStatCard("Clientes", String.valueOf(clientService.count()), VaadinIcon.USERS, "success"),
            createStatCard("Vendas", String.valueOf(saleService.count()), VaadinIcon.CART, "contrast"),
            createStatCard("Compras", String.valueOf(purchaseService.count()), VaadinIcon.TRUCK, "error")
        );
        return statsLayout;
    }

    private Component createStatCard(String title, String value, VaadinIcon icon, String theme) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("stat-card");
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );
        card.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Icon cardIcon = icon.create();
        cardIcon.addClassName("text-" + theme);
        cardIcon.setSize("20px");

        header.add(titleSpan, cardIcon);

        H2 valueH2 = new H2(value);
        valueH2.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.NONE);

        card.add(header, valueH2);
        return card;
    }

    private Component createChartsSection() {
        VerticalLayout chartsSection = new VerticalLayout();
        chartsSection.addClassName("charts-section");
        
        H2 chartsHeader = new H2("Resumo Rápido");
        chartsHeader.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);
        
        // Placeholder para futuros gráficos
        VerticalLayout placeholder = new VerticalLayout();
        placeholder.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.XLARGE,
            LumoUtility.AlignItems.CENTER
        );
        placeholder.setHeight("300px");
        
        Span placeholderText = new Span("📊 Gráficos de vendas e custos serão implementados aqui");
        placeholderText.addClassNames(LumoUtility.TextColor.SECONDARY);
        placeholder.add(placeholderText);
        
        chartsSection.add(chartsHeader, placeholder);
        return chartsSection;
    }
}