package br.unifesp.ict.seg.icodegenie.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class StatusView extends FormLayout {

	private static final long serialVersionUID = 1L;

	private MainView mainView;

	private Label entityIdLabel = new Label("status: " + "valor aqui");
    private Button closeButton = new Button("Fechar");

    public StatusView(MainView mainView) {
        this.mainView = mainView;

        setVisible(false);

        HorizontalLayout buttons = new HorizontalLayout(closeButton);
        //close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(entityIdLabel, buttons);

        closeButton.addClickListener(event -> close());
    }

	public void enter() {
		setVisible(true);
    }

	private void close() {
    	setVisible(false);
    }
}
