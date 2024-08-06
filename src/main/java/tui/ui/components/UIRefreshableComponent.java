/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

public abstract class UIRefreshableComponent extends UIComponent {

	public static final String ATTRIBUTE_SOURCE = "tui-source";

	protected String m_source;

	public String getSource() {
		return m_source;
	}

	public void setSource(String source) {
		m_source = source;
	}

	public boolean hasSource() {
		return m_source != null;
	}
}
