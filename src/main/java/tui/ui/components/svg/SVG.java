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
import tui.ui.components.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class SVG extends UIComponent {

	private final List<SVGComponent> m_components = new ArrayList<>();
	private ViewBox m_viewBox = null;

	record ViewBox(long x, long y, long width, long height) {
	}

	public void add(SVGComponent component) {
		m_components.add(component);
	}

	public void setViewBox(long x, long y, long width, long height) {
		m_viewBox = new ViewBox(x, y, width, height);
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("svg");
		if(m_viewBox != null) {
			result.setAttribute("viewBox", String.format("%d %d %d %d", m_viewBox.x, m_viewBox.y, m_viewBox.width, m_viewBox.height));
		}
		for(SVGComponent component : m_components) {
			result.addChild(component.toHTMLNode());
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		return null;
	}
}
