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

public class HorizontalFlow extends AFlow {

	public static final String HTML_CLASS = "tui-horizontal-flow";

	public static final String JSON_TYPE = "horizontal_flow";

	private Layouts.TextAlign m_componentAlign = Layouts.TextAlign.CENTER;

	public HorizontalFlow setAlign(Layouts.TextAlign align) {
		m_componentAlign = align;
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return toHTMLNode("div").addClass(HTML_CLASS);
	}

	public HTMLNode toHTMLNode(String tagName) {
		final HTMLNode result = new HTMLNode(tagName);
		result.addClass(m_componentAlign.getHTMLClass());

		giveMarginReadingProperties(result.createChild("p"));

		final HTMLNode flowContent = giveCenterReadingProperties(result.createChild("div"));
		for(UIComponent component : m_content) {
			final HTMLNode div = new HTMLNode("div");
			div.setStyleProperty("display", "inline");
			div.addClass(m_spacing.getHTMLClass().replaceAll("spacing", "horizontal-spacing"));
			div.append(component.toHTMLNode());
			flowContent.append(div);
		}

		giveMarginReadingProperties(result.createChild("p"));

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		return null;
	}
}
