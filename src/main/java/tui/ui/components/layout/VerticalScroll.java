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

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class VerticalScroll extends UIComponent {

	public static final String HTML_CLASS = "tui-vertical-scroll";

	public static final String JSON_TYPE = "verticalScroll";

	private final int m_height_px;
	private final List<UIComponent> m_content = new ArrayList<>();

	public VerticalScroll(int height_px, UIComponent... components) {
		m_height_px = height_px;
		for(UIComponent component : components) {
			append(component);
		}
	}

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	public List<UIComponent> getContent() {
		return m_content;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("div")
				.setAttribute("id", HTMLConstants.toId(getTUID()))
				.setAttribute("class", HTML_CLASS)
				.setAttribute("style", String.format("height: %dpx;", m_height_px));

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
