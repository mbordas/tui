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

import org.jetbrains.annotations.Nullable;
import tui.html.HTMLNode;
import tui.json.JsonMap;

public abstract class FormInput implements Comparable<FormInput> {

	protected final String m_jsonType;
	protected final String m_htmlType;
	protected final String m_label;
	protected final String m_name;
	protected String m_initialValue = null;
	protected String m_placeHolder = null;

	public FormInput(String jsonType, String htmlType, String label, String name) {
		m_jsonType = jsonType;
		m_htmlType = htmlType;
		m_label = label;
		m_name = name;
	}

	public String getHTMLType() {
		return m_htmlType;
	}

	public void setPlaceHolder(@Nullable String placeHolder) {
		m_placeHolder = placeHolder;
	}

	public String getLabel() {
		return m_label;
	}

	public String getName() {
		return m_name;
	}

	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("input");
		result.setAttribute("type", m_htmlType);
		result.setAttribute("name", m_name);
		if(m_initialValue != null) {
			result.setAttribute("value", m_initialValue);
		}
		if(m_placeHolder != null) {
			result.setAttribute("placeholder", m_placeHolder);
		}
		return result;
	}

	public JsonMap toJsonObject() {
		final JsonMap result = new JsonMap(m_jsonType);
		result.setAttribute("label", m_label);
		result.setAttribute("name", m_name);
		if(m_initialValue != null) {
			result.setAttribute("initialValue", m_initialValue);
		}
		if(m_placeHolder != null) {
			result.setAttribute("placeholder", m_placeHolder);
		}
		return result;
	}

	@Override
	public int compareTo(FormInput other) {
		return m_name.compareTo(other.m_name);
	}

	public static String getLabel(JsonMap map) {
		return map.getAttribute("label");
	}

	public static String getName(JsonMap map) {
		return map.getAttribute("name");
	}
}
