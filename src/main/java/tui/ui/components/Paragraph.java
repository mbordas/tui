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
import org.jetbrains.annotations.Nullable;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.StyleSet;
import tui.ui.components.layout.Layouts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Paragraph extends UIRefreshableComponent {

	public static final String JSON_TYPE = "paragraph";

	public static final String HTML_CLASS_CONTAINER = "tui-container-paragraph";
	public static final String HTML_CLASS_BORDER_ON = "tui-border-on";
	public static final String HTML_CLASS_BORDER_OFF = "tui-border-off";
	public static final String ATTRIBUTE_CONTENT = "content";
	public static final String ATTRIBUTE_BORDER = "border";
	public static final String ATTRIBUTE_TEXT_ALIGN = "textAlign";

	public class Text extends UIComponent {

		public static final String JSON_TYPE = "text";
		public static final String JSON_ATTRIBUTE_CONTENT = "content";

		private final String m_text;

		public Text(String format, Object... args) {
			m_text = String.format(format, args);
		}

		@Override
		public HTMLNode toHTMLNode() {
			final HTMLNode result = new HTMLNode("span");
			result.setText(m_text);
			applyCustomStyle(result);
			return result;
		}

		@Override
		public JsonMap toJsonMap() {
			final JsonMap result = new JsonMap(JSON_TYPE);
			result.setAttribute(JSON_ATTRIBUTE_CONTENT, m_text);
			applyCustomStyle(result);
			return result;
		}
	}

	private boolean m_withBorder = false;
	private Layouts.TextAlign m_textAlign = Layouts.TextAlign.LEFT;
	private final List<UIComponent> m_content = new ArrayList<>();

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
		m_content.clear();
		return this;
	}

	/**
	 * @param styler This optional function may modify text's custom {@link StyleSet}.
	 */
	public Paragraph append(@Nullable Consumer<StyleSet> styler, @NotNull String format, Object... args) {
		final Text text = new Text(format, args);
		if(styler != null) {
			styler.accept(text.customStyle());
		}
		m_content.add(text);
		return this;
	}

	public Paragraph appendNormal(String format, Object... args) {
		return append(null, format, args);
	}

	public Paragraph appendBold(String format, Object... args) {
		return append((style) -> style.setFontWeight("bold"), format, args);
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("p", HTML_CLASS_CONTAINER);

		final HTMLNode paragraphElement = containedElement.element();
		paragraphElement.addClass(m_textAlign.getHTMLClass());
		paragraphElement.addClass(m_withBorder ? HTML_CLASS_BORDER_ON : HTML_CLASS_BORDER_OFF);
		for(UIComponent fragment : m_content) {
			paragraphElement.append(fragment.toHTMLNode());
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

		result.createArray(ATTRIBUTE_CONTENT, m_content, UIComponent::toJsonMap);

		return result;
	}

}
