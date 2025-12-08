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
import tui.ui.components.layout.Layouts;
import tui.ui.style.LayoutStyleSet;
import tui.ui.style.StyleSet;
import tui.ui.style.TextStyleSet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Paragraph extends UIRefreshableComponent {

	public static final String JSON_TYPE = "paragraph";

	public static final String HTML_CLASS_CONTAINER = "tui-container-paragraph";
	public static final String HTML_CLASS_BORDER_ON = "tui-border-on";
	public static final String HTML_CLASS_BORDER_OFF = "tui-border-off";
	public static final String ATTRIBUTE_CONTENT = "content";
	public static final String ATTRIBUTE_BORDER = "border";
	public static final String ATTRIBUTE_TEXT_ALIGN = "textAlign";

	public static class Text extends UIComponentWithText {

		public static final String JSON_TYPE = "text";
		public static final String JSON_ATTRIBUTE_CONTENT = "content";

		private final String m_text;

		public Text(String format, Object... args) {
			if(args.length == 0) {
				m_text = format;
			} else {
				m_text = String.format(format, args);
			}
		}

		@Override
		public HTMLNode toHTMLNode() {
			final HTMLNode result = new HTMLNode("span");
			result.setText(m_text.replaceAll("\\n", "<br/>"));
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

	private Layouts.Align m_textAlign = Layouts.Align.LEFT;
	private final List<UIComponent> m_content = new ArrayList<>();

	public Paragraph() {
	}

	public Paragraph(Layouts.Align align) {
		this();
		setAlign(align);
	}

	public Paragraph(String format, Object... args) {
		appendNormal(String.format(format, args));
	}

	public Paragraph setAlign(@NotNull Layouts.Align align) {
		m_textAlign = align;
		return this;
	}

	public Paragraph clear() {
		m_content.clear();
		return this;
	}

	public <C extends UIComponent> C append(C component) {
		m_content.add(component);
		return component;
	}

	/**
	 * @param styler This optional function may modify text's custom {@link StyleSet}.
	 */
	public Paragraph append(@Nullable BiConsumer<LayoutStyleSet, TextStyleSet> styler, @NotNull String format, Object... args) {
		final Text text = new Text(format, args);
		if(styler != null) {
			styler.accept(text.customStyle(), text.customTextStyle());
		}
		m_content.add(text);
		return this;
	}

	public Paragraph appendNormal(String format, Object... args) {
		return append(null, format, args);
	}

	public Paragraph appendBold(String format, Object... args) {
		return append((layoutStyle, textStyle) -> textStyle.setFontWeight("bold"), format, args);
	}

	public Paragraph appendItalic(String format, Object... args) {
		return append((layoutStyle, textStyle) -> textStyle.setTextItalic(), format, args);
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("p", HTML_CLASS_CONTAINER);

		final HTMLNode paragraphElement = containedElement.element();
		paragraphElement.addClass(m_textAlign.getHTMLClass());
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
		result.createArray(ATTRIBUTE_CONTENT, m_content, UIComponent::toJsonMap);
		appendParameters(result);
		applyCustomStyle(result);
		return result;
	}

}
