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
import tui.http.FormRequest;
import tui.ui.components.UIComponent;

import java.util.Collection;
import java.util.Iterator;

public class ModalForm extends Form {

	public static final String HTML_CLASS = "tui-modal-form";

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
		final HTMLNode result = new HTMLNode("div")
				.setAttribute("class", HTML_CLASS);

		result.createChild("button")
				.setAttribute("class", "tui-modal-form-open-button")
				.setText(getOpenButtonLabel());

		final HTMLNode dialog = result.createChild("dialog")
				.setAttribute("class", "modal");

		final HTMLNode htmlForm = dialog.createChild("form")
				.setAttribute("action", getTarget())
				.setAttribute("method", "post")
				.setAttribute("enctype", FormRequest.ENCTYPE);

		final Collection<UIComponent> refreshListeners = getRefreshListeners();
		if(!refreshListeners.isEmpty()) {
			final Iterator<UIComponent> iterator = refreshListeners.iterator();
			final StringBuilder tuids = new StringBuilder();
			while(iterator.hasNext()) {
				tuids.append(iterator.next().getTUID());
				if(iterator.hasNext()) {
					tuids.append(",");
				}
			}
			result.setAttribute("refresh-listeners", tuids.toString());
		}

		final HTMLNode fieldset = htmlForm.createChild("fieldset");
		fieldset.createChild("legend").setText(getTitle());
		for(FormInput input : getInputs()) {
			final HTMLNode div = fieldset.createChild("div");
			final HTMLNode label = div.createChild("label")
					.setText(input.getLabel());
			label.createChild("input")
					.setAttribute("placeholder", "Text input")
					.setAttribute("name", input.getName());
		}

		htmlForm.createChild("button")
				.setAttribute("class", "tui-modal-form-cancel-button")
				.setAttribute("type", "button")
				.setText("Cancel");

		htmlForm.createChild("button")
				.setAttribute("type", "submit")
				.setText("Submit");

		return result;
	}
}
