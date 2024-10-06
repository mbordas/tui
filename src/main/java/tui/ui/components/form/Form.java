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

import tui.html.HTMLFetchErrorMessage;
import tui.html.HTMLNode;
import tui.http.FormRequest;
import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.json.JsonParser;
import tui.json.JsonString;
import tui.ui.components.Paragraph;
import tui.ui.components.UIComponent;
import tui.ui.components.layout.Layouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Form extends UIComponent {

	public static final String HTML_CLASS = "tui-form";
	public static final String HTML_CLASS_FIELD = "tui-form-input";

	public static final String JSON_TYPE = "form";
	public static final String JSON_TYPE_FORM_SUBMISSION_RESPONSE = "formSubmissionResponse";

	private final String m_title;

	private final String m_target; // Web service path

	private final Set<FormInput> m_inputs = new LinkedHashSet<>();
	private final Collection<UIComponent> m_refreshListeners = new ArrayList<>();

	public Form(String title, String target) {
		m_title = title;
		m_target = target;
	}

	public String getTitle() {
		return m_title;
	}

	public String getTarget() {
		return m_target;
	}

	public Set<FormInput> getInputs() {
		return m_inputs;
	}

	public Collection<UIComponent> getRefreshListeners() {
		return m_refreshListeners;
	}

	public FormInputCheckbox createInputCheckbox(String label, String name) {
		final FormInputCheckbox result = new FormInputCheckbox(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputString createInputString(String label, String name) {
		final FormInputString result = new FormInputString(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputNumber createInputNumber(String label, String name) {
		final FormInputNumber result = new FormInputNumber(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputDay createInputDay(String label, String name) {
		final FormInputDay result = new FormInputDay(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputDayHHmm createInputDayHHmm(String label, String name) {
		final FormInputDayHHmm result = new FormInputDayHHmm(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputPassword createInputPassword(String label, String name) {
		final FormInputPassword result = new FormInputPassword(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputRadio createInputRadio(String label, String name) {
		final FormInputRadio result = new FormInputRadio(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputEmail createInputEmail(String label, String name) {
		final FormInputEmail result = new FormInputEmail(label, name);
		m_inputs.add(result);
		return result;
	}

	public FormInputFile createInputFile(String label, String name) {
		final FormInputFile result = new FormInputFile(label, name);
		m_inputs.add(result);
		return result;
	}

	/**
	 * Registered listener will be refreshed each time the form will be successfully submitted.
	 */
	public void registerRefreshListener(UIComponent listener) {
		m_refreshListeners.add(listener);
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("form", false)
				.setAttribute("class", HTML_CLASS)
				.setAttribute("action", m_target)
				.setAttribute("method", "post")
				.setAttribute("enctype", FormRequest.ENCTYPE);

		HTMLFetchErrorMessage.addErrorMessageChild(result);

		final Collection<UIComponent> refreshListeners = getRefreshListeners();
		if(!refreshListeners.isEmpty()) {
			result.setAttribute("refresh-listeners", getTUIsSeparatedByComa(refreshListeners));
		}

		final HTMLNode fieldset = result.createChild("fieldset");
		fieldset.createChild("legend").setText(getTitle());
		for(FormInput input : getInputs()) {
			final HTMLNode inputDiv = fieldset.createChild("div").addClass(HTML_CLASS_FIELD);
			inputDiv.createChild("label")
					.setAttribute("for", input.getName())
					.setText(input.getLabel());

			inputDiv.append(input.toHTMLNode());
		}

		final HTMLNode formFooter = fieldset.append(new Paragraph().setAlign(Layouts.TextAlign.RIGHT).toHTMLNode());
		formFooter.createChild("button")
				.setAttribute("type", "submit")
				.setText("Submit");

		return result;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("title", m_title);
		result.setAttribute("target", m_target);
		result.createArray("refreshListeners", m_refreshListeners, (listener) -> new JsonString(JsonConstants.toId(listener.getTUID())));
		result.createArray("inputs", m_inputs, FormInput::toJsonObject);
		return result;
	}

	public static Set<Long> getRefreshListenersIds(JsonMap map) {
		final Set<Long> result = new TreeSet<>();
		final JsonArray refreshListeners = map.getArray("refreshListeners");
		final Iterator<JsonObject> iterator = refreshListeners.iterator();
		while(iterator.hasNext()) {
			final JsonString listenerId = (JsonString) iterator.next();
			result.add(Long.parseLong(listenerId.getValue()));
		}
		return result;
	}

	public static boolean isSuccessfulSubmissionResponse(String json) {
		final JsonMap jsonMap = JsonParser.parseMap(json);
		if(!JSON_TYPE_FORM_SUBMISSION_RESPONSE.equals(jsonMap.getType())) {
			return false;
		} else if(!"ok".equals(jsonMap.getAttribute("status"))) {
			return false;
		}
		return true;
	}

	public static JsonObject getSuccessfulSubmissionResponse() {
		return new JsonMap(JSON_TYPE_FORM_SUBMISSION_RESPONSE)
				.setAttribute("status", "ok")
				.setAttribute("message", "form submitted");
	}

	public static JsonObject getFailedSubmissionResponse(String message, Map<String, String> errorMessageByFieldName) {
		final JsonMap result = new JsonMap(JSON_TYPE_FORM_SUBMISSION_RESPONSE)
				.setAttribute("status", "nok")
				.setAttribute("message", message);

		final JsonMap fieldsErrorsMap = new JsonMap(null);
		for(Map.Entry<String, String> errorEntry : errorMessageByFieldName.entrySet()) {
			final String fieldName = errorEntry.getKey();
			final String errorMessage = errorEntry.getValue();
			fieldsErrorsMap.setAttribute(fieldName, errorMessage);
		}

		result.setChild("errors", fieldsErrorsMap);
		return result;
	}
}
