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

package tui.test.components;

import tui.http.RequestReader;
import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;
import tui.test.TestExecutionException;
import tui.ui.components.form.FormInput;
import tui.ui.components.form.Search;
import tui.utils.TUIUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class TSearch extends TComponent {

	private final String m_title;
	private final String m_submitLabel;
	private final Map<String, String> m_parameters = new HashMap<>();
	private final Set<TForm.TFormField> m_inputs = new LinkedHashSet<>();
	private final Set<Long> m_refreshListeners = new TreeSet<>();
	private boolean m_hideButton = false;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	TSearch(long tuid, String title, String submitLabel, TClient client) {
		super(tuid, client);
		m_title = title;
		m_submitLabel = submitLabel;
	}

	public String getTitle() {
		return m_title;
	}

	public String getSubmitLabel() {
		return m_submitLabel;
	}

	public void enterInput(String fieldName, String value) {
		final Optional<TForm.TFormField> anyField = m_inputs.stream().filter(
				(field) -> field.name.equals(fieldName)).findAny();
		if(anyField.isEmpty()) {
			throw new TestExecutionException("No string input found in search '%s' with name: %s", m_title, fieldName);
		}
		anyField.get().enteredValue = value;
	}

	public void enterInputDay(String fieldName, Date day) {
		final String value = RequestReader.toInputString(day, Locale.getDefault());
		enterInput(fieldName, value);
	}

	/**
	 * Sends form data to the backend. When successful, it refreshes listeners too.
	 */
	public void submit() {
		final Map<String, Object> parameters = new HashMap<>();
		m_inputs.forEach((field) -> parameters.put(field.name, field.enteredValue));
		parameters.putAll(m_parameters);

		for(long listenerId : m_refreshListeners) {
			m_client.refresh(listenerId, parameters);
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

	public static TComponent parse(JsonMap json, TClient client) {
		final long tuid = JsonConstants.readTUID(json);
		final String title = json.getAttribute(Search.JSON_ATTRIBUTE_TITLE);
		final String submitLabel = json.getAttribute(Search.JSON_ATTRIBUTE_SUBMIT_LABEL);

		final TSearch result = new TSearch(tuid, title, submitLabel, client);
		result.m_hideButton = Boolean.parseBoolean(json.getAttribute(Search.JSON_ATTRIBUTE_HIDE_BUTTON));

		// Displayed inputs
		final JsonArray inputsArray = json.getArray(Search.JSON_ATTRIBUTE_INPUTS);
		final Iterator<JsonObject> iterator = inputsArray.iterator();
		while(iterator.hasNext()) {
			final JsonObject input = iterator.next();
			assert input instanceof JsonMap;
			JsonMap inputMap = (JsonMap) input;
			final String name = FormInput.getName(inputMap);
			final String label = FormInput.getLabel(inputMap);
			final String initialValue = FormInput.getInitialValue(inputMap);
			result.m_inputs.add(new TForm.TFormField(name, label, initialValue));
		}

		// Hidden parameters
		final JsonMap parametersMap = json.getMap(Search.JSON_ATTRIBUTE_PARAMETERS);
		parametersMap.getAttributes().forEach((key, value) -> result.m_parameters.put(key, value.toString()));

		result.m_refreshListeners.addAll(TUIUtils.parseTUIDsSeparatedByComa(json.getAttribute(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS)));

		return result;
	}

}
