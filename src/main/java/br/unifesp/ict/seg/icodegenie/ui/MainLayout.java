package br.unifesp.ict.seg.icodegenie.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

//TODO remover esta classe?

public class MainLayout extends Div implements RouterLayout, PageConfigurator {

	private static final long serialVersionUID = 1L;

	@Override
	public void configurePage(InitialPageSettings settings) {
		//settings.addInlineFromFile(InitialPageSettings.Position.PREPEND, "inline.js", InitialPageSettings.WrapMode.JAVASCRIPT);

		settings.addMetaTag("og:title", "The Rock");
		settings.addMetaTag("og:type", "video.movie");
		settings.addMetaTag("og:url", "http://www.imdb.com/title/tt0117500/");
		settings.addMetaTag("og:image", "http://ia.media-imdb.com/images/rock.jpg");

		settings.addLink("shortcut icon", "icons/favicon.ico");
		settings.addFavIcon("icon", "icons/icon-192.png", "192x192");
	}
}