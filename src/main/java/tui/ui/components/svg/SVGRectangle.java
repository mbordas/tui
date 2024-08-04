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

public class SVGRectangle extends SVGComponent {

	private long m_x, m_y, m_width, m_height;
	private long m_rx = 0, m_ry = 0;

	public SVGRectangle(long x, long y, long width, long height) {
		m_x = x;
		m_y = y;
		m_width = width;
		m_height = height;
	}

	public SVGRectangle withCornerRadius(long rx, long ry) {
		m_rx = rx;
		m_ry = ry;
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("rect");
		setStyleAttribute(result);
		result.setAttribute("x", m_x);
		result.setAttribute("y", m_y);
		result.setAttribute("width", m_width);
		result.setAttribute("height", m_height);
		result.setAttribute("xr", m_rx);
		result.setAttribute("ry", m_ry);
		return result;
	}
}
