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

package tui.test;

import org.apache.http.HttpException;
import tui.ui.TUIComponent;
import tui.ui.Table;
import tui.ui.form.Form;
import tui.ui.form.FormInputString;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TForm {

	private final TestHTTPClient m_httpClient;
	private final Form m_form;

	private final Map<String, Object> m_enteredValues = new HashMap<>();

	public TForm(Form form, TestHTTPClient httpClient) {
		m_form = form;
		m_httpClient = httpClient;
	}

	public void enterInput(String fieldName, String value) {
		final Optional<FormInputString> anyField = m_form.getInputs().stream().filter(
				(field) -> field.getName().equals(fieldName)).findAny();
		if(anyField.isEmpty()) {
			throw new TestExecutionException("No string input found in form '%s' with name: %s", m_form.getTitle(), fieldName);
		}
		m_enteredValues.put(fieldName, value);
	}

	/**
	 * Sends form data to the backend. When successful, it refreshes listeners too.
	 */
	public void submit() throws HttpException {
		final String jsonResponse = m_httpClient.callBackend(m_form.getTarget(), m_enteredValues);
		if(!Form.isSuccessfulSubmissionResponse(jsonResponse)) {
			throw new HttpException("Unexpected web service response");
		}
		for(TUIComponent refreshListener : m_form.getRefreshListeners()) {
			if(refreshListener instanceof Table table) {
				table.refresh(m_httpClient);
			}
		}

	}
}
