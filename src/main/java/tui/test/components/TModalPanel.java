/* Copyright (c) 2026, Mathieu Bordas
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

import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;
import tui.ui.components.layout.ModalPanel;

import java.util.Iterator;

public class TModalPanel extends TPanel {

	private final String m_openButtonLabel;
	private boolean m_opened = false;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TModalPanel(long tuid, TClient client, String openButtonLabel) {
		super(tuid, client);
		m_openButtonLabel = openButtonLabel;
		m_isVisible = m_opened;
	}

	public String getOpenButtonLabel() {
		return m_openButtonLabel;
	}

	public void open() {
		if(m_opened) {
			throw new IllegalStateException("ModalPanel is already opened");
		}
		m_opened = true;
		m_isVisible = true;
	}

	public void close() {
		if(!m_opened) {
			throw new IllegalStateException("ModalPanel is already closed");
		}
		m_opened = false;
		m_isVisible = true;
	}

	public boolean isOpened() {
		return m_opened;
	}

	public static TModalPanel parse(JsonMap json, TClient client) {
		final long tuid = JsonConstants.readTUID(json);
		final String openButtonLabel = json.getAttribute(ModalPanel.JSON_OPEN_BUTTON_LABEL);
		final TModalPanel result = new TModalPanel(tuid, client, openButtonLabel);

		result.readSource(json);
		result.readParameters(json);
		final JsonArray content = json.getArray("content");
		final Iterator<JsonObject> contentIterator = content.iterator();
		while(contentIterator.hasNext()) {
			final JsonObject componentJson = contentIterator.next();
			result.m_content.add(TComponentFactory.parse(componentJson, client));
		}

		result.readCustomTag(json);

		return result;
	}
}
