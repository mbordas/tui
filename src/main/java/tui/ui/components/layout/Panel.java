/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components.layout;

import org.jetbrains.annotations.NotNull;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;
import tui.ui.components.UIRefreshableComponent;

import java.util.ArrayList;
import java.util.List;

public class Panel extends UIRefreshableComponent {

	public static final String HTML_CLASS = "tui-panel";
	public static final String HTML_CLASS_CONTAINER = "tui-container-panel";

	public static final String JSON_TYPE = "panel";

	private final List<UIComponent> m_content = new ArrayList<>();
	private Layouts.TextAlign m_textAlign = Layouts.TextAlign.LEFT;
	protected Layouts.Spacing m_spacing = Layouts.Spacing.NORMAL;

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public Panel setAlign(@NotNull Layouts.TextAlign textAlign) {
		m_textAlign = textAlign;
		return this;
	}

	public Panel setSpacing(Layouts.Spacing spacing) {
		m_spacing = spacing;
		return this;
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("div", HTML_CLASS_CONTAINER);
		containedElement.element().addClass(HTML_CLASS);

		final HTMLNode node = containedElement.element();
		for(UIComponent component : getContent()) {
			final HTMLNode componentNode = component.toHTMLNode();
			componentNode.addClass(m_spacing.getHTMLClass().replaceAll("spacing", "horizontal-spacing"));
			node.append(componentNode);
		}

		applyCustomStyle(containedElement.element());

		return containedElement.getHigherNode();
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("textAlign", m_textAlign.name());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		applyCustomStyle(result);
		return result;
	}

}
