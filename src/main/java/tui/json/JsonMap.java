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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonMap extends JsonObject {

	private final Map<String, JsonObject> m_children = new LinkedHashMap<>();

	public JsonMap(String type) {
		super(type);
	}

	public JsonMap setAttribute(String name, String value) {
		m_children.put(name, new JsonString(value));
		return this;
	}

	public JsonArray createArray(String name) {
		final JsonArray result = new JsonArray();
		m_children.put(name, result);
		result.setPrettyPrintDepth(m_prettyPrintDepth + 1);
		return result;
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
