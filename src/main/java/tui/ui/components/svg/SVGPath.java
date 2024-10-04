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
import tui.ui.components.svg.defs.SVGMarker;

public class SVGPath extends SVGComponent {

	private StringBuilder m_drawing = new StringBuilder();
	private SVGMarker m_markerAtStart = null;
	private SVGMarker m_markerAtMiddle = null;
	private SVGMarker m_markerAtEnd = null;

	public SVGPath(long startX, long startY) {
		m_drawing.append(String.format("M%d,%d", startX, startY));
	}

	public SVGPath lineRelative(long x, long y) {
		m_drawing.append(String.format(" l%d,%d", x, y));
		return this;
	}

	public SVGPath lineAbsolute(long x, long y) {
		m_drawing.append(String.format(" L%d,%d", x, y));
		return this;
	}

	public SVGPath arcRelative(long rx, long ry, long rotation, boolean largeArcFlag, boolean sweepFlag, long x, long y) {
		m_drawing.append(String.format(" a%d,%d,%d,%d,%d,%d,%d", rx, ry, rotation, largeArcFlag ? 1 : 0, sweepFlag ? 1 : 0, x, y));
		return this;
	}

	public SVGPath withMarkerAtStart(SVGMarker marker) {
		m_markerAtStart = marker;
		return this;
	}

	public SVGPath withMarkerAtMiddle(SVGMarker marker) {
		m_markerAtMiddle = marker;
		return this;
	}

	public SVGPath withMarkerAtEnd(SVGMarker marker) {
		m_markerAtEnd = marker;
		return this;
	}

	public SVGPath close() {
		m_drawing.append(" Z");
		return this;
	}

	@Override
	public String computeStyleAttribute() {
		String result = super.computeStyleAttribute();
		if(m_markerAtStart != null) {
			result += String.format("marker-start:url(#%s);", m_markerAtStart.getId());
		}
		if(m_markerAtMiddle != null) {
			result += String.format("marker-middle:url(#%s);", m_markerAtMiddle.getId());
		}
		if(m_markerAtEnd != null) {
			result += String.format("marker-end:url(#%s);", m_markerAtEnd.getId());
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap("path");
		result.setAttribute("d", m_drawing.toString());
		setStyleAttribute(result);
		return result;
	}
}
