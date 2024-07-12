/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonArray;
import tui.json.JsonMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabbedPage extends APage {

	public static final String JSON_TYPE = "tabbed_page";
	public static final String TABBED_PANEL_JSON_TYPE = "tabbed_panel";

	public static final String CLASS_TABNAV = "tui-tabnav";
	public static final String CLASS_TABLINK = "tui-tablink";
	public static final String CLASS_TABLINK_ACTIVE = "tui-tablink-active";

	private final Map<String /* title */, Panel> m_content = new LinkedHashMap<>();

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
		final HTMLNode result = new HTMLNode("html");
		result.setRoot(true);

		final HTMLNode head = result.createChild("head");
		head.createChild("meta").setAttribute("charset", "utf-8");
		head.createChild("meta").setAttribute("name", "viewport")
				.setAttribute("content", "width=device-width, initial-scale=1");
		head.createChild("title").setText(getTitle());
		if(pathToCSS != null) {
			head.createChild("link")
					.setAttribute("rel", "stylesheet")
					.setAttribute("href", pathToCSS);
		}
		if(pathToScript != null) {
			head.createChild("script")
					.setAttribute("type", HTMLConstants.JAVASCRIPT_CONTENT_TYPE)
					.setAttribute("src", pathToScript)
					.setAttribute("defer", null); // the script is meant to be executed after the document has been parsed
		}

		final HTMLNode body = result.createChild("body");
		if(onLoadFunctionCall != null) {
			body.setAttribute("onload", onLoadFunctionCall);
		}
		final HTMLNode main = body.createChild("main");
		final Map<String, Panel> tabs = getContent();

		final HTMLNode tabsNav = main.createChild("div")
				.setAttribute("class", CLASS_TABNAV);

		int index = 1;
		for(Map.Entry<String, Panel> tabEntry : tabs.entrySet()) {
			final String label = tabEntry.getKey();
			final Panel panel = tabEntry.getValue();

			final String onClickCode = String.format("selectTab('%s', this)", HTMLConstants.toId(panel.getTUID()));
			tabsNav.createChild("button")
					.setAttribute("class", index == 1 ? CLASS_TABLINK + " " + CLASS_TABLINK_ACTIVE : CLASS_TABLINK)
					.setAttribute("onclick", onClickCode)
					.setText(label);

			final HTMLNode panelNode = panel.toHTMLNode();
			panelNode.setAttribute("style", index == 1 ? "display:block" : "display:none");
			main.addChild(panelNode);

			index++;
		}

		return result;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTMLNode(null, null, null);
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("title", getTitle());
		final JsonArray tabNodes = result.createArray("content");
		for(Map.Entry<String, Panel> tabEntry : m_content.entrySet()) {
			final Panel panel = tabEntry.getValue();
			final JsonMap tabNode = new JsonMap(TABBED_PANEL_JSON_TYPE, panel.getTUID());
			tabNode.setAttribute("title", tabEntry.getKey());
			tabNode.createArray("content", tabEntry.getValue().getContent(), UIComponent::toJsonMap);
			tabNodes.add(tabNode);
		}
		return result;
	}
}
