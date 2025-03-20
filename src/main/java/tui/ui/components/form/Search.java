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
import tui.json.JsonConstants;
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

	public static final String HTML_CLASS = "tui-search";
	public static final String HTML_CLASS_FIELD = "tui-search-input";

	public static final String JSON_TYPE = "search_form";
	public static final String JSON_ATTRIBUTE_TITLE = "title";
	public static final String JSON_ATTRIBUTE_HIDE_TITLE = "hideButton";
	public static final String JSON_ATTRIBUTE_SUBMIT_LABEL = "submitLabel";
	public static final String JSON_ATTRIBUTE_HIDE_BUTTON = "hideButton";
	public static final String JSON_ATTRIBUTE_INPUTS = "inputs";
	public static final String JSON_ATTRIBUTE_PARAMETERS = "parameters";

	private final String m_title;
	private final String m_submitLabel;
	private final Map<String, String> m_parameters = new HashMap<>();
	private final Set<FormInput> m_inputs = new LinkedHashSet<>();
	private final Collection<UIComponent> m_refreshListeners = new ArrayList<>();
	private boolean m_displayLikeForm = false;
	private boolean m_hideTitle = false;
	private boolean m_hideButton = false;

	public Search(String title, String submitLabel, String inputName) {
		m_title = title;
		m_submitLabel = submitLabel;
		createInputSearch(submitLabel, inputName);
	}

	public Search(String title, String buttonLabel) {
		m_title = title;
		m_submitLabel = buttonLabel;
	}

	public String getTitle() {
		return m_title;
	}

	public void hideTitle() {
		m_hideTitle = true;
	}

	public void displayLikeForm() {
		m_displayLikeForm = true;
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

	public FormInputCheckbox createInputCheckbox(String label, String name) {
		final FormInputCheckbox result = new FormInputCheckbox(label, name);
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
		final HTMLNode result = super.toHTMLNode("search", false);
		result.setClass(HTML_CLASS);

		final HTMLNode titleNode = result.createChild("label").setText(m_title);
		if(m_hideTitle) {
			titleNode.setStyleProperty("display", "none");
		}

		for(FormInput input : m_inputs) {
			final HTMLNode inputNode = Form.createInputNodeWithLabel(getTUID(), input);
			inputNode.addClass(m_displayLikeForm ? Form.HTML_CLASS_FIELD : HTML_CLASS_FIELD);
			result.append(inputNode);
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
				.setText(m_submitLabel);
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
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute(JSON_ATTRIBUTE_TITLE, m_title);
		result.setAttribute(JSON_ATTRIBUTE_HIDE_TITLE, String.valueOf(m_hideTitle));
		result.setAttribute(JSON_ATTRIBUTE_SUBMIT_LABEL, m_submitLabel);
		result.setAttribute(JSON_ATTRIBUTE_HIDE_BUTTON, String.valueOf(m_hideButton));
		result.setAttribute(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS, getTUIsSeparatedByComa(m_refreshListeners));
		result.createArray(JSON_ATTRIBUTE_INPUTS, m_inputs, FormInput::toJsonObject);
		final JsonMap parameters = result.createMap(JSON_ATTRIBUTE_PARAMETERS);
		for(final Map.Entry<String, String> parameterEntry : m_parameters.entrySet()) {
			parameters.setAttribute(parameterEntry.getKey(), parameterEntry.getValue());
		}
		return result;
	}

}
