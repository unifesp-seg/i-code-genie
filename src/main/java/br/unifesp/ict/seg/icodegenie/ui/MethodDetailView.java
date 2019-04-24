package br.unifesp.ict.seg.icodegenie.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;

import br.unifesp.ict.seg.icodegenie.model.Method;

public class MethodDetailView extends FormLayout {

	private static final long serialVersionUID = 1L;

	private MainView mainView;

	private Label entityIdLabel = new Label();
    private Button closeButton = new Button("Fechar");

    public MethodDetailView(MainView mainView) {
        this.mainView = mainView; 

        setVisible(false);
        
        HorizontalLayout buttons = new HorizontalLayout(closeButton);
        //close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(entityIdLabel, buttons);

        closeButton.addClickListener(event -> close());
    }

	public void enter(Method method) {
		entityIdLabel.setTitle("title");
		entityIdLabel.setText("entity_id: " + method.getEntityId());

		setVisible(true);
    }

	private void close() {
    	setVisible(false);
    }
}
