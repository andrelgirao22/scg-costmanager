package br.com.alg.scg.infra.web.config;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;

/**
 * Configuração global da aplicação Vaadin
 * O tema Lumo é usado por padrão, então não precisa ser especificado
 */
@CssImport("./styles/shared-styles.css")
public class AppShellConfig implements AppShellConfigurator {
    
}