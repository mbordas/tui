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

import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonValue;
import tui.test.TClient;
import tui.ui.components.RefreshButton;
import tui.utils.TUIUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TRefreshButton extends TComponent {

	private final String m_label;
	private final Map<String, String> m_parameters = new HashMap<>();
	private final Set<Long> m_refreshListeners = new TreeSet<>();

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TRefreshButton(long tuid, TClient client, String label) {
		super(tuid, client);
		m_label = label;
	}

	public String getLabel() {
		return m_label;
	}

	/**
	 * Triggers a click on this button. All connected refreshable components will be refresh one by one. Only then this method will end.
	 * As soon as one component could not be refreshed because an error occurs, this method stops and throws {@link tui.test.TestExecutionException}.
	 */
	public void click() {
		final Map<String, Object> parameters = new TreeMap<>(m_parameters);
		for(Long listenerTUID : m_refreshListeners) {
			m_client.refresh(listenerTUID, parameters);
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

	public static TRefreshButton parse(JsonMap jsonMap, TClient tClient) {
		final long tuid = JsonConstants.readTUID(jsonMap);
		final String label = jsonMap.getAttribute(RefreshButton.JSON_ATTRIBUTE_LABEL);
		final TRefreshButton result = new TRefreshButton(tuid, tClient, label);

		final JsonMap parameters = jsonMap.getMap(RefreshButton.JSON_ATTRIBUTE_PARAMETERS);
		for(Map.Entry<String, JsonValue<?>> entry : parameters.getAttributes().entrySet()) {
			final String name = entry.getKey();
			final String value = entry.getValue().toString();
			result.m_parameters.put(name, value);
		}

		final String refreshListenersTUIDs = jsonMap.getAttributeOrNull(JsonConstants.ATTRIBUTE_REFRESH_LISTENERS);
		if(refreshListenersTUIDs != null) {
			result.m_refreshListeners.addAll(TUIUtils.parseTUIDsSeparatedByComa(refreshListenersTUIDs));
		}

		return result;
	}

}
