/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components.layout;

import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class Panel extends UIComponent {

	public static final String HTML_CLASS = "tui-panel";

	public static final String JSON_TYPE = "panel";

	private final List<UIComponent> m_content = new ArrayList<>();

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("div", true)
				.setAttribute("class", HTML_CLASS);

		for(UIComponent component : getContent()) {
			result.addChild(component.toHTMLNode());
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		return result;
	}

}
