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

package tui.ui.components;

import tui.html.HTMLNode;
import tui.json.JsonArray;
import tui.json.JsonMap;

import java.util.ArrayList;

public class List extends UIComponent {

	public static final String JSON_TYPE = "list";
	public static final String JSON_ATTRIBUTE_IS_ORDERED = "isOrdered";
	public static final String JSON_ARRAY_ELEMENTS = "content";

	private boolean m_isOrdered = true;
	private final java.util.List<UIComponent> m_content = new ArrayList<>();

	public List(boolean isOrdered) {
		m_isOrdered = isOrdered;
	}

	public List append(UIComponent component) {
		m_content.add(component);
		return this;
	}

	public List appendText(String format, Object... args) {
		return append(new Paragraph.Text(String.format(format, args)));
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode(m_isOrdered ? "ol" : "ul");
		m_content.forEach((component) -> {
			final HTMLNode itemContainer = result.createChild("li");
			itemContainer.append(component.toHTMLNode());
		});

		applyCustomStyle(result);
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute(JSON_ATTRIBUTE_IS_ORDERED, Boolean.toString(m_isOrdered));
		final JsonArray contentArray = result.createArray(JSON_ARRAY_ELEMENTS);
		m_content.forEach((component) -> contentArray.add(component.toJsonMap()));
		applyCustomStyle(result);
		return result;
	}

}
