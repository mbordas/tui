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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonObject {

	private final List<JsonObject> m_items = new ArrayList<>();

	public JsonArray() {
		super(null);
	}

	public JsonArray add(JsonObject item) {
		m_items.add(item);
		return this;
	}

	public JsonArray add(String value) {
		return add(new JsonString(value));
	}

	public JsonArray createArray() {
		final JsonArray result = new JsonArray();
		m_items.add(result);
		result.setPrettyPrintDepth(m_prettyPrintDepth + 1);
		return result;
	}

	public int size() {
		return m_items.size();
	}

	public JsonObject get(int i) {
		return m_items.get(i);
	}

	public List<JsonObject> getItems() {
		return m_items;
	}

	public JsonMap getMap(int i) {
		final JsonObject result = m_items.get(i);
		if(result instanceof JsonMap map) {
			return map;
		} else {
			throw new RuntimeException(
					String.format("Item #%d is of type %s != %s", i, result.getClass().getSimpleName(), JsonMap.class.getSimpleName()));
		}
	}

	public JsonArray getArray(int i) {
		final JsonObject result = m_items.get(i);
		if(result instanceof JsonArray array) {
			return array;
		} else {
			throw new RuntimeException(
					String.format("Item #%d is of type %s != %s", i, result.getClass().getSimpleName(), JsonArray.class.getSimpleName()));
		}
	}

	public Iterator<JsonObject> iterator() {
		return m_items.iterator();
	}

	@Override
	public void setPrettyPrintDepth(int depth) {
		m_prettyPrintDepth = depth;
		for(Object item : m_items) {
			if(item instanceof JsonObject jsonObject) {
				jsonObject.setPrettyPrintDepth(m_prettyPrintDepth + 1);
			}
		}
	}

	@Override
	public String toJson() {
		final StringBuilder result = new StringBuilder();
		result.append("[");
		endOfTag(result);
		final Iterator<JsonObject> iterator = m_items.iterator();
		while(iterator.hasNext()) {
			prettyPrintTab(result, 1);
			final JsonObject value = iterator.next();
			if(value == null) {
				result.append("\"\"");
			} else {
				result.append(String.format("%s", value.toJson()));
			}
			if(iterator.hasNext()) {
				result.append(",");
			}
			endOfTag(result);
		}
		prettyPrintTab(result, 0).append("]");
		return result.toString();
	}

}
