/* Copyright (c) 2024, Mathieu Bordas
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

import java.util.ArrayList;
import java.util.List;

public class CenteredFlow extends UIComponent {

	public static final String JSON_TYPE = "centered_flow";

	public enum Width {
		NORMAL("tui-reading-normal"), WIDE("tui-reading-wide"), MAX("tui-reading-max");
		private final String m_htmlClass;

		Width(String htmlClass) {
			m_htmlClass = htmlClass;
		}

		public String getHTMLClass() {
			return m_htmlClass;
		}
	}

	private Width m_width = Width.NORMAL;
	private final List<UIComponent> m_content = new ArrayList<>();

	public CenteredFlow setWidth(@NotNull Width width) {
		m_width = width;
		return this;
	}

	public void appendAll(List<UIComponent> components) {
		m_content.addAll(components);
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTMLNode("div");
	}

	public HTMLNode toHTMLNode(String tagName) {
		final HTMLNode result = new HTMLNode(tagName);
		result.addClass(Grid.HTML_CLASS);
		switch(m_width) {
		case MAX -> result.setAttribute("style", "grid-template-rows: auto;grid-template-columns: 0% 100% 0%");
		case WIDE -> result.setAttribute("style", "grid-template-rows: auto;grid-template-columns: min-content 1fr min-content");
		case NORMAL -> result.setAttribute("style", "grid-template-rows: auto;grid-template-columns: 1fr min-content 1fr");
		}

		giveMarginReadingProperties(result.createChild("p"));
		final HTMLNode flowContent = giveCenterReadingProperties(result.createChild("div"));
		giveMarginReadingProperties(result.createChild("p"));

		for(UIComponent component : m_content) {
			flowContent.addChild(component.toHTMLNode());
		}

		return result;
	}

	private HTMLNode giveCenterReadingProperties(HTMLNode node) {
		switch(m_width) {
		case NORMAL -> node.addClass("tui-reading-normal-area");
		}
		return node;
	}

	private void giveMarginReadingProperties(HTMLNode node) {
		switch(m_width) {
		case WIDE -> node.addClass("tui-reading-wide-margin");
		}
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("width", m_width.name());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		return result;
	}
}
