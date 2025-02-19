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
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.test.components.BadComponentException;
import tui.ui.components.UIComponent;
import tui.ui.components.UIRefreshableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Search extends UIComponent {

	public static final String JSON_TYPE = "search_form";
	public static final String HTML_CLASS = "tui-search-form";

	private final String m_title;
	private final String m_buttonLabel;
	private final Map<String, String> m_parameters = new HashMap<>();
	private final Set<FormInput> m_inputs = new LinkedHashSet<>();
	private final Collection<UIComponent> m_refreshListeners = new ArrayList<>();
	private boolean m_hideButton = false;

	public Search(String title, String buttonLabel, String parameterName) {
		m_title = title;
		m_buttonLabel = buttonLabel;
		createInputSearch(buttonLabel, parameterName);
	}

	public Search(String title, String buttonLabel) {
		m_title = title;
		m_buttonLabel = buttonLabel;
	}

	public String getTitle() {
		return m_title;
	}

	public Search addParameter(String name, String value) {
		m_parameters.put(name, value);
		return this;
	}

	public FormInputSearch createInputSearch(String label, String name) {
		final FormInputSearch result = new FormInputSearch(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputString createInputString(String label, String name) {
		final FormInputString result = new FormInputString(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputDayHHmm createInputDayHHmm(String label, String name) {
		final FormInputDayHHmm result = new FormInputDayHHmm(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputRadio createInputRadio(String label, String name) {
		final FormInputRadio result = new FormInputRadio(label, name);
		m_inputs.add(result);
		return result;
	}

	public Search hideButton() {
		m_hideButton = true;
		return this;
	}

	public UIRefreshableComponent connectListener(UIRefreshableComponent component) {
		if(component.getSource() == null) {
			throw new BadComponentException("%s must have a source set for reload events.", UIRefreshableComponent.class.getSimpleName());
		}
		m_refreshListeners.add(component);
		return component;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("search", false)
				.setClass(HTML_CLASS);

		result.createChild("label").setText(m_title);

		for(FormInput input : m_inputs) {
			result.append(input.toHTMLNode());
		}

		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			final String name = entry.getKey();
			final String value = entry.getValue();
			result.createChild("input")
					.setAttribute("type", "hidden")
					.setAttribute("name", name)
					.setAttribute("value", value);
		}

		final HTMLNode button = result.createChild("button")
				.setText(m_buttonLabel);
		if(m_hideButton) {
			button.setStyleProperty("display", "none");
		}

		if(!m_refreshListeners.isEmpty()) {
			result.setAttribute(HTMLConstants.ATTRIBUTE_REFRESH_LISTENERS, getTUIsSeparatedByComa(m_refreshListeners));
		}

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		//		final JsonMap result = super.toJsonMap();
		//		result.setType(JSON_TYPE);
		//		return result;
		return null;
	}

}
