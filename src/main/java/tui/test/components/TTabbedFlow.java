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

import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTabbedFlow extends TComponent {

	private final Map<String /* title */, TVerticalFlow> m_content = new LinkedHashMap<>();

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TTabbedFlow(long tuid, TClient client) {
		super(tuid, client);
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	@Override
	protected Collection<TComponent> getChildrenComponents() {
		return List.of();
	}

	public static TTabbedFlow parse(JsonMap jsonMap, TClient tClient) {
		final long tuid = JsonConstants.readTUID(jsonMap);
		final TTabbedFlow result = new TTabbedFlow(tuid, tClient);
		final JsonArray tabs = jsonMap.getArray("tabs");
		final Iterator<JsonObject> contentIterator = tabs.iterator();
		while(contentIterator.hasNext()) {
			final JsonMap tabEntryJson = (JsonMap) contentIterator.next();
			final String tabTitle = tabEntryJson.getAttribute("title");
			final TVerticalFlow tabFlow = TVerticalFlow.parse(tabEntryJson.getMap("content"), tClient);
			result.m_content.put(tabTitle, tabFlow);
		}
		return result;
	}
}
