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
import tui.test.TestClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TPage extends TComponent {

	private String m_title;

	private List<TComponent> m_content;

	TPage(long tuid, String title) {
		super(tuid);
		m_title = title;
		m_content = new ArrayList<>();
	}

	public String getTitle() {
		return m_title;
	}

	public static TPage parse(JsonMap jsonMap, TestClient testClient) {
		final String title = jsonMap.getAttribute("title");
		final long tuid = JsonConstants.readTUID(jsonMap);
		TPage result = new TPage(tuid, title);
		final JsonArray content = jsonMap.getArray("content");
		final Iterator<JsonObject> contentIterator = content.iterator();
		while(contentIterator.hasNext()) {
			final JsonObject componentJson = contentIterator.next();
			result.m_content.add(TComponentFactory.parse(componentJson, testClient));
		}
		return result;
	}

	public Collection<TComponent> getSubComponents() {
		return new ArrayList<>(m_content);
	}

	@Override
	public TComponent find(long tuid) {
		return TComponent.find(tuid, m_content);
	}
}
