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

import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.form.Form;
import tui.ui.components.form.ModalForm;
import tui.utils.TUIUtils;

public class TModalForm extends TForm {

	private String m_openButtonLabel;
	private boolean m_opened = false;

	TModalForm(long tuid, String title, String target, TClient tClient) {
		super(tuid, title, target, tClient);
	}

	public String getOpenButtonLabel() {
		return m_openButtonLabel;
	}

	public void open() {
		if(m_opened) {
			throw new IllegalStateException("ModalForm is already opened");
		}
		m_opened = true;
	}

	public void close() {
		if(!m_opened) {
			throw new IllegalStateException("ModalForm is already closed");
		}
		m_opened = false;
	}

	public boolean isOpened() {
		return m_opened;
	}

	@Override
	public void enterInput(String fieldName, String value) {
		checkIsOpened();
		super.enterInput(fieldName, value);
	}

	@Override
	public void submit() {
		checkIsOpened();
		super.submit();
	}

	private void checkIsOpened() {
		if(!m_opened) {
			throw new IllegalStateException("ModalForm is not opened");
		}
	}

	public static TModalForm parse(JsonMap json, TClient client) {
		final long tuid = JsonConstants.readTUID(json);
		final String title = json.getAttribute(ModalForm.JSON_ATTRIBUTE_TITLE);
		final String target = json.getAttribute(ModalForm.JSON_ATTRIBUTE_TARGET);
		final TModalForm result = new TModalForm(tuid, title, target, client);

		parseInputs(json, result);

		if(json.hasAttribute(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS)) {
			result.m_refreshListeners.addAll(
					TUIUtils.parseTUIDsSeparatedByComa(json.getAttribute(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS)));
		}

		if(json.hasAttribute(Form.JSON_ATTRIBUTE_OPENS_PAGE_SOURCE)) {
			result.m_opensPageSource = json.getAttribute(Form.JSON_ATTRIBUTE_OPENS_PAGE_SOURCE);
		}

		result.m_openButtonLabel = json.getAttribute(ModalForm.JSON_OPEN_BUTTON_LABEL);

		return result;
	}
}
