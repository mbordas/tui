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

import java.util.HashMap;
import java.util.Map;

public class NavButton extends UIComponentWithText {

	public static final String HTML_CLASS = "tui-navbutton";

	public static final String JSON_TYPE = "navbutton";

	protected final String m_label;
	protected final String m_target;
	protected final Map<String, String> m_parameters = new HashMap<>();

	public NavButton(String label, String target) {
		m_label = label;
		m_target = target;
	}

	public NavButton setParameter(String name, String value) {
		m_parameters.put(name, value);
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("form", true)
				.setAttribute("method", "POST")
				.setAttribute("action", m_target)
				.setAttribute("target", "_self")
				.addClass(HTML_CLASS);

		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			final String name = entry.getKey();
			final String value = entry.getValue();
			result.createChild("input")
					.setAttribute("type", "hidden")
					.setAttribute("name", name)
					.setAttribute("value", value);
		}

		final HTMLNode button = result.createChild("button")
				.setAttribute("type", "submit")
				.setText(m_label);

		applyCustomStyle(button);
		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("label", m_label);
		result.setAttribute("target", m_target);
		final JsonMap parameters = result.setChild("parameters", new JsonMap(null));
		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			parameters.setAttribute(entry.getKey(), entry.getValue());
		}
		applyCustomStyle(result);
		return result;
	}
}
