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

import org.jetbrains.annotations.Nullable;
import tui.json.JsonMap;
import tui.json.JsonParser;
import tui.test.TClient;
import tui.test.TestExecutionException;
import tui.ui.components.UIRefreshableComponent;

import java.util.HashMap;
import java.util.Map;

public abstract class TRefreshableComponent extends TComponent {

	private String m_source = null;
	private Map<String, Object> m_fetchData = new HashMap<>();

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TRefreshableComponent(long tuid, TClient client) {
		super(tuid, client);
	}

	public void setSource(String source) {
		m_source = source;
	}

	public abstract void update(JsonMap jsonMap);

	public void refresh(@Nullable Map<String, Object> data) throws TestExecutionException {
		if(data != null) {
			m_fetchData.putAll(data);
		}
		final String response;
		response = m_client.callBackend(m_source, m_fetchData, false);
		final JsonMap map = JsonParser.parseMap(response);
		update(map);
	}

	protected void readSource(JsonMap map) {
		if(map.hasAttribute(UIRefreshableComponent.ATTRIBUTE_SOURCE)) {
			setSource(map.getAttribute(UIRefreshableComponent.ATTRIBUTE_SOURCE));
		}
	}
}
