package br.unifesp.ict.seg.icodegenie.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.domain.Expander;
import br.unifesp.ict.seg.geniesearchapi.services.searchaqe.infrastructure.SourcererQueryBuilder;
import br.unifesp.ict.seg.icodegenie.model.Method;
import edu.uci.ics.sourcerer.services.search.adapter.SearchResult;
import edu.uci.ics.sourcerer.services.search.adapter.SingleResult;

@Route
@PWA(name = "ICodeGenie: Search and analyze methods in a sourcerer repository", shortName = "ICodeGenie")
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private MethodDetailView methodDetailView = new MethodDetailView(this);
	private StatusView statusView = new StatusView(this);

	private TextField searchField = new TextField();
	private Button searchHelpButton = new Button("help");
	private Button repoStatusButton = new Button("status");
	private Label searchExpressionLabel = new Label();
	private Button searchButton = new Button("buscar");
	private Button newSearchButton = new Button("nova busca");
	private Button sourcererQueryButton = new Button("query");
	private Label sourcererQueryLabel = new Label();
	private Grid<Method> sourcererResultGrid = new Grid<>(Method.class);

	private String exprMethodName = "";
	private String exprReturnType = "";
	private String exprParams = "";
	private Long exprEntityId = null;
	private String exprExpanders = "";

	public MainView() {
		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

	private void buildLayout() {
		setVisible(true);

		HorizontalLayout inputToolbar = new HorizontalLayout(searchField, searchHelpButton, repoStatusButton);
		HorizontalLayout searchToolbar = new HorizontalLayout(searchExpressionLabel, searchButton, newSearchButton, sourcererQueryButton);

		HorizontalLayout mainContent = new HorizontalLayout(sourcererResultGrid, methodDetailView, statusView);
		mainContent.setSizeFull();

		add(inputToolbar, searchToolbar, sourcererQueryLabel, mainContent);

		setSizeFull();
	}

	//TODO ícone nos botões
	private void configureComponents() {

		searchField.setPlaceholder("Filter by name...");
		searchField.setClearButtonVisible(true);
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.focus();

		sourcererResultGrid.setSizeFull();
		sourcererResultGrid.setColumns("entityId", "fqn");

		searchButton.setEnabled(false);
		sourcererQueryButton.setEnabled(false);
		sourcererQueryLabel.setEnabled(false);
		this.updateComponentsVisibility(true);
	}

	private void hookLogicToComponents() {

		searchField.addValueChangeListener(e -> updateSearchExpression());
		searchHelpButton.addClickListener(e -> showFilterExamples());
		repoStatusButton.addClickListener(e -> statusView.enter());
		sourcererQueryButton.addClickListener(e -> showSourcererQuery());
		searchButton.addClickListener(e -> search());
		newSearchButton.addClickListener(e -> newSearch());

		//TODO duplo clique no mesmo registro dá NPE
		sourcererResultGrid.asSingleSelect().addValueChangeListener(e -> methodDetailView.enter(sourcererResultGrid.asSingleSelect().getValue()));
	}

	//TODO simplificar e clarear a parte de exibição e habilitação dos componentes
	private void updateComponentsVisibility(boolean inputPhase) {
		searchField.setEnabled(inputPhase);
		searchHelpButton.setEnabled(inputPhase);
		searchExpressionLabel.setEnabled(!inputPhase);
		newSearchButton.setEnabled(!inputPhase);
		sourcererResultGrid.setEnabled(!inputPhase);
	}

	private void updateSearchExpression() {

		boolean isValidExpression = this.isValidExpression(searchField.getValue());

		searchExpressionLabel.setText(this.getExpressionLabel());
		if (isValidExpression) {
			searchExpressionLabel.setEnabled(true);
			searchButton.setEnabled(true);
			sourcererQueryButton.setEnabled(true);
			sourcererQueryLabel.setText(this.getSourcererQueryLabel());
		} else {
			searchExpressionLabel.setEnabled(false);
			searchButton.setEnabled(false);
			sourcererQueryButton.setEnabled(false);
			sourcererQueryLabel.setText("");
		}
	}

	private boolean isValidExpression(String searchTerm) {

		// remove duplicates spaces
		String term = StringUtils.trim(searchTerm).replaceAll(" +", " ");

		if (term.length() < 3)
			return false;

		exprReturnType = "";
		exprMethodName = "";
		exprParams = "";
		exprEntityId = null;
		exprExpanders = "";

		// Expand validation
		if (term.contains(":")) {

			// only once ':'
			String dot2 = StringUtils.replace(term, ":", "");
			if (dot2.length() != term.length() - 1)
				return false;

			String[] methodExpand = StringUtils.split(term, ":");
			if (methodExpand.length != 2)
				return false;

			term = methodExpand[0].trim();
			String expand = methodExpand[1];

			// Up to 3
			String[] expandersArray = StringUtils.split(expand, ",");
			if (expandersArray.length == 0 || expandersArray.length > 3)
				return false;

			if (expandersArray.length == 1 && "*".equals(expandersArray[0].trim())) {
				exprExpanders = Expander.WORDNET_EXPANDER + ", " + Expander.TYPE_EXPANDER + ", " + Expander.CODE_VOCABULARY_EXPANDER;
			} else {
				for (String exp : expandersArray) {
					if ("w".equalsIgnoreCase(exp.trim())) {
						if(exprExpanders.contains(Expander.WORDNET_EXPANDER))
							return false;
						exprExpanders += Expander.WORDNET_EXPANDER + ", ";
					} else if ("t".equalsIgnoreCase(exp.trim())) {
						if(exprExpanders.contains(Expander.TYPE_EXPANDER))
							return false;
						exprExpanders += Expander.TYPE_EXPANDER + ", ";
					} else if ("c".equalsIgnoreCase(exp.trim())) {
						if(exprExpanders.contains(Expander.CODE_VOCABULARY_EXPANDER))
							return false;
						exprExpanders += Expander.CODE_VOCABULARY_EXPANDER + ", ";
					} else
						return false;
				}
				exprExpanders = StringUtils.removeEnd(exprExpanders, ", ");
			}
		}

		if (term.contains("(")) {
			int open = term.indexOf("(");
			int close = term.indexOf(")");
			if (open < 0 || close < 0 || open > close)
				return false;

			String[] twoFirsts = StringUtils.split(term.substring(0, open), " ");
			if (twoFirsts.length != 2)
				return false;

			exprReturnType = twoFirsts[0];
			exprMethodName = twoFirsts[1];

			String openTest = StringUtils.replace(term, "(", "");
			String closeTest = StringUtils.replace(term, ")", "");
			if (openTest.length() != term.length() - 1 || closeTest.length() != term.length() - 1)
				return false;

			exprParams = StringUtils.substring(term, open + 1, close);

			if (StringUtils.isBlank(exprParams))
				return false;

			if ("*".equals(exprReturnType) && "*".equals(exprMethodName) && "*".equals(exprParams))
				return false;

			return true;
		}

		if (StringUtils.isNumeric(term)) {
			exprEntityId = new Long(term);
			return true;
		}

		if (StringUtils.split(term, " ").length != 1)
			return false;

		if (StringUtils.split(term, " ").length == 1) {
			exprReturnType = "*";
			exprMethodName = term;
			exprParams = "*";

			if ("*".equals(exprReturnType) && "*".equals(exprMethodName) && "*".equals(exprParams))
				return false;

			return true;
		}

		return false;
	}

	private String getExpressionLabel() {
		String label = "";
		if (exprEntityId != null) {
			label = "Entity_id = " + exprEntityId;
		} else {
			label = " Return: " + exprReturnType;
			label += " Method name: " + exprMethodName;
			label += " Params: " + exprParams;
			label += StringUtils.isBlank(exprExpanders) ? "" : " Expanders: " + exprExpanders;
		}

		return label;
	}

	private String getSourcererQueryLabel() {
		String query = "";
		try {
			if(exprEntityId != null)
				query = this.getSourcererQueryBuilder().getSourcererExpandedQuery(exprEntityId);
			else
				query = this.getSourcererQueryBuilder().getSourcererExpandedQuery(exprMethodName, exprReturnType, exprParams);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return query;
	}

	private void search() {
		SearchResult result = null;
		try {
			if(exprEntityId != null)
				result = this.getSourcererQueryBuilder().search(exprEntityId);
			else
				result = this.getSourcererQueryBuilder().search(exprMethodName, exprReturnType, exprParams);
				
			List<Method> methods = this.getMethods(result);
			sourcererResultGrid.setItems(methods);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.updateComponentsVisibility(false);
	}

	private List<Method> getMethods(SearchResult result) throws Exception {
		List<Method> methods = new ArrayList<Method>();

		if (result == null)
			return methods;

		for (SingleResult singleResult : result.getResults(0, result.getNumFound())) {
			methods.add(new Method(singleResult));
		}
		return methods;
	}

	private void newSearch() {
		this.updateComponentsVisibility(true);
		searchField.focus();
	}

	private void showSourcererQuery() {
		sourcererQueryLabel.setVisible(!sourcererQueryLabel.isVisible());
	}

	private SourcererQueryBuilder getSourcererQueryBuilder() {
		String r = this.exprReturnType;
		String p = this.exprParams;

		String expanders = this.exprExpanders;
		boolean relaxReturn = "*".equals(r) ? true : false;
		boolean relaxParams = "*".equals(p) ? true : false;
		boolean contextRelevants = false;
		boolean filterMethodNameTermsByParameter = false;

		SourcererQueryBuilder sourcererQueryBuilder = null;
		try {
			sourcererQueryBuilder = new SourcererQueryBuilder(expanders, relaxReturn, relaxParams, contextRelevants, filterMethodNameTermsByParameter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourcererQueryBuilder;
	}

	private void showFilterExamples() {
		Notification.show("Exemplos...");
	}
}
