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

package tui.ui.components;

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.html.HTMLText;
import tui.json.JsonArray;
import tui.json.JsonMap;

import java.util.ArrayList;
import java.util.List;

public class Paragraph extends UIRefreshableComponent {

	public static final String JSON_TYPE = "paragraph";

	public static final String ATTRIBUTE_CONTENT = "content";
	
	public enum Style {
		NORMAL(null, "text"), STRONG("strong", "strong");
		String htmlNodeName;
		String jsonType;

		Style(String htmlNodeName, String jsonType) {
			this.htmlNodeName = htmlNodeName;
			this.jsonType = jsonType;
		}

		public static Style parseJsonType(String type) {
			return switch(type) {
				case "text" -> NORMAL;
				case "strong" -> STRONG;
				default -> throw new IllegalStateException("Unexpected value: " + type);
			};
		}
	}

	public record Fragment(Style style, String text) {
	}

	private final List<Fragment> m_fragments = new ArrayList<>();

	public Paragraph() {
	}

	public Paragraph(String text) {
		appendNormal(text);
	}

	public Paragraph appendNormal(String text) {
		return append(Style.NORMAL, text);
	}

	public Paragraph appendStrong(String text) {
		return append(Style.STRONG, text);
	}

	private Paragraph append(Style style, String text) {
		m_fragments.add(new Fragment(style, text));
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("p");
		if(hasSource()) {
			result.setAttribute("id", HTMLConstants.toId(getTUID()));
			result.setAttribute(ATTRIBUTE_SOURCE, getSource());
		}

		for(Fragment fragment : m_fragments) {
			if(Style.NORMAL == fragment.style()) {
				result.addChild(new HTMLText(fragment.text()));
			} else {
				result.createChild(fragment.style().htmlNodeName)
						.setText(fragment.text());
			}
		}

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		if(hasSource()) {
			result.setAttribute(ATTRIBUTE_SOURCE, getSource());
		}

		final JsonArray content = result.createArray(ATTRIBUTE_CONTENT);

		for(Fragment fragment : m_fragments) {
			content.add(new JsonArray().add(fragment.style().jsonType).add(fragment.text()));
		}

		return result;
	}

}
