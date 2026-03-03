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

import org.jetbrains.annotations.NotNull;
import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTabbedFlow extends TComponent {

	private final Map<String /* title */, Collection<TComponent>> m_content = new LinkedHashMap<>();

	private String m_openTabTitle = null;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TTabbedFlow(long tuid, TClient client) {
		super(tuid, client);
	}

	public String getSelectedTabTitle() {
		return m_openTabTitle;
	}

	public void selectTab(String title) {
		if(m_content.containsKey(title)) {
			m_openTabTitle = title;
		} else {
			throw new IllegalArgumentException("No tab with title: " + title);
		}
	}

	public List<String> getTabTitles() {
		return new ArrayList<>(m_content.keySet());
	}

	@Override
	public TComponent find(long tuid) {
		for(Collection<TComponent> tabComponents : m_content.values()) {
			final TComponent foundComponent = TComponent.find(tuid, tabComponents);
			if(foundComponent != null) {
				return foundComponent;
			}
		}

		return null;
	}

	@Override
	public @NotNull Collection<TComponent> getReachableChildrenComponents() {
		if(m_openTabTitle == null /* happens when TabbedFlow has no tab*/) {
			return List.of();
		} else {
			return m_content.get(m_openTabTitle).stream()
					.map((flow) -> (TComponent) flow)
					.toList();
		}
	}

	@Override
	public String toString() {
		return String.join(" | ", m_content.keySet().stream()
				.map((key) -> key.equals(m_openTabTitle) ? "[" + key + "]" : key)
				.toList());
	}

	public static TTabbedFlow parse(JsonMap json, TClient tClient) {
		final long tuid = JsonConstants.readTUID(json);
		final TTabbedFlow result = new TTabbedFlow(tuid, tClient);
		final JsonArray tabs = json.getArray("tabs");
		final Iterator<JsonObject> contentIterator = tabs.iterator();
		while(contentIterator.hasNext()) {
			final JsonMap tabEntryJson = (JsonMap) contentIterator.next();
			final String tabTitle = tabEntryJson.getAttribute("title");
			if(result.m_openTabTitle == null) {
				result.m_openTabTitle = tabTitle;
			}
			final TVerticalFlow tabFlow = TVerticalFlow.parse(tabEntryJson.getMap("content"), tClient);
			result.m_content.put(tabTitle, tabFlow.getReachableChildrenComponents());
		}
		result.readCustomTag(json);
		return result;
	}
}
