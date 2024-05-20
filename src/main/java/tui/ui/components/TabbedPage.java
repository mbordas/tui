/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

import tui.html.HTMLNode;
import tui.html.HTMLTabbedPage;
import tui.json.JsonArray;
import tui.json.JsonMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabbedPage extends APage {

	public static final String JSON_TYPE = "tabbed_page";
	public static final String TABBED_PANEL_JSON_TYPE = "tabbed_panel";

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

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("title", getTitle());
		final JsonArray tabNodes = result.createArray("content");
		for(Map.Entry<String, Panel> tabEntry : m_content.entrySet()) {
			final JsonMap tabNode = new JsonMap(TABBED_PANEL_JSON_TYPE);
			tabNode.setAttribute("title", tabEntry.getKey());
			tabNode.setChild("content", tabEntry.getValue().toJsonMap());
			tabNodes.add(tabNode);
		}
		return result;
	}
}
