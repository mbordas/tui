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

package tui.http;

import org.json.JSONArray;
import org.json.JSONException;
import tui.json.JsonObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public interface TUIWebService {

	JsonObject handle(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException;

	static String getStringParameter(HttpServletRequest request, String key) throws IOException {
		final Map<String, String[]> parameterMap = request.getParameterMap();
		final Map<String, String> postContentMap = getPostContentAsMap(request);

		return parameterMap.containsKey(key) ? String.valueOf(request.getParameter(key))
				: postContentMap.containsKey(key) ? postContentMap.get(key)
				: null;
	}

	static String getPostContent(HttpServletRequest request) throws IOException {
		final ServletInputStream inputStream = request.getInputStream();
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	static Map<String, String> getPostContentAsMap(HttpServletRequest request) throws IOException {
		final String json = getPostContent(request);
		return parsePostMap(json);
	}

	static Map<String, String> parsePostMap(String json) {
		final Map<String, String> result = new LinkedHashMap<>();
		final JSONArray object = new JSONArray(json);
		int index = 0;
		JSONArray entry = object.getJSONArray(index);
		while(entry != null) {
			result.put(entry.get(0).toString(), entry.get(1).toString());
			try {
				entry = object.getJSONArray(++index);
			} catch(JSONException e) {
				break;
			}
		}
		return result;
	}
}
