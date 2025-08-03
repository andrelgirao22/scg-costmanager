package br.com.alg.scg.infra.web.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ComponentUtil {

    public static ConfirmDialog createConfirmDialog(String description) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirma essa operação?");
        dialog.setText(description);
        dialog.setCancelable(true);
        dialog.setConfirmText("Confirmar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelText("Cancelar");
        dialog.addCancelListener(e -> dialog.close());
        return dialog;
    }

    public static void configureFullSize(HasSize component) {
        component.setSizeFull();
    }

    public static void addStandardMargins(Component component) {
        component.getStyle().set("padding", "var(--lumo-space-l)");
    }

}
