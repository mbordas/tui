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

package tui.ui.style;

import tui.ui.components.layout.Layouts;

import java.awt.*;
import java.util.Locale;
import java.util.function.BiConsumer;

public class TextStyleSet extends StyleSet {

	private String m_color = null;
	private String m_fontSize = null;
	private String m_fontFamily = null;
	private String m_fontWeight = null;
	private String m_fontStyle = null;
	private Double m_lineHeight_em = null;
	private String m_textTransform = null;
	private String m_textDecoration = null;
	private Layouts.Align m_textAlign = null;

	public TextStyleSet setTextColor(Color color) {
		m_color = Style.toCSSHex(color);
		return this;
	}

	public TextStyleSet setTextColor(String cssValue) {
		m_color = cssValue;
		return this;
	}

	public TextStyleSet setTextSize_px(int size_px) {
		m_fontSize = String.format(Locale.US, "%dpx", size_px);
		return this;
	}

	public TextStyleSet setTextSize_em(float size_em) {
		m_fontSize = String.format(Locale.US, "%fem", size_em);
		return this;
	}

	public TextStyleSet setLineHeight(double height_em) {
		m_lineHeight_em = height_em;
		return this;
	}

	public TextStyleSet setTextUpperCase() {
		m_textTransform = "uppercase";
		return this;
	}

	public TextStyleSet setTextItalic() {
		m_fontStyle = "italic";
		return this;
	}

	public TextStyleSet setNoTextDecoration() {
		m_textDecoration = "none";
		return this;
	}

	public TextStyleSet setTextUnderlined() {
		m_textDecoration = "underline";
		return this;
	}

	public TextStyleSet setTextAlign(Layouts.Align textAlign) {
		m_textAlign = textAlign;
		return this;
	}

	public TextStyleSet setFontFamily(String fontFamily) {
		m_fontFamily = fontFamily;
		return this;
	}

	public TextStyleSet setFontWeight(String fontWeight) {
		m_fontWeight = fontWeight;
		return this;
	}

	@Override
	<T> void apply(T node, BiConsumer<T, Property> setter) {
		setStylePropertyIfDefined(node, "color", m_color, setter);
		setStylePropertyIfDefined(node, "font-size", m_fontSize, setter);
		setStylePropertyIfDefined(node, "font-family", m_fontFamily, setter);
		setStylePropertyIfDefined(node, "font-weight", m_fontWeight, setter);
		setStylePropertyIfDefined(node, "font-style", m_fontStyle, setter);
		setStylePropertyIfDefined(node, "line-height", m_lineHeight_em == null ? null : String.valueOf(m_lineHeight_em), setter);
		setStylePropertyIfDefined(node, "text-transform", m_textTransform, setter);
		setStylePropertyIfDefined(node, "text-decoration", m_textDecoration, setter);
		setStylePropertyIfDefined(node, "text-align", m_textAlign == null ? null : m_textAlign.getCSSValue(), setter);
	}

}
