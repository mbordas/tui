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

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.svg.defs.SVGPatternStripes;
import tui.utils.TUIColors;

import java.awt.*;
import java.util.Locale;

public abstract class SVGComponent {

	private Long m_tuid = null;
	protected String m_display = "inline";
	private Color m_strokeColor = Color.BLACK;
	private int m_strokeWidth = 1;
	private double m_strokeOpacity = 1.0;
	private StrokeDashArray m_strokeDashArray = null;
	private Color m_fillColor = Color.BLACK;
	private SVGPatternStripes m_fillPattern = null;
	private double m_fillOpacity = 1.0;
	private String m_title = null;

	record StrokeDashArray(int length, int space) {
	}

	protected void setTUID(long tuid) {
		m_tuid = tuid;
	}

	public Long getTUID() {
		return m_tuid;
	}

	public abstract JsonMap toJsonMap();

	protected JsonMap toJsonMap(String type) {
		final JsonMap result = new JsonMap(type);
		if(m_title != null) {
			result.setAttribute(SVG.JSON_ATTRIBUTE_TITLE, m_title);
		}
		if(m_tuid != null) {
			result.setAttribute(HTMLConstants.ATTRIBUTE_ID, m_tuid);
		}
		return result;
	}

	public void hide() {
		m_display = "none";
	}

	public SVGComponent withTitle(String title) {
		m_title = title;
		return this;
	}

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

	public SVGComponent withNoFillColor() {
		m_fillColor = null;
		return this;
	}

	public SVGComponent withFillColor(Color color) {
		m_fillColor = color;
		return this;
	}

	public SVGComponent withFillColor(TUIColors.ColorHSL color) {
		m_fillColor = color.toRGB();
		return this;
	}

	public SVGComponent withFillOpacity(double value) {
		m_fillOpacity = value;
		return this;
	}

	public SVGComponent withFillPattern(SVGPatternStripes pattern) {
		m_fillPattern = pattern;
		return this;
	}

	/**
	 * Sets the style so that the component is not visible but still can trigger mouse clicks.
	 */
	public SVGComponent setTransparent() {
		withFillOpacity(0.0);
		return withStrokeWidth(0);
	}

	public String computeStyleAttribute() {
		String result = String.format(Locale.US, "display:%s;stroke:%s;stroke-width:%d;stroke-opacity:%.2f;fill:%s;fill-opacity:%.2f;",
				m_display,
				m_strokeColor == null ? "none" : TUIColors.toCSSHex(m_strokeColor),
				m_strokeWidth, m_strokeOpacity,
				computeFillProperty(),
				m_fillOpacity);

		if(m_strokeDashArray != null) {
			result += String.format("stroke-dasharray:%d %d;", m_strokeDashArray.length, m_strokeDashArray.space);
		}

		return result;
	}

	private String computeFillProperty() {
		if(m_fillPattern != null) {
			return String.format("url(#%s)", m_fillPattern.getId());
		} else if(m_fillColor != null) {
			return TUIColors.toCSSHex(m_fillColor);
		} else {
			return "none";
		}
	}

	protected void setStyleAttribute(HTMLNode svgComponentNode) {
		svgComponentNode.setAttribute("style", computeStyleAttribute());
	}

	protected void setStyleAttribute(JsonMap svgComponentNode) {
		svgComponentNode.setAttribute("style", computeStyleAttribute());
	}
}
