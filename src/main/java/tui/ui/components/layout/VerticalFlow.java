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

import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;

public class VerticalFlow extends AFlow {

	public static final String HTML_CLASS = "tui-vertical-flow";

	public static final String JSON_TYPE = "vertical_flow";

	public VerticalFlow appendUnitedBlock(UIComponent... components) {
		final VerticalFlow unitedBlock = createUnitedBlock(components);
		append(unitedBlock);
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTMLNode("div").addClass(HTML_CLASS);
	}

	public HTMLNode toHTMLNode(String tagName) {
		final HTMLNode result = new HTMLNode(tagName);
		result.addClass(Grid.HTML_CLASS);

		result.setStyleProperty("place-items", "center");
		result.setStyleProperty("grid-template-rows", "auto");
		switch(m_width) {
		case MAX -> result.setStyleProperty("grid-template-columns", "0% 100% 0%");
		case WIDE -> result.setStyleProperty("grid-template-columns", "min-content 1fr min-content");
		case NORMAL -> result.setStyleProperty("grid-template-columns", "1fr min-content 1fr");
		}

		giveMarginReadingProperties(result.createChild("p"));

		final HTMLNode flowContent = giveCenterReadingProperties(result.createChild("div"));
		flowContent.setStyleProperty("display", "grid");
		flowContent.setStyleProperty("grid-template-rows", "auto");

		for(UIComponent component : m_content) {
			final HTMLNode htmlNode = component.toHTMLNode();
			htmlNode.setStyleProperty("margin", "auto");
			final HTMLNode div = new HTMLNode("div");
			div.setStyleProperty("text-align", "center");
			div.addClass(m_spacing.getHTMLClass().replaceAll("spacing", "vertical-spacing"));
			div.addChild(htmlNode);
			flowContent.addChild(div);
		}

		giveMarginReadingProperties(result.createChild("p"));

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("width", m_width.name());
		result.setAttribute("spacing", m_spacing.name());
		result.createArray("content", m_content, UIComponent::toJsonMap);
		return result;
	}

	public static VerticalFlow createUnitedBlock(UIComponent... components) {
		final VerticalFlow unitedBlock = new VerticalFlow();
		unitedBlock.setWidth(Layouts.Width.MAX);
		unitedBlock.setSpacing(Layouts.Spacing.FIT);
		for(UIComponent component : components) {
			unitedBlock.append(component);
		}
		return unitedBlock;
	}
}