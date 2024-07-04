/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components;

import tui.html.HTMLNode;
import tui.html.HTMLPanel;
import tui.json.JsonMap;

import java.util.ArrayList;
import java.util.List;

public class Panel extends UIComponent {

	public static final String JSON_TYPE = "panel";

	private final List<UIComponent> m_content = new ArrayList<>();

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public Section createSection(String title) {
		final Section result = new Section(title);
		m_content.add(result);
		return result;
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return HTMLPanel.toHTML(this);
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		return result;
	}

}
