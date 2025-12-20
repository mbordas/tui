/* Copyright (c) 2025, Mathieu Bordas
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

public class FormInputSelect extends FormInput {

	public static final String HTML_TYPE = "select";
	public static final String JSON_TYPE = "select";

	private final Map<String /* value */, String /* label */> m_options = new LinkedHashMap<>();

	public FormInputSelect(String label, String name) {
		super(JSON_TYPE, HTML_TYPE, label, name);
	}

	public FormInputSelect addOption(String value, String label) {
		m_options.put(value, label);
		return this;
	}

	public FormInputSelect setInitialValue(String value) {
		if(!m_options.containsKey(value)) {
			throw new IllegalArgumentException("Initial value is not a valid option");
		}
		m_initialValue = value;
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode(HTML_TYPE);
		result.setAttribute("name", m_name);
		for(Map.Entry<String, String> optionEntry : m_options.entrySet()) {
			final String value = optionEntry.getKey();
			final String label = optionEntry.getValue();

			final HTMLNode option = result.append(new HTMLNode("option"));
			option.setText(label);
			option.setAttribute("value", value);
			if(value.equals(m_initialValue)) {
				option.setAttribute("selected", "selected");
			}
		}
		return result;
	}

	@Override
	public JsonMap toJsonObject() {
		final JsonMap result = super.toJsonObject();
		final JsonMap options = new JsonMap(null);
		m_options.forEach(options::setAttribute);
		result.setChild("options", options);
		return result;
	}
}
