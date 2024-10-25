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

import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.Style;

import java.awt.*;
import java.util.Locale;

public abstract class SVGComponent {

	private Color m_strokeColor = Color.BLACK;
	private int m_strokeWidth = 1;
	private double m_strokeOpacity = 1.0;
	private StrokeDashArray m_strokeDashArray = null;
	private Color m_fillColor = Color.BLACK;
	private double m_fillOpacity = 1.0;

	record StrokeDashArray(int length, int space) {
	}

	public abstract JsonMap toJsonMap();

	public SVGComponent withStrokeColor(Color color) {
		m_strokeColor = color;
		return this;
	}

	public SVGComponent withStrokeOpacity(double value) {
		m_strokeOpacity = value;
		return this;
	}

	public SVGComponent withStrokeWidth(int width) {
		m_strokeWidth = width;
		return this;
	}

	public SVGComponent withStrokeDashArray(int length, int space) {
		m_strokeDashArray = new StrokeDashArray(length, space);
		return this;
	}

	public SVGComponent withFillColor(Color color) {
		m_fillColor = color;
		return this;
	}

	public SVGComponent withFillOpacity(double value) {
		m_fillOpacity = value;
		return this;
	}

	public String computeStyleAttribute() {
		String result = String.format(Locale.US, "stroke:%s;stroke-width:%d;stroke-opacity:%.2f;fill:%s;fill-opacity:%.2f;",
				m_strokeColor == null ? "none" : Style.toCSSHex(m_strokeColor),
				m_strokeWidth, m_strokeOpacity,
				m_fillColor == null ? "none" : Style.toCSSHex(m_fillColor),
				m_fillOpacity);

		if(m_strokeDashArray != null) {
			result += String.format("stroke-dasharray:%d %d;", m_strokeDashArray.length, m_strokeDashArray.space);
		}

		return result;
	}

	protected void setStyleAttribute(HTMLNode svgComponentNode) {
		svgComponentNode.setAttribute("style", computeStyleAttribute());
	}

	protected void setStyleAttribute(JsonMap svgComponentNode) {
		svgComponentNode.setAttribute("style", computeStyleAttribute());
	}
}
