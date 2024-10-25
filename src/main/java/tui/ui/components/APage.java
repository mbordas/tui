/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

import tui.html.HTMLNode;

public abstract class APage extends UIComponent {

	public record Resource(boolean isExternal, String contentOrLink) {
	}

	protected final String m_title;
	private String m_source;

	protected APage(String title) {
		m_title = title;
	}

	protected APage(String title, String source) {
		m_title = title;
		m_source = source;
	}

	public abstract HTMLNode toHTMLNode(Resource cssResource, Resource scriptResource);

	public String getTitle() {
		return m_title;
	}

	public void setSource(String source) {
		m_source = source;
	}

	public String getSource() {
		return m_source;
	}
}
