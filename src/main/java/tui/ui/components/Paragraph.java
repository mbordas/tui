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

import org.jetbrains.annotations.NotNull;
import tui.html.HTMLNode;
import tui.html.HTMLText;
import tui.json.JsonArray;
import tui.json.JsonMap;
import tui.ui.components.layout.Layouts;

import java.util.ArrayList;
import java.util.List;

public class Paragraph extends UIRefreshableComponent {

	public static final String JSON_TYPE = "paragraph";

	public static final String HTML_CLASS_CONTAINER = "tui-container-paragraph";
	public static final String HTML_CLASS_BORDER_ON = "tui-border-on";
	public static final String HTML_CLASS_BORDER_OFF = "tui-border-off";
	public static final String ATTRIBUTE_CONTENT = "content";
	public static final String ATTRIBUTE_BORDER = "border";
	public static final String ATTRIBUTE_TEXT_ALIGN = "textAlign";

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

	private boolean m_withBorder = false;
	private Layouts.TextAlign m_textAlign = Layouts.TextAlign.LEFT;
	private final List<Fragment> m_fragments = new ArrayList<>();

	public Paragraph() {
	}

	public Paragraph(Layouts.TextAlign textAlign) {
		this();
		setAlign(textAlign);
	}

	public Paragraph(String format, Object... args) {
		appendNormal(String.format(format, args));
	}

	public Paragraph setAlign(@NotNull Layouts.TextAlign textAlign) {
		m_textAlign = textAlign;
		return this;
	}

	public Paragraph withBorder(boolean enabled) {
		m_withBorder = enabled;
		return this;
	}

	public Paragraph clear() {
		m_fragments.clear();
		return this;
	}

	public Paragraph appendNormal(String format, Object... args) {
		assert format != null;
		return append(Style.NORMAL, format, args);
	}

	public Paragraph appendStrong(String text) {
		assert text != null;
		return append(Style.STRONG, text);
	}

	private Paragraph append(Style style, @NotNull String format, Object... args) {
		if(!format.isEmpty()) {
			m_fragments.add(new Fragment(style, String.format(format, args)));
		}
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("p", HTML_CLASS_CONTAINER);

		final HTMLNode paragraphElement = containedElement.element();
		paragraphElement.addClass(m_textAlign.getCSSValue());
		paragraphElement.addClass(m_withBorder ? HTML_CLASS_BORDER_ON : HTML_CLASS_BORDER_OFF);
		for(Fragment fragment : m_fragments) {
			if(Style.NORMAL == fragment.style()) {
				paragraphElement.append(new HTMLText(fragment.text()));
			} else {
				paragraphElement.createChild(fragment.style().htmlNodeName)
						.setText(fragment.text());
			}
		}

		return containedElement.getHigherNode();
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		if(hasSource()) {
			result.setAttribute(ATTRIBUTE_SOURCE, getSource());
		}
		result.setAttribute(ATTRIBUTE_TEXT_ALIGN, m_textAlign.name());
		result.setAttribute(ATTRIBUTE_BORDER, m_withBorder ? "on" : "off");

		final JsonArray content = result.createArray(ATTRIBUTE_CONTENT);

		for(Fragment fragment : m_fragments) {
			content.add(new JsonArray().add(fragment.style().jsonType).add(fragment.text()));
		}

		return result;
	}

}
