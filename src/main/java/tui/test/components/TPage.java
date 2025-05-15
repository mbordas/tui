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
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;
import tui.ui.components.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class TPage {

	private final String m_title;
	private Map<String, Object> m_sessionParameters = new HashMap<>();
	private List<TComponent> m_content = new ArrayList<>();

	TPage(String title, TClient tClient) {
		m_title = title;
	}

	public Map<String, Object> getSessionParameters() {
		return m_sessionParameters;
	}

	public String getTitle() {
		return m_title;
	}

	public List<TComponent> getChildrenComponents() {
		return m_content;
	}

	public Collection<TComponent> getReachableSubComponents() {
		final Collection<TComponent> result = new ArrayList<>();
		for(TComponent component : m_content) {
			result.add(component);
			result.addAll(component.getReachableSubComponents());
		}
		return result;
	}

	public Optional<TComponent> findReachableSubComponent(Predicate<TComponent> condition) {
		for(TComponent childComponent : m_content) {
			if(childComponent.isReachable()) {
				if(condition.test(childComponent)) {
					return Optional.of(childComponent);
				} else {
					final Optional<TComponent> anyFoundComponent = childComponent.findReachableSubComponent(condition);
					if(anyFoundComponent.isPresent()) {
						return anyFoundComponent;
					}
				}
			}
		}
		return Optional.empty();
	}

	public TComponent find(long tuid) {
		return TComponent.find(tuid, m_content);
	}

	public static TPage parse(JsonMap jsonMap, TClient client) {
		final String title = jsonMap.getAttribute("title");
		TPage result = new TPage(title, client);
		final JsonMap sessionParameters = jsonMap.getMap(Page.JSON_ARRAY_SESSION_PARAMETERS);
		sessionParameters.getAttributes().forEach((key, value) -> result.m_sessionParameters.put(key, value.toString()));
		final JsonArray content = jsonMap.getArray("content");
		final Iterator<JsonObject> contentIterator = content.iterator();
		while(contentIterator.hasNext()) {
			final JsonObject componentJson = contentIterator.next();
			result.m_content.add(TComponentFactory.parse(componentJson, client));
		}
		return result;
	}

}
