package br.com.alg.scg.infra.web.views.components;

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

}
