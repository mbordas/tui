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

public class FormInputCheckbox extends FormInput {

	public static final String HTML_TYPE = "checkbox";
	public static final String JSON_TYPE = HTML_TYPE;

	private boolean m_checked = false;

	public FormInputCheckbox(String label, String name) {
		super(JSON_TYPE, HTML_TYPE, label, name);
	}

	public FormInputCheckbox check() {
		m_checked = true;
		return this;
	}

	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode();
		if(m_checked) {
			result.setAttribute("checked", null);
		}
		return result;
	}

	public JsonMap toJsonObject() {
		final JsonMap result = super.toJsonObject();
		if(m_checked) {
			result.setAttribute("checked", "true");
		} else {
			result.setAttribute("checked", "false");
		}
		return result;
	}
}
