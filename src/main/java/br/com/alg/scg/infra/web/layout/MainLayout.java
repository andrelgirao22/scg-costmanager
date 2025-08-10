package br.com.alg.scg.infra.web.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Layout principal da aplicação com menu lateral
 */
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addClassName("main-layout");
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Header header = new Header();
        header.add(createLogoSection());

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller);
    }

    private Span createLogoSection() {
        Icon logoIcon = new Icon(VaadinIcon.CUBES);
        logoIcon.setSize("24px");
        logoIcon.addClassNames(LumoUtility.TextColor.PRIMARY);
        
        Span logoText = new Span("SCG Cost Manager");
        logoText.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.HEADER);
        
        Span logo = new Span(logoIcon, logoText);
        logo.addClassNames(
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Display.FLEX,
            LumoUtility.FontSize.LARGE,
            LumoUtility.Gap.SMALL,
            LumoUtility.Height.XLARGE,
            LumoUtility.Padding.Horizontal.MEDIUM
        );
        return logo;
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addClassName("main-nav");

        // Dashboard
        nav.addItem(new SideNavItem("Dashboard", "/dashboard", VaadinIcon.DASHBOARD.create()));

        // Produtos (com submenu)
        SideNavItem productsMenu = new SideNavItem("Produtos");
        productsMenu.setPrefixComponent(VaadinIcon.PACKAGE.create());
        productsMenu.addItem(new SideNavItem("Matérias-Primas", "/raw-materials"));
        productsMenu.addItem(new SideNavItem("Produtos Finais", "/final-products"));
        productsMenu.addItem(new SideNavItem("Receitas", "/recipes"));
        productsMenu.addItem(new SideNavItem("Formação de Preços", "/price-formation"));
        nav.addItem(productsMenu);

        // Clientes
        nav.addItem(new SideNavItem("Clientes", "/clients", VaadinIcon.USERS.create()));

        // Vendas (com submenu)
        SideNavItem salesMenu = new SideNavItem("Vendas");
        salesMenu.setPrefixComponent(VaadinIcon.CART.create());
        salesMenu.addItem(new SideNavItem("Nova", "/sale-operation"));
        salesMenu.addItem(new SideNavItem("Histórico", "/sales"));
        nav.addItem(salesMenu);

        // Compras (com submenu)
        SideNavItem purchasesMenu = new SideNavItem("Compras");
        purchasesMenu.setPrefixComponent(VaadinIcon.TRUCK.create());
        purchasesMenu.addItem(new SideNavItem("Nova", "/purchase-operation"));
        purchasesMenu.addItem(new SideNavItem("Histórico", "/purchases"));
        nav.addItem(purchasesMenu);

        return nav;
    }




    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        try {
            if (getContent() != null) {
                PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
                return title == null ? "SCG Cost Manager" : title.value();
            }
            return "SCG Cost Manager";
        } catch (Exception e) {
            return "SCG Cost Manager";
        }
    }
}