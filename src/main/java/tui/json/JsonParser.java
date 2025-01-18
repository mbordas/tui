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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonParser {

	public static JsonMap parseMap(String json) {
		final JSONObject object = new JSONObject(json);
		return toMap(object);
	}

	private static JsonMap toMap(JSONObject object) {
		final String type = object.has(JsonObject.KEY_TYPE) ? object.getString(JsonObject.KEY_TYPE) : null;
		final JsonMap result = new JsonMap(type);

		final Iterator<String> keyIterator = object.keys();
		while(keyIterator.hasNext()) {
			final String key = keyIterator.next();
			final Object _object = object.get(key);
			if(_object instanceof String jsonString) {
				if(!JsonObject.KEY_TYPE.equals(key)) {
					result.setAttribute(key, jsonString);
				}
			} else if(_object instanceof Integer intValue) {
				result.setAttribute(key, intValue);
			} else if(_object instanceof Long longValue) {
				result.setAttribute(key, longValue);
			} else if(_object instanceof JSONArray jsonArray) {
				final JsonArray array = toArray(jsonArray);
				result.setArray(key, array);
			} else if(_object instanceof JSONObject jsonMap) {
				final JsonMap map = toMap(jsonMap);
				result.setChild(key, map);
			}
		}
		return result;
	}

	private static JsonArray toArray(JSONArray object) {
		final JsonArray result = new JsonArray();
		for(Object _object : object) {
			result.add(convert(_object));
		}
		return result;
	}

	private static JsonObject convert(Object object) {
		if(object instanceof String jsonString) {
			return new JsonString(jsonString);
		} else if(object instanceof JSONArray jsonArray) {
			return toArray(jsonArray);
		} else if(object instanceof JSONObject jsonObject) {
			return toMap(jsonObject);
		}
		throw new JsonException("Unsupported type: %s", object.getClass().getCanonicalName());
	}
}
