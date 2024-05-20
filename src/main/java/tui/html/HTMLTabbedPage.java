/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.html;

import tui.ui.components.Panel;
import tui.ui.components.TabbedPage;

import java.util.Map;

public class HTMLTabbedPage {

	public static final String CLASS_TABNAV = "tui-tabnav";
	public static final String CLASS_TABLINK = "tui-tablink";
	public static final String CLASS_TABLINK_ACTIVE = "tui-tablink-active";

	public static HTMLNode toHTML(TabbedPage page, String pathToCSS, String pathToScript, String onLoadFunctionCall) {
		final HTMLNode result = new HTMLNode("html");
		result.setRoot(true);

		final HTMLNode head = result.createChild("head");
		head.createChild("meta").setAttribute("charset", "utf-8");
		head.createChild("meta").setAttribute("name", "viewport")
				.setAttribute("content", "width=device-width, initial-scale=1");
		head.createChild("title").setText(page.getTitle());
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
		final Map<String, Panel> tabs = page.getContent();

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
}
