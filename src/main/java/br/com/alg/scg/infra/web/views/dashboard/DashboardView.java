package br.com.alg.scg.infra.web.views.dashboard;

import br.com.alg.scg.application.service.ClientService;
import br.com.alg.scg.application.service.ProductService;
import br.com.alg.scg.application.service.PurchaseService;
import br.com.alg.scg.application.service.SaleService;
import br.com.alg.scg.infra.web.layout.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
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
 * Dashboard principal da aplicação (versão melhorada)
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
        // Adiciona padding ao redor de todo o conteúdo do dashboard
        getStyle().set("padding", LumoUtility.Padding.LARGE);

        // Não há mais necessidade de um cabeçalho H2 separado, o MainLayout já cuida do título da página.
        add(createStatsBoard());
        add(createChartsSection());
    }

    // Em br/com/alg/scg/infra/web/views/dashboard/DashboardView.java

    private Component createStatsBoard() {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.addClassName("stats-board");
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true); // Manter isso é uma boa prática

        // Criamos os cards primeiro
        Component productsCard = createStatCard("Produtos", String.valueOf(productService.count()), VaadinIcon.PACKAGE, "primary");
        Component clientsCard = createStatCard("Clientes", String.valueOf(clientService.count()), VaadinIcon.USERS, "success");
        Component salesCard = createStatCard("Vendas", String.valueOf(saleService.count()), VaadinIcon.CART, "contrast");
        Component purchasesCard = createStatCard("Compras", String.valueOf(purchaseService.count()), VaadinIcon.TRUCK, "error");

        // Adicionamos os cards ao layout
        statsLayout.add(productsCard, clientsCard, salesCard, purchasesCard);

        // **AQUI ESTÁ A CORREÇÃO MÁGICA**
        // Dizemos para cada card crescer igualmente para preencher o espaço.
        statsLayout.setFlexGrow(1, productsCard, clientsCard, salesCard, purchasesCard);

        return statsLayout;
    }

    private Component createStatCard(String title, String value, VaadinIcon icon, String colorTheme) {
        // Usamos um HorizontalLayout para o card, alinhando o ícone à esquerda e o texto à direita
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("stat-card");
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.LARGE, LumoUtility.AlignItems.CENTER);
        card.getThemeList().add("spacing-l");

        // Wrapper para o ícone com fundo colorido
        Icon cardIcon = icon.create();
        cardIcon.addClassName("text-" + colorTheme); // Estiliza a cor do ícone

        Div iconWrapper = new Div(cardIcon);
        iconWrapper.addClassName("stat-card-icon-wrapper");
        iconWrapper.addClassName("bg-" + colorTheme + "-light"); // Estiliza o fundo do wrapper

        // Layout vertical para o número e o título
        H2 valueH2 = new H2(value);
        valueH2.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.NONE);

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        VerticalLayout textLayout = new VerticalLayout(valueH2, titleSpan);
        textLayout.setSpacing(false);
        textLayout.setPadding(false);

        card.add(iconWrapper, textLayout);
        return card;
    }

    private Component createChartsSection() {
        VerticalLayout chartsSection = new VerticalLayout();
        chartsSection.addClassName("charts-section");

        H3 chartsHeader = new H3("Resumo Rápido");
        chartsHeader.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);

        // Placeholder para futuros gráficos com um visual mais elaborado
        VerticalLayout placeholder = new VerticalLayout();
        placeholder.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.XLARGE,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER
        );
        placeholder.setMinHeight("350px");

        Icon chartIcon = VaadinIcon.CHART_LINE.create();
        chartIcon.setSize("80px");
        chartIcon.addClassName(LumoUtility.TextColor.PRIMARY);

        Span placeholderText = new Span("Gráficos de desempenho de vendas e custos aparecerão aqui.");
        placeholderText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

        placeholder.add(chartIcon, placeholderText);

        chartsSection.add(chartsHeader, placeholder);
        return chartsSection;
    }
}