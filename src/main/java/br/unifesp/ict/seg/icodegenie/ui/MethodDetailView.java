package br.unifesp.ict.seg.icodegenie.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.GenieMethodRepository;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;
import br.unifesp.ict.seg.icodegenie.model.Method;

//TODO 0 MethodDetailView: implementar

public class MethodDetailView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private MainView mainView;

	private GenieMethodRepository repository = new GenieMethodRepository();

	private Method currentMethod;

	private Details methodHeader = new Details(new Span("Method"), null);
	private Details projectHeader = new Details("Project", null);
	private Details sourceHeader = new Details("Source Code", null);
	private Details execHeader = new Details("Execute", null);

	private Anchor sliceLink = new Anchor();
	private Anchor compiledJarLink = new Anchor();

	private String okColor = "hsl(214, 90%, 52%)";

	public MethodDetailView(MainView mainView) {

		this.mainView = mainView;
		setVisible(false);

		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

	private void buildLayout() {

		add(methodHeader, projectHeader, sourceHeader, execHeader);
	}

	private void configureComponents() {

		methodHeader.getSummary().getElement().getStyle().set("color", okColor);
		projectHeader.getSummary().getElement().getStyle().set("color", okColor);
		sourceHeader.getSummary().getElement().getStyle().set("color", okColor);
		execHeader.getSummary().getElement().getStyle().set("color", okColor);

		methodHeader.setOpened(true);
		projectHeader.setOpened(true);
		sourceHeader.setOpened(true);
		execHeader.setOpened(true);
	}

	private void hookLogicToComponents() {
	}

	private void loadContent() {

		GenieMethod genieMethod = repository.findByEntityId(currentMethod.getEntityId());
		this.currentMethod.setGenieMethod(genieMethod);

		this.loadMethodContent();
		this.loadProjectContent();
		this.loadSourceContent();
		this.loadExecContent();
	}

	private Component[] getDetailsLine(String title, String value) {
		Span bold = new Span(title + ": ");
		bold.getStyle().set("font-weight", "bold");
		Component[] detailsLine = { new Paragraph(""), bold, new Span(value) };
		return detailsLine;
	}

	private Component[] getDetailsLines(String singularTitle, String pluralTitle, String[] values) {
		List<Component> detailsLines = new ArrayList<Component>();

		if (values == null)
			return null;

		Span bold = new Span();
		bold.getStyle().set("font-weight", "bold");
		if (values.length == 0) {
			bold.setText("No " + pluralTitle);
			detailsLines.add(new Paragraph(""));
			detailsLines.add(bold);
		} else if (values.length == 1) {
			bold.setText("1 " + singularTitle + ": ");
			detailsLines.add(new Paragraph(""));
			detailsLines.add(bold);
			detailsLines.add(new Span(values[0]));
		} else {
			bold.setText(values.length + " " + pluralTitle + ": ");
			detailsLines.add(new Paragraph(""));
			detailsLines.add(bold);
			for (String value : values) {
				detailsLines.add(new Paragraph(value));
			}
		}

		Component[] components = new Component[detailsLines.size()];
		components = detailsLines.toArray(components);

		return components;
	}

	private void loadMethodContent() {
		methodHeader.setContent(null);

		methodHeader.addContent(this.getDetailsLine("EntityId", currentMethod.getEntityId() + ""));
		methodHeader.addContent(this.getDetailsLine("FQN", currentMethod.getFqn()));
		methodHeader.addContent(this.getDetailsLine("Class", currentMethod.getGenieMethod().getClassName()));
		methodHeader.addContent(this.getDetailsLine("Modifiers", currentMethod.getGenieMethod().getModifiers()));
		methodHeader.addContent(this.getDetailsLine("Name", currentMethod.getGenieMethod().getMethodName()));
		methodHeader.addContent(this.getDetailsLines("Parameter", "Parameters", currentMethod.getGenieMethod().getParamsNames()));
		methodHeader.addContent(this.getDetailsLine("Return", currentMethod.getGenieMethod().getReturnType()));

		sliceLink.setHref(GenieSearchAPIConfig.getSlicedPath() + "");
		sliceLink.setTarget("_blank");
		sliceLink.setText("Slice Download");
		methodHeader.addContent(new Paragraph(""), sliceLink);
	}

	private void loadProjectContent() {
		projectHeader.setContent(null);

		projectHeader.addContent(this.getDetailsLine("Id", currentMethod.getGenieMethod().getProjectId() + ""));
		projectHeader.addContent(this.getDetailsLine("Name", currentMethod.getGenieMethod().getProjectName()));
		projectHeader.addContent(this.getDetailsLine("Type", currentMethod.getGenieMethod().getProjectType()));
	}

	private void loadSourceContent() {
		
		//TODO mostrar só se for crawled?
		//TODO como formatar?
		sourceHeader.addContent(new Span(currentMethod.getGenieMethod().getSourceCode()));
	}

	//TODO mostrar só o metodo permitir execução
	private void loadExecContent() {
		compiledJarLink.setHref(GenieSearchAPIConfig.getJarPath() + "");
		compiledJarLink.setTarget("_blank");
		compiledJarLink.setText("Jar Download");
		methodHeader.addContent(new Paragraph(""), sliceLink);
	}

	public void enter(Method method) {
		if (method == null) {
			setVisible(false);
			return;
		}
		this.currentMethod = method;
		setVisible(true);
		this.loadContent();
	}
}
