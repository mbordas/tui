/* Copyright (c) 2025, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

	public enum Align {
		LEFT, CENTER, RIGHT, STRETCH, VERTICAL;

		public String getHTMLClass() {
			return "tui-panel-" + name().toLowerCase();
		}
	}

	private final List<UIComponent> m_content = new ArrayList<>();
	private Align m_align = Align.LEFT;
	protected Layouts.Spacing m_spacing = Layouts.Spacing.NORMAL;

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public Panel setAlign(@NotNull Align align) {
		m_align = align;
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
		containedElement.element().addClass(m_align.getHTMLClass());

		final HTMLNode node = containedElement.element();
		for(UIComponent component : getContent()) {
			final HTMLNode componentNode = component.toHTMLNode();
			componentNode.addClass(m_spacing.getHTMLClass().replaceAll("spacing", "horizontal-spacing"));
			node.append(componentNode);
		}

		return containedElement.getHigherNode();
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		if(hasSource()) {
			result.setAttribute(ATTRIBUTE_SOURCE, getSource());
		}
		result.setAttribute("align", m_align.name());
		result.setAttribute("spacing", m_spacing.name());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		applyCustomStyle(result);
		return result;
	}

}
