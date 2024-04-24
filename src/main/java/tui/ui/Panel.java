/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui;

import tui.html.HTMLNode;
import tui.html.HTMLPanel;

import java.util.ArrayList;
import java.util.List;

public class Panel extends TUIComponent {

	private final List<TUIComponent> m_content = new ArrayList<>();

	public void append(TUIComponent component) {
		m_content.add(component);
	}

	public Section createSection(String title) {
		final Section result = new Section(title);
		m_content.add(result);
		return result;
	}

	public List<TUIComponent> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return HTMLPanel.toHTML(this);
	}

}
