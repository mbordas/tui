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

import tui.html.HTMLConstants;
import tui.html.HTMLFetchErrorMessage;
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.json.JsonMap;
import tui.ui.components.UIComponent;

import java.util.Collection;

public class ModalForm extends Form {

	public static final String HTML_CLASS = "tui-modal-form";
	public static final String HTML_CLASS_MODAL_FORM_OPEN_BUTTON = "tui-modal-form-open-button";

	public static final String JSON_TYPE = "modalform";
	public static final String JSON_OPEN_BUTTON_LABEL = "openButtonLabel";

	private final String m_openButtonLabel;

	public ModalForm(String title, String openButtonLabel, String target) {
		super(title, target);
		m_openButtonLabel = openButtonLabel;
	}

	public String getOpenButtonLabel() {
		return m_openButtonLabel;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("div", false)
				.setAttribute("class", HTML_CLASS);

		result.createChild("button")
				.setAttribute("class", HTML_CLASS_MODAL_FORM_OPEN_BUTTON)
				.setText(getOpenButtonLabel());

		final HTMLNode dialog = result.createChild("dialog")
				.setAttribute("class", "modal");

		final HTMLNode htmlForm = dialog.createChild("form")
				.setAttribute("action", getTarget())
				.setAttribute("method", "post")
				.setAttribute("enctype", RequestReader.FORM_ENCTYPE)
				.setAttribute("id", HTMLConstants.toId(getTUID()));
		if(m_opensPageSource != null) {
			htmlForm.setAttribute("tui-opens-page", m_opensPageSource);
		}

		HTMLFetchErrorMessage.addErrorMessageChild(htmlForm);

		final Collection<UIComponent> refreshListeners = getRefreshListeners();
		if(!refreshListeners.isEmpty()) {
			htmlForm.setAttribute(HTMLConstants.ATTRIBUTE_REFRESH_LISTENERS, getTUIsSeparatedByComa(refreshListeners));
		}

		createFieldSet(htmlForm, true);

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = super.toJsonMap();
		result.setType(JSON_TYPE);
		result.setAttribute(JSON_OPEN_BUTTON_LABEL, m_openButtonLabel);
		return result;
	}
}
