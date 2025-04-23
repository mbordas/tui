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

package tui.ui.components;

import tui.html.HTMLNode;
import tui.json.JsonMap;

import java.util.HashMap;
import java.util.Map;

public class DownloadButton extends UIComponent {

	public static final String HTML_CLASS = "tui-download-button";
	public static final String JSON_TYPE = "download_button";

	private final String m_label;
	private final String m_target;
	private final String m_downloadName;
	private final Map<String, String> m_parameters = new HashMap<>();

	public DownloadButton(String label, String target, String downloadName) {
		m_label = label;
		m_target = target;
		m_downloadName = downloadName;
	}

	public DownloadButton setParameter(String name, String value) {
		m_parameters.put(name, value);
		return this;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("button").addClass(HTML_CLASS);
		result.setText(m_label);
		result.setAttribute("target", m_target);
		result.setAttribute("downloadName", m_downloadName);
		result.setAttribute("onClick", "downloadFromButton(this)");

		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			final String name = entry.getKey();
			final String value = entry.getValue();
			result.createChild("input")
					.setAttribute("type", "hidden")
					.setAttribute("name", name)
					.setAttribute("value", value);
		}

		applyCustomStyle(result);

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("label", m_label);
		result.setAttribute("target", m_target);
		result.setAttribute("downloadName", m_downloadName);
		final JsonMap parameters = result.setChild("parameters", new JsonMap(null));
		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			parameters.setAttribute(entry.getKey(), entry.getValue());
		}
		applyCustomStyle(result);
		return result;
	}

}
