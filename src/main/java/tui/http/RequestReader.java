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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestReader {

	private static final Logger LOG = LoggerFactory.getLogger(RequestReader.class);

	public static final String FORM_ENCTYPE = "multipart/form-data";

	private record FileInput(String name, InputStream inputStream) {
	}

	private final Map<String, String> m_parameters = new HashMap<>();
	private final Map<String, FileInput> m_files = new HashMap<>();

	public RequestReader(HttpServletRequest request) {
		final String contentType = request.getContentType();
		try {
			if(contentType != null && contentType.startsWith("multipart/")) {
				MultipartConfigElement multipartConfigElement =
						new MultipartConfigElement("target/test-classes/tmp", 1024, 1024, 256);
				request.setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

				for(Part part : request.getParts()) {
					final String name = part.getName();

					if(name.startsWith("_file_")) {
						m_files.put(name.substring("_file_".length()),
								new FileInput(part.getSubmittedFileName(), part.getInputStream()));
					} else {
						m_parameters.put(name, new String(part.getInputStream().readAllBytes()));
					}
				}
			} else {
				for(Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					m_parameters.put(entry.getKey(), entry.getValue()[0]);
				}
				m_parameters.putAll(getPostContentAsMap(request));
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getStringParameter(String key) {
		return m_parameters.containsKey(key) ? m_parameters.get(key) : m_parameters.getOrDefault(key, null);
	}

	public Integer getIntegerParameter(String key) {
		final String valueStr = getStringParameter(key);
		return valueStr == null ? null : Integer.valueOf(valueStr);
	}

	public int getIntParameter(String key) {
		final Integer result = getIntegerParameter(key);
		if(result == null) {
			throw new NullPointerException(String.format("expected parameter '%s' not found in request", key));
		} else {
			return result;
		}
	}

	public int getIntParameter(String key, int defaultValue) {
		final Integer result = getIntegerParameter(key);
		if(result == null) {
			return defaultValue;
		} else {
			return result;
		}
	}

	public boolean getCheckboxParameter(String key) {
		final String valueStr = getStringParameter(key);
		if("on".equals(valueStr)) {
			return true;
		} else if("off".equals(valueStr)) {
			return false;
		} else {
			throw new IllegalArgumentException(String.format("Unexpected request parameter value for checkbox '%s': %s", key, valueStr));
		}
	}

	public InputStream getFileInputStream(String key) {
		return m_files.containsKey(key) ? m_files.get(key).inputStream : null;
	}

	static Map<String, String> getPostContentAsMap(HttpServletRequest request) throws IOException {
		final String json = getPostContent(request);
		return parsePostMap(json);
	}

	static String getPostContent(HttpServletRequest request) throws IOException {
		final ServletInputStream inputStream = request.getInputStream();
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	static Map<String, String> parsePostMap(String json) {
		if(json.trim().isEmpty()) {
			return new HashMap<>();
		}

		final Map<String, String> result = new LinkedHashMap<>();
		try {
			final JSONArray object = new JSONArray(json);
			if(object.length() > 0) {
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
			}
		} catch(Exception e) {
			LOG.error("Exception parsing json: {}", json);
			throw e;
		}
		return result;
	}
}
