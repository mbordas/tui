/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.html;

import tui.ui.Panel;
import tui.ui.TUIComponent;

public class HTMLPanel {

	public static final String CLASS = "tui-panel";

	public static HTMLNode toHTML(Panel panel) {
		final HTMLNode result = new HTMLNode("div")
				.setAttribute("id", HTMLConstants.toId(panel.getTUID()))
				.setAttribute("class", CLASS);
		for(TUIComponent component : panel.getContent()) {
			result.addChild(component.toHTMLNode());
		}
		return result;
	}
}
