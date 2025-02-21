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

package tui.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.layout.Layouts;

import java.awt.*;
import java.util.function.BiConsumer;

public class StyleSet {

	private String m_color = null;
	private String m_fontSize = null;
	private String m_fontFamily = null;
	private String m_fontWeight = null;
	private Double m_lineHeight_em = null;
	private String m_textTransform = null;
	private Layouts.TextAlign m_textAlign = null;
	private String m_backgroundColor = null;
	private String m_borderStyle = null;
	private String m_borderColor = null;
	private String m_borderWidth = null;
	private String m_borderRadius_px = null;

	private Style.Padding m_padding = null;
	private Style.Margin m_margin = null;
	private String m_width = null;
	private String m_height = null;

	public StyleSet() {
	}

	public StyleSet setNoBorder() {
		m_borderStyle = "none";
		return this;
	}

	public StyleSet setBorderColor(Color color) {
		m_borderColor = Style.toCSSHex(color);
		m_borderStyle = "solid";
		return this;
	}

	public StyleSet setBorderWidth_px(int top, int right, int bottom, int left) {
		m_borderWidth = String.format("%dpx %dpx %dpx %dpx", top, right, bottom, left);
		return this;
	}

	public StyleSet setBorderWidth_px(int width_px) {
		m_borderWidth = String.format("%dpx", width_px);
		return this;
	}

	public StyleSet setBorderRadius(Integer radius_px) {
		m_borderRadius_px = String.format("%dpx", radius_px);
		return this;
	}

	public StyleSet setBackgroundColor(String cssValue) {
		m_backgroundColor = cssValue;
		return this;
	}

	public StyleSet setBackgroundColor(Color color) {
		m_backgroundColor = Style.toCSSHex(color);
		setTextColor(Style.computeContrastColor(color));
		return this;
	}

	public StyleSet setNoBackground() {
		m_backgroundColor = "transparent";
		return this;
	}

	public StyleSet setTextColor(Color color) {
		m_color = Style.toCSSHex(color);
		return this;
	}

	public StyleSet setTextColor(String cssValue) {
		m_color = cssValue;
		return this;
	}

	public StyleSet setTextSize_px(int size_px) {
		m_fontSize = String.format("%dpx", size_px);
		return this;
	}

	public StyleSet setLineHeight(double height_em) {
		m_lineHeight_em = height_em;
		return this;
	}

	public StyleSet setTextUpperCase() {
		m_textTransform = "uppercase";
		return this;
	}

	public StyleSet setPadding(int top, int right, int bottom, int left) {
		m_padding = new Style.Padding(top, right, bottom, left);
		return this;
	}

	public StyleSet setMargin(int top, int right, int bottom, int left) {
		m_margin = new Style.Margin(top, right, bottom, left);
		return this;
	}

	public StyleSet setWidth_px(int width_px) {
		m_width = String.format("%dpx", width_px);
		return this;
	}

	public StyleSet setWidth_percent(int width_percent) {
		m_width = String.format("%d%%", width_percent);
		return this;
	}

	public StyleSet setHeight_px(int height_px) {
		m_height = String.format("%dpx", height_px);
		return this;
	}

	public String toCSS() {
		final HTMLNode node = new HTMLNode("fake");
		apply(node);
		return node.computeStyleAttribute();
	}

	public void apply(HTMLNode node) {
		apply(node, (htmlNode, property) -> htmlNode.setStyleProperty(property.name, property.value));
	}

	public void apply(JsonMap node) {
		apply(node, (map, property) -> map.setStyleProperty(property.name, property.value));
	}

	public void setTextAlign(Layouts.TextAlign textAlign) {
		m_textAlign = textAlign;
	}

	public void setFontFamily(String fontFamily) {
		m_fontFamily = fontFamily;
	}

	public void setFontWeight(String fontWeight) {
		m_fontWeight = fontWeight;
	}

	private record Property(String name, String value) {
	}

	public <T> void apply(T node, BiConsumer<T, Property> setter) {
		setStylePropertyIfDefined(node, "color", m_color, setter);
		setStylePropertyIfDefined(node, "font-size", m_fontSize, setter);
		setStylePropertyIfDefined(node, "font-family", m_fontFamily, setter);
		setStylePropertyIfDefined(node, "font-weight", m_fontWeight, setter);
		setStylePropertyIfDefined(node, "line-height", m_lineHeight_em == null ? null : String.valueOf(m_lineHeight_em), setter);
		setStylePropertyIfDefined(node, "text-transform", m_textTransform, setter);
		setStylePropertyIfDefined(node, "text-align", m_textAlign == null ? null : m_textAlign.getCSSValue(), setter);
		setStylePropertyIfDefined(node, "background-color", m_backgroundColor, setter);
		setStylePropertyIfDefined(node, "border-style", m_borderStyle, setter);
		setStylePropertyIfDefined(node, "border-color", m_borderColor, setter);
		setStylePropertyIfDefined(node, "border-width", m_borderWidth, setter);
		setStylePropertyIfDefined(node, "border-radius", m_borderRadius_px, setter);
		setStylePropertyIfDefined(node, "width", m_width, setter);
		setStylePropertyIfDefined(node, "height", m_height, setter);

		if(m_margin != null) {
			setStylePropertyIfDefined(node, "margin", String.format("%dpx %dpx %dpx %dpx",
					m_margin.top_px(), m_margin.right_px(), m_margin.bottom_px(), m_margin.left_px()), setter);
		}
		if(m_padding != null) {
			setStylePropertyIfDefined(node, "padding", String.format("%dpx %dpx %dpx %dpx",
					m_padding.top_px(), m_padding.right_px(), m_padding.bottom_px(), m_padding.left_px()), setter);
		}
	}

	private <T> void setStylePropertyIfDefined(@NotNull T node, @NotNull String name, @Nullable String value,
			BiConsumer<T, Property> setter) {
		if(value != null) {
			setter.accept(node, new Property(name, value));
		}
	}

}
