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

package tui.test.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.json.JsonParser;
import tui.json.JsonValue;
import tui.test.TClient;
import tui.test.TestExecutionException;
import tui.ui.components.form.Form;
import tui.ui.components.form.FormInput;
import tui.utils.TUIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class TForm extends TComponent {

	private static final Logger LOG = LoggerFactory.getLogger(TForm.class);

	private String m_title;
	private String m_target;

	private final List<TFormField> m_inputs = new ArrayList<>();
	private final Set<Long> m_refreshListeners = new TreeSet<>();
	private String m_opensPageSource = null;

	static class TFormField {
		String name;
		String label;
		Object enteredValue;

		TFormField(String name, String label, Object enteredValue) {
			this.name = name;
			this.label = label;
			this.enteredValue = enteredValue;
		}
	}

	TForm(long tuid, String title, String target, TClient tClient) {
		super(tuid, tClient);
		m_title = title;
		m_target = target;
	}

	public String getTitle() {
		return m_title;
	}

	public void enterInput(String fieldName, String value) {
		final Optional<TFormField> anyField = m_inputs.stream().filter(
				(field) -> field.name.equals(fieldName)).findAny();
		if(anyField.isEmpty()) {
			throw new TestExecutionException("No string input found in form '%s' with name: %s", m_title, fieldName);
		}
		anyField.get().enteredValue = value;
	}

	/**
	 * Sends form data to the backend. When successful, it refreshes listeners too.
	 */
	public void submit() {
		final Map<String, Object> parameters = new HashMap<>();
		m_inputs.forEach((field) -> parameters.put(field.name, field.enteredValue));
		final String jsonResponse = m_client.callBackend(m_target, parameters, true);
		if(!Form.isSuccessfulSubmissionResponse(jsonResponse)) {
			throw new TestExecutionException("Unexpected web service response");
		}

		for(long listenerId : m_refreshListeners) {
			m_client.refresh(listenerId, null);
		}

		if(m_opensPageSource != null) {
			final JsonMap response = JsonParser.parseMap(jsonResponse);

			final Map<String, String> params = new HashMap<>();
			params.put("action", m_opensPageSource);
			final JsonMap responseParameters = response.getMap("parameters");
			for(Map.Entry<String, JsonValue<?>> entry : responseParameters.getAttributes().entrySet()) {
				params.put(entry.getKey(), entry.getValue().toString());
			}
			LOG.trace("Form submission opening page {}...", m_opensPageSource);
			m_client.open(m_opensPageSource, params);
		}
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	@Override
	public Collection<TComponent> getChildrenComponents() {
		return List.of();
	}

	public static TForm parse(JsonMap json, TClient client) {
		final long tuid = JsonConstants.readTUID(json);
		final String title = json.getAttribute("title");
		final String target = json.getAttribute("target");
		final TForm result = new TForm(tuid, title, target, client);

		final JsonArray array = json.getArray("inputs");
		final Iterator<JsonObject> iterator = array.iterator();
		while(iterator.hasNext()) {
			final JsonObject input = iterator.next();
			assert input instanceof JsonMap;
			JsonMap map = (JsonMap) input;
			final String name = FormInput.getName(map);
			final String label = FormInput.getLabel(map);
			result.m_inputs.add(new TFormField(name, label, null));
		}

		result.m_refreshListeners.addAll(TUIUtils.parseTUIDsSeparatedByComa(json.getAttribute(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS)));

		if(json.hasAttribute(Form.JSON_ATTRIBUTE_OPENS_PAGE_SOURCE)) {
			result.m_opensPageSource = json.getAttribute(Form.JSON_ATTRIBUTE_OPENS_PAGE_SOURCE);
		}

		return result;
	}
}
