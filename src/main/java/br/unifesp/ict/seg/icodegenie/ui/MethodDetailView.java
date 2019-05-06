package br.unifesp.ict.seg.icodegenie.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.appreciated.prism.element.Language;
import com.github.appreciated.prism.element.PrismHighlighter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.GenieMethodRepository;
import br.unifesp.ict.seg.icodegenie.model.Method;

//FIXME 0 MethodDetailView: implementar

public class MethodDetailView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private MainView mainView;

	private GenieMethodRepository repository = new GenieMethodRepository();

	private Method currentMethod;

	private Details methodHeader = new Details(new Span("Method"), null);
	private Details downloadHeader = new Details(new Span("Downloads"), null);
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

		add(methodHeader, downloadHeader, sourceHeader, execHeader);
	}

	private void configureComponents() {

		methodHeader.getSummary().getElement().getStyle().set("color", okColor);
		downloadHeader.getSummary().getElement().getStyle().set("color", okColor);
		sourceHeader.getSummary().getElement().getStyle().set("color", okColor);
		execHeader.getSummary().getElement().getStyle().set("color", okColor);

		methodHeader.setOpened(true);
		downloadHeader.setOpened(false);
		sourceHeader.setOpened(true);
		execHeader.setOpened(true);
}

	private void hookLogicToComponents() {
		downloadHeader.addOpenedChangeListener(e -> this.loadDownloadContent());
	}

	private void loadContent() {

		GenieMethod genieMethod = repository.findByEntityId(currentMethod.getEntityId());
		this.currentMethod.setGenieMethod(genieMethod);

		downloadHeader.setVisible(genieMethod.isCrawled());
		sourceHeader.setVisible(genieMethod.isCrawled());
		execHeader.setVisible(genieMethod.isAllowsExecution());
		
		this.loadMethodContent();
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
		methodHeader.addContent(this.getDetailsLine("Project id", currentMethod.getGenieMethod().getProjectId() + ""));
		methodHeader.addContent(this.getDetailsLine("Project name", currentMethod.getGenieMethod().getProjectName()));
		methodHeader.addContent(this.getDetailsLine("Project yype", currentMethod.getGenieMethod().getProjectType()));
}

	public StreamResource getStreamResource(Path path) {
		return new StreamResource(path.getFileName() + "", () -> {
			try {
				return new ByteArrayInputStream(FileUtils.readFileToByteArray(path.toFile()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	private void loadDownloadContent() {

		if (!downloadHeader.isOpened())
			return;

		downloadHeader.setContent(null);

		GenieMethod genieMethod = currentMethod.getGenieMethod();

		// Slice
		sliceLink.setHref(this.getStreamResource(genieMethod.getSlicedFilePath()));
		sliceLink.setText(genieMethod.getSlicedFilePath().getFileName() + "");
		try {
			if (genieMethod.slice())
				downloadHeader.addContent(new Span("Slice: "), sliceLink);
		} catch (Exception e) {
		}

		// Jar file
		compiledJarLink.setHref(this.getStreamResource(genieMethod.getCompiledJarPath()));
		compiledJarLink.setText(genieMethod.getCompiledJarPath().getFileName() + "");
		try {
			if (genieMethod.generateJar()) {
				downloadHeader.addContent(new Paragraph(""));
				downloadHeader.addContent(new Span("Compiled Jar: "), compiledJarLink);
			}
		} catch (Exception e) {
		}
	}

	private void loadSourceContent() {
		sourceHeader.setContent(null);
		String sourceCode = currentMethod.getGenieMethod().getSourceCode();
		PrismHighlighter sourceCodeComponent = new PrismHighlighter(sourceCode, Language.java);
		sourceHeader.addContent(sourceCodeComponent);

	}

	private void loadExecContent() {

		if(!currentMethod.getGenieMethod().isAllowsExecution())
			return;
		
		// FIXME Implementar daqui pra baixo...
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
