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

package tui.ui.components.form;

import tui.html.HTMLNode;
import tui.json.JsonMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormInputRadio extends FormInput {

	public static final String HTML_TYPE = "radio";
	public static final String JSON_TYPE = "from_input_radio";

	private final Map<String, String> m_options = new LinkedHashMap<>();
	private boolean m_showOptionsOnSameLine = false; // Shows options on the same line

	public FormInputRadio(String label, String name) {
		super(JSON_TYPE, HTML_TYPE, label, name);
	}

	public FormInputRadio addOption(String label, String value) {
		m_options.put(label, value);
		return this;
	}

	public FormInputRadio showOptionsOnSameLine(boolean enabled) {
		m_showOptionsOnSameLine = enabled;
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("div");
		for(Map.Entry<String, String> optionEntry : m_options.entrySet()) {
			final String label = optionEntry.getKey();
			final String value = optionEntry.getValue();

			final HTMLNode inputDiv = result.append(new HTMLNode("div"));
			if(m_showOptionsOnSameLine) {
				inputDiv.addClass("tui-form-radio-inline");
			}
			inputDiv.append(new HTMLNode("input")
					.setAttribute("type", HTML_TYPE)
					.setAttribute("name", m_name)
					.setAttribute("value", value));
			inputDiv.append(new HTMLNode("label")
					.setAttribute("for", m_name)
					.setText(label));
		}
		return result;
	}

	@Override
	public JsonMap toJsonObject() {
		final JsonMap result = new JsonMap(m_jsonType);
		result.setAttribute("label", m_label);
		result.setAttribute("name", m_name);
		final JsonMap options = new JsonMap(null);
		m_options.forEach(options::setAttribute);
		result.setChild("options", options);
		return result;
	}
}
