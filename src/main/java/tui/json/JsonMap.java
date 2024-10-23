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

package tui.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class JsonMap extends JsonObject {

	private final Map<String, JsonObject> m_children = new LinkedHashMap<>();

	public JsonMap(String type) {
		super(type);
	}

	public JsonMap(String type, long tuid) {
		super(type);
		setAttribute(JsonConstants.ATTRIBUTE_TUID, JsonConstants.toId(tuid));
	}

	public boolean hasAttribute(String key) {
		return m_children.containsKey(key);
	}

	public JsonMap setAttribute(String name, String value) {
		m_children.put(name, new JsonString(value));
		return this;
	}

	public JsonMap setAttribute(String name, long value) {
		m_children.put(name, new JsonLong(value));
		return this;
	}

	public String getAttribute(String key) {
		final String result = getAttributeOrNull(key);
		if(result == null) {
			throw new JsonException("Attribute '%s' not found", key);
		}
		return result;
	}

	public String getAttributeOrNull(String key) {
		if(!m_children.containsKey(key)) {
			return null;
		}
		final JsonObject jsonObject = m_children.get(key);
		if(jsonObject instanceof JsonString jsonString) {
			return jsonString.getValue();
		}
		throw new JsonException("Child '%s' is not a string", key);
	}

	public Map<String, JsonValue<?>> getAttributes() {
		final Map<String, JsonValue<?>> result = new LinkedHashMap<>();
		for(Map.Entry<String, JsonObject> child : m_children.entrySet()) {
			if(child.getValue() instanceof JsonValue<?> value) {
				result.put(child.getKey(), value);
			}
		}
		return result;
	}

	public Map<String, JsonMap> getMaps() {
		final Map<String, JsonMap> result = new LinkedHashMap<>();
		for(Map.Entry<String, JsonObject> child : m_children.entrySet()) {
			if(child.getValue() instanceof JsonMap map) {
				result.put(child.getKey(), map);
			}
		}
		return result;
	}

	public Map<String, JsonArray> getArrays() {
		final Map<String, JsonArray> result = new LinkedHashMap<>();
		for(Map.Entry<String, JsonObject> child : m_children.entrySet()) {
			if(child.getValue() instanceof JsonArray array) {
				result.put(child.getKey(), array);
			}
		}
		return result;
	}

	public JsonArray createArray(String name) {
		final JsonArray result = new JsonArray();
		setArray(name, result);
		return result;
	}

	public <X> JsonArray createArray(String name, Collection<X> items, Function<X, JsonObject> convertion) {
		final JsonArray result = createArray(name);
		items.forEach((item) -> result.add(convertion.apply(item)));
		return result;
	}

	public <J extends JsonObject> J setChild(String name, J child) {
		m_children.put(name, child);
		child.setPrettyPrintDepth(m_prettyPrintDepth + 1);
		return child;
	}

	public void setArray(String name, JsonArray array) {
		m_children.put(name, array);
		array.setPrettyPrintDepth(m_prettyPrintDepth + 1);
	}

	public JsonArray getArray(String name) {
		return (JsonArray) m_children.get(name);
	}

	public JsonMap getMap(String name) {
		return (JsonMap) m_children.get(name);
	}

	public void setPrettyPrintDepth(int depth) {
		m_prettyPrintDepth = depth;
		for(JsonObject child : m_children.values()) {
			child.setPrettyPrintDepth(depth + 1);
		}
	}

	@Override
	public String toJson() {
		StringBuilder result = new StringBuilder();
		result.append("{");
		endOfTag(result);
		if(m_type != null) {
			prettyPrintTab(result, 1)
					.append(String.format("\"type\": \"%s\",", m_type));
			endOfTag(result);
		}

		final Iterator<Map.Entry<String, JsonObject>> iterator = m_children.entrySet().iterator();
		while(iterator.hasNext()) {
			final Map.Entry<String, JsonObject> child = iterator.next();
			prettyPrintTab(result, 1)
					.append(String.format("\"%s\": ", child.getKey()));
			result.append(child.getValue().toJson());
			if(iterator.hasNext()) {
				result.append(",");
			}
			endOfTag(result);
		}

		result.append("}");
		return result.toString();
	}

}
