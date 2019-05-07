package br.unifesp.ict.seg.icodegenie.ui;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.GenieMethodRepository;
import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

public class StatusView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private MainView mainView;

	private Button back = new Button(VaadinIcon.ARROW_BACKWARD.create());
	private Details propHeader = new Details(new Span("Properties"), null);
	private Details repoHeader = new Details("  Repository", null);
	private Details solrHeader = new Details("  Solr", null);
	private Details dbHeader = new Details("  Database", null);
	private Details problemsHeader = new Details(new Span("Problems"), null);
	private Details appHeader = new Details("  ICodeGenie", null);

	private Anchor solrLink = new Anchor();
	private Anchor appLink = new Anchor();
	
	private String okColor = "hsl(214, 90%, 52%)";
	private String errorColor = "hsl(3, 100%, 61%)";
	private String configFileName = "geniesearchapi.properties";

	public StatusView(MainView mainView) {

		this.mainView = mainView;
		setVisible(false);

		buildLayout();
		configureComponents();
		hookLogicToComponents();
	}

	private void buildLayout() {

		add(back, problemsHeader, propHeader, repoHeader, solrHeader, dbHeader, appHeader);
	}

	private void configureComponents() {

		back.setWidthFull();
		
		problemsHeader.getSummary().getElement().getStyle().set("color", errorColor);
		appHeader.getSummary().getElement().getStyle().set("color", okColor);

		propHeader.setOpened(true);
		repoHeader.setOpened(true);
		solrHeader.setOpened(true);
		dbHeader.setOpened(true);
		problemsHeader.setOpened(true);
		appHeader.setOpened(true);

		if (GenieSearchAPIConfig.isValidProperties()) {
			this.loadPropertiesContent();
			this.loadRepoContent();
			this.loadSolrContent();
			this.loadDBContent();
		}
	}

	private void hookLogicToComponents() {
		back.addClickListener(e -> { this.setVisible(false);});
	}

	private void updateContent() {

		this.loadAppContent();
		
		propHeader.getSummary().getElement().getStyle().set("color", okColor);
		repoHeader.getSummary().getElement().getStyle().set("color", okColor);
		solrHeader.getSummary().getElement().getStyle().set("color", okColor);
		dbHeader.getSummary().getElement().getStyle().set("color", okColor);

		
		boolean isValidProperties = GenieSearchAPIConfig.isValidProperties();
		boolean checkAll = GenieSearchAPIConfig.checkAll();

		propHeader.setVisible(isValidProperties);
		repoHeader.setVisible(isValidProperties);
		solrHeader.setVisible(isValidProperties);
		dbHeader.setVisible(isValidProperties);
		problemsHeader.setVisible(!checkAll);

		if (!checkAll)
			this.loadProblemsContent();
	}

	private void loadPropertiesContent() {

		Properties properties = GenieSearchAPIConfig.getProperties();

		if (properties == null)
			return;

		for (Object key : properties.keySet()) {
			String value = GenieSearchAPIConfig.P_DATABASE_PASSWORD.equals(key) ? "***" : properties.get(key) + "";
			String line = key + " = " + value;
			propHeader.addContent(new Paragraph(line));
		}
	}

	private void loadRepoContent() {
		String loadFrom = "";
		if (GenieSearchAPIConfig.hasConfigFileName()) {
			loadFrom = "Arquivo de configuração: " + ClassLoader.getSystemResource(configFileName).getPath();
			loadFrom = StringUtils.replaceOnce(loadFrom, "/", "");
		} else
			loadFrom = "Configurações passadas por parâmetro via String args[]";

		repoHeader.addContent(new Paragraph("Name: " + GenieSearchAPIConfig.getRepoName()));
		repoHeader.addContent(new Paragraph("Path: " + GenieSearchAPIConfig.getRepoPath()));
		repoHeader.addContent(new Paragraph(loadFrom));
	}

	private void loadSolrContent() {
		solrHeader.addContent(new Paragraph("Config path: " + GenieSearchAPIConfig.getSolrConfigPath()));
		solrHeader.addContent(new Paragraph("Index path: " + GenieSearchAPIConfig.getSolrIndexPath()));
		solrHeader.addContent(new Paragraph("Documentos: " + GenieSearchAPIConfig.getSolrNumDocs()));
		solrLink.setHref(GenieSearchAPIConfig.getSolrURL());
		solrLink.setTarget("_blank");
		solrLink.setText(GenieSearchAPIConfig.getSolrURL());
		solrHeader.addContent(solrLink);
	}

	private void loadDBContent() {
		dbHeader.addContent(new Paragraph("Methods: " + new GenieMethodRepository().countAllInterfaceMetrics()));
	}

	private void loadProblemsContent() {
		problemsHeader.setContent(null);

		if(!GenieSearchAPIConfig.isValidProperties()) {
			String expectedPath = ClassLoader.getSystemResource("").getPath() + this.configFileName;
			expectedPath = StringUtils.replaceOnce(expectedPath, "/", "");
			problemsHeader.addContent(new Paragraph("🔴 Erro ao carregar as propriedades de configuração."));
			problemsHeader.addContent(new Paragraph("Use o arquivo de configuração '" + this.configFileName + "'"));
			problemsHeader.addContent(new Paragraph("Caminho esperado: " + expectedPath ));
		}
		
		if(!GenieSearchAPIConfig.checkRepoFolderExistence()) {
			repoHeader.getSummary().getElement().getStyle().set("color", errorColor);
			problemsHeader.addContent(new Paragraph("🔴 Repository folder not found: " + GenieSearchAPIConfig.getRepoPath()));
		}
		
		if(!GenieSearchAPIConfig.checkCrawledProjectsFolderName()) {
			propHeader.getSummary().getElement().getStyle().set("color", errorColor);
			problemsHeader.addContent(new Paragraph("🔴 Pasta diferente de 'crawled-projects'"));
			problemsHeader.addContent(new Paragraph("Valor definido é inválido: " + GenieSearchAPIConfig.getCrawledProjectsPath()));
		}
		
		if(!GenieSearchAPIConfig.checkOnlineWebServerURL()) {
			solrHeader.getSummary().getElement().getStyle().set("color", errorColor);
			problemsHeader.addContent(new Paragraph("🔴 Web Server OFFLINE: " + GenieSearchAPIConfig.getWebServerURL()));
		}
		
		if(!GenieSearchAPIConfig.checkOnlineSolrURL()) {
			solrHeader.getSummary().getElement().getStyle().set("color", errorColor);
			problemsHeader.addContent(new Paragraph("🔴 Solr OFFLINE: " + GenieSearchAPIConfig.getSolrURL()));
		}
		
		if(!GenieSearchAPIConfig.checkSolrReaderDir()) {
			solrHeader.getSummary().getElement().getStyle().set("color", errorColor);
			problemsHeader.addContent(new Paragraph("🔴 Solr Index Path esperado   : " + GenieSearchAPIConfig.getSolrIndexPath()));
			problemsHeader.addContent(new Paragraph("Solr Index Path configurado: " + GenieSearchAPIConfig.getSolrReaderDirPath()));
		}

		if(!GenieSearchAPIConfig.checkDBConnection()) {
			dbHeader.getSummary().getElement().getStyle().set("color", errorColor);
			problemsHeader.addContent(new Paragraph("🔴 Failed to connect to database."));
		}
	}

	private void loadAppContent() {
		appLink.setHref("https://github.com/unifesp-seg/i-code-genie");
		appLink.setTarget("_blank");
		appLink.setText("GitHub");

		appHeader.setContent(null);
		appHeader.addContent(new Paragraph("Version: " + mainView.getBuildVersion()));
		appHeader.addContent(new Paragraph("Date: " + mainView.getBuildTimestamp()));
		appHeader.addContent(appLink);
	}

	public void enter() {
		setVisible(!this.isVisible());
		this.updateContent();
	}
}
