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
import com.vaadin.flow.component.dependency.CssImport;

/**
 * Layout principal da aplicação com menu lateral
 */
@AnonymousAllowed
@CssImport("./styles/shared-styles.css")
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

        // Produtos
        nav.addItem(new SideNavItem("Produtos", "/products", VaadinIcon.PACKAGE.create()));

        // Clientes
        nav.addItem(new SideNavItem("Clientes", "/clients", VaadinIcon.USERS.create()));

        // Vendas
        nav.addItem(new SideNavItem("Vendas", "/sales", VaadinIcon.CART.create()));

        // Compras
        nav.addItem(new SideNavItem("Compras", "/purchases", VaadinIcon.TRUCK.create()));

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