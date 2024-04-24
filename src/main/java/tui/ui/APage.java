/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui;

import tui.html.HTMLNode;

public abstract class APage extends TUIComponent {

	public abstract HTMLNode toHTMLNode(String pathToCSS, String pathToScript, String onLoadFunctionCall);

	protected final String m_title;

	protected APage(String title) {
		m_title = title;
	}

	public String getTitle() {
		return m_title;
	}
}
