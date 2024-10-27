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

import java.awt.*;

public class StyleSet {

	private String m_color = null;
	private String m_fontSize = null;
	private String m_textTransform = null;
	private String m_backgroundColor = null;
	private String m_borderStyle = null;
	private String m_borderColor = null;
	private String m_borderWidth = null;

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
		setStylePropertyIfDefined(node, "color", m_color);
		setStylePropertyIfDefined(node, "font-size", m_fontSize);
		setStylePropertyIfDefined(node, "text-transform", m_textTransform);
		setStylePropertyIfDefined(node, "background-color", m_backgroundColor);
		setStylePropertyIfDefined(node, "border-style", m_borderStyle);
		setStylePropertyIfDefined(node, "border-color", m_borderColor);
		setStylePropertyIfDefined(node, "border-width", m_borderWidth);
		setStylePropertyIfDefined(node, "width", m_width);
		setStylePropertyIfDefined(node, "height", m_height);

		if(m_margin != null) {
			setStylePropertyIfDefined(node, "margin", String.format("%dpx %dpx %dpx %dpx",
					m_margin.top_px(), m_margin.right_px(), m_margin.bottom_px(), m_margin.left_px()));
		}
		if(m_padding != null) {
			setStylePropertyIfDefined(node, "padding", String.format("%dpx %dpx %dpx %dpx",
					m_padding.top_px(), m_padding.right_px(), m_padding.bottom_px(), m_padding.left_px()));
		}
	}

	private void setStylePropertyIfDefined(@NotNull HTMLNode node, @NotNull String name, @Nullable String value) {
		if(value != null) {
			node.setStyleProperty(name, value);
		}
	}

}
