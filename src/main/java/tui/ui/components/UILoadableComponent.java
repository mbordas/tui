/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

public abstract class UILoadableComponent extends UIComponent {

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
