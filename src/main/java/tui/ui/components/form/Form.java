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

import org.jetbrains.annotations.NotNull;
import tui.html.HTMLConstants;
import tui.html.HTMLFetchErrorMessage;
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonParser;
import tui.ui.components.UIComponent;
import tui.ui.components.layout.Panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Form extends UIComponent {

	public static final String HTML_CLASS = "tui-form";
	public static final String HTML_CLASS_FIELD = "tui-form-input";

	public static final String JSON_TYPE = "form";
	public static final String JSON_TYPE_FORM_SUBMISSION_RESPONSE = "formSubmissionResponse";
	public static final String JSON_ATTRIBUTE_OPENS_PAGE_SOURCE = "opensPageSource";
	public static final String JSON_ATTRIBUTE_TITLE = "title";
	public static final String JSON_ATTRIBUTE_TARGET = "target";
	public static final String JSON_ATTRIBUTE_INPUTS = "inputs";
	public static final String JSON_ATTRIBUTE_PARAMETERS = "parameters";
	public static final String JSON_ATTRIBUTE_SUBMIT_LABEL = "submitLabel";
	public static final String JSON_PARAMETER_SUBMISSION_MESSAGE = "message";

	private final String m_title;
	private String m_submitLabel = "Submit";
	private final String m_target; // Web service path
	protected String m_opensPageSource = null; // Optional page to open when form is successfully submitted.

	private final Map<String, String> m_parameters = new HashMap<>();
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

	public void setSubmitLabel(String label) {
		m_submitLabel = label;
	}

	/**
	 * The page will open when the form is successfully submitted.
	 */
	public void opensPage(String source) {
		m_opensPageSource = source;
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

	public FormInputTextArea createInputTextArea(String label, String name) {
		final FormInputTextArea result = new FormInputTextArea(label, name);
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

	public Form addParameter(String name, String value) {
		m_parameters.put(name, value);
		return this;
	}

	/**
	 * Registered listener will be refreshed each time the form will be successfully submitted.
	 */
	public void registerRefreshListener(UIComponent listener) {
		m_refreshListeners.add(listener);
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = super.toHTMLNode("form", true)
				.addClass(HTML_CLASS)
				.setAttribute("action", m_target)
				.setAttribute("method", "post")
				.setAttribute("enctype", RequestReader.FORM_ENCTYPE);
		if(m_opensPageSource != null) {
			result.setAttribute("tui-opens-page", m_opensPageSource);
		}

		HTMLFetchErrorMessage.addErrorMessageChild(result);

		final Collection<UIComponent> refreshListeners = getRefreshListeners();
		if(!refreshListeners.isEmpty()) {
			result.setAttribute(HTMLConstants.ATTRIBUTE_REFRESH_LISTENERS, getTUIsSeparatedByComa(refreshListeners));
		}

		createFieldSet(result, false);

		return result;
	}

	protected HTMLNode createFieldSet(HTMLNode formNode, boolean isModal) {
		final HTMLNode result = formNode.createChild("fieldset");
		result.createChild("legend").setText(m_title);

		final HTMLNode inputsDiv = result.createChild("div");
		for(FormInput input : getInputs()) {
			final HTMLNode inputDiv = inputsDiv.append(createInputNodeWithLabel(getTUID(), input));
			inputDiv.addClass(HTML_CLASS_FIELD);
			inputDiv.createChild("span").addClass("tui-input-error");
		}

		for(Map.Entry<String, String> entry : m_parameters.entrySet()) {
			final String name = entry.getKey();
			final String value = entry.getValue();
			inputsDiv.createChild("input")
					.setAttribute("type", "hidden")
					.setAttribute("name", name)
					.setAttribute("value", value);
		}

		result.createChild("div")
				.setAttribute("id", String.format("form-message-%s", getTUID()))
				.addClass("tui-form-message")
				.setText(" "); // Prevents the following elements to be created under this div

		final HTMLNode formFooter = result.append(new Panel(Panel.Align.RIGHT).toHTMLNode());
		formFooter.addClass("tui-form-footer");
		if(isModal) {
			formFooter.createChild("button")
					.addClass("tui-form-close-button")
					.setAttribute("type", "button")
					.setText("Close");
		}
		formFooter.createChild("button")
				.setAttribute("type", "reset")
				.addClass("tui-form-reset-button")
				.setText("Reset");
		formFooter.createChild("button")
				.setAttribute("type", "submit")
				.setText(m_submitLabel);

		return result;
	}

	/**
	 * @param formTUID Used to generate unique identifiers for each input that has to be linked to a label element.
	 */
	static @NotNull HTMLNode createInputNodeWithLabel(long formTUID, FormInput input) {
		// We must use a unique id for the input to be linked to its label with the 'for' attribute.
		final String inputId = String.format("%d-%s", formTUID, input.getName());
		final HTMLNode inputDiv = new HTMLNode("div");
		inputDiv.createChild("label")
				.addClass("label-" + input.getHTMLType())
				.setAttribute("for", inputId)
				.setText(input.getLabel());

		final HTMLNode inputNode = input.toHTMLNode();
		inputNode.setAttribute("id", inputId);
		inputDiv.append(inputNode);
		return inputDiv;
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute(JSON_ATTRIBUTE_TITLE, m_title);
		result.setAttribute(JSON_ATTRIBUTE_TARGET, m_target);
		if(!m_refreshListeners.isEmpty()) {
			result.setAttribute(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS, getTUIsSeparatedByComa(m_refreshListeners));
		}
		result.createArray(JSON_ATTRIBUTE_INPUTS, m_inputs, FormInput::toJsonObject);
		final JsonMap parameters = result.createMap(JSON_ATTRIBUTE_PARAMETERS);
		for(final Map.Entry<String, String> parameterEntry : m_parameters.entrySet()) {
			parameters.setAttribute(parameterEntry.getKey(), parameterEntry.getValue());
		}
		result.setAttribute(JSON_ATTRIBUTE_SUBMIT_LABEL, m_submitLabel);
		if(m_opensPageSource != null) {
			result.setAttribute(JSON_ATTRIBUTE_OPENS_PAGE_SOURCE, m_opensPageSource);
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

	public static JsonMap buildSuccessfulSubmissionResponse() {
		return buildSuccessfulSubmissionResponse("form submitted");
	}

	public static JsonMap buildSuccessfulSubmissionResponse(String message) {
		return new JsonMap(JSON_TYPE_FORM_SUBMISSION_RESPONSE)
				.setAttribute("status", "ok")
				.setAttribute(JSON_PARAMETER_SUBMISSION_MESSAGE, message);
	}

	/**
	 * @param parameters Parameters to send back to frontend. It will be used if the form is set to open a page after submission.
	 *                   These parameters will be ignored when the form does not open page. See {@link #opensPage(String)}.
	 */
	public static JsonMap buildSuccessfulSubmissionResponse(Map<String, String> parameters) {
		final JsonMap result = new JsonMap(JSON_TYPE_FORM_SUBMISSION_RESPONSE)
				.setAttribute("status", "ok")
				.setAttribute(JSON_PARAMETER_SUBMISSION_MESSAGE, "form submitted");
		final JsonMap parametersMap = result.setChild("parameters", new JsonMap(null));
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			parametersMap.setAttribute(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static JsonMap buildFormUpdateSubmissionResponse(Form updatedForm) {
		final JsonMap result = new JsonMap(JSON_TYPE_FORM_SUBMISSION_RESPONSE)
				.setAttribute("status", "ok")
				.setAttribute(JSON_PARAMETER_SUBMISSION_MESSAGE, "form submitted");
		result.setChild("formUpdate", updatedForm.toJsonMap());
		return result;
	}

	public static JsonMap buildFailedSubmissionResponse(String message, Map<String, String> errorMessageByFieldName) {
		final JsonMap result = new JsonMap(JSON_TYPE_FORM_SUBMISSION_RESPONSE)
				.setAttribute("status", "nok")
				.setAttribute(JSON_PARAMETER_SUBMISSION_MESSAGE, message);

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
