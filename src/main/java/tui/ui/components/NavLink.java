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

import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.style.TextStyleSet;

public class NavLink extends UIComponent {

	public static final String HTML_CLASS = "tui-navlink";

	public static final String JSON_TYPE = "navlink";

	private String m_label;
	private String m_target;
	protected TextStyleSet m_customTextStyle = null;

	public NavLink(String label, String target) {
		m_label = label;
		m_target = target;
	}

	public TextStyleSet customTextStyle() {
		if(m_customTextStyle == null) {
			m_customTextStyle = new TextStyleSet();
		}
		return m_customTextStyle;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("a", false);
		result.setAttribute("href", m_target);
		result.setText(m_label);
		if(m_customTextStyle != null) {
			m_customTextStyle.apply(result);
		}
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("label", m_label);
		result.setAttribute("target", m_target);
		if(m_customTextStyle != null) {
			m_customTextStyle.apply(result);
		}
		return result;
	}
}
