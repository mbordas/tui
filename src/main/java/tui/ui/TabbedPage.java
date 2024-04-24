/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui;

import tui.html.HTMLNode;
import tui.html.HTMLTabbedPage;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabbedPage extends APage {

	private final Map<String, Panel> m_content = new LinkedHashMap<>();

	public TabbedPage(String title) {
		super(title);
	}

	public Panel createTab(String label) {
		final Panel result = new Panel();
		m_content.put(label, result);
		return result;
	}

	public Map<String, Panel> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode(String pathToCSS, String pathToScript, String onLoadFunctionCall) {
		return HTMLTabbedPage.toHTML(this, pathToCSS, pathToScript, onLoadFunctionCall);
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTMLNode(null, null, null);
	}
}
