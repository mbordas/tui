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

package tui.ui.components.svg;

import tui.json.JsonMap;

import java.util.Locale;

public class SVGText extends SVGComponent {

	public enum Anchor {START, MIDDLE, END}

	private long m_x;
	private long m_y;
	private String m_text;
	private final Anchor m_anchor;
	private float m_fontSize_em = 1;

	public SVGText(long x, long y, String text, Anchor anchor) {
		m_x = x;
		m_y = y;
		m_text = text;
		m_anchor = anchor;
	}

	public SVGText withFontSize_em(float size_em) {
		m_fontSize_em = size_em;
		return this;
	}

	@Override
	public String computeStyleAttribute() {
		return String.format("text-anchor:%s;", m_anchor.name().toLowerCase());
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap("text");
		result.setAttribute("x", String.valueOf(m_x));
		result.setAttribute("y", String.valueOf(m_y));
		result.setAttribute(SVG.JSON_ATTRIBUTE_INNER_TEXT, m_text);
		result.setAttribute("font-family", "\"Times New Roman\", \"Times\", \"Nimbus Roman 9L\", serif");
		result.setAttribute("font-style", "normal");
		result.setAttribute("font-size", String.format(Locale.US, "%.1fem", m_fontSize_em));
		setStyleAttribute(result);
		return result;
	}
}
