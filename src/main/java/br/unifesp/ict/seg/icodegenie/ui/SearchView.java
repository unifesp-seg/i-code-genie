package br.unifesp.ict.seg.icodegenie.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import edu.uci.ics.sourcerer.services.search.adapter.SearchResult;

public class SearchView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private MainView mainView;
	
	private MethodDetailView methodDetailView;
	private StatusView statusView;

	private TextField searchField = new TextField();
	private Button searchHelpButton = new Button("help");
	private Button repoStatusButton = new Button("status");
	private Label searchExpressionLabel = new Label();
	private Button searchButton = new Button("buscar");
	private Button newSearchButton = new Button("nova busca");
	private Grid<SearchResult> sourcererResultGrid = new Grid<>(SearchResult.class);
	
	public SearchView(MainView mainView) {
		this.mainView = mainView;

		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

    private void buildLayout(){
    	setVisible(true);

		HorizontalLayout toolbar = new HorizontalLayout(searchField, searchHelpButton, repoStatusButton);


		methodDetailView = new MethodDetailView(mainView);
		statusView = new StatusView(mainView);
		HorizontalLayout mainContent = new HorizontalLayout(sourcererResultGrid, methodDetailView, statusView);
		mainContent.setSizeFull();

		add(toolbar, mainContent);

		setSizeFull();
	}
	
	private void configureComponents() {
        
		searchField.setPlaceholder("Filter by name...");
		searchField.setClearButtonVisible(true);
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		
		sourcererResultGrid.setSizeFull();
		//sourcererResultGrid.setColumns("firstName", "lastName", "status");

	}

	private void hookLogicToComponents() {

		//searchField.addValueChangeListener(e -> updateList());
	}

}
