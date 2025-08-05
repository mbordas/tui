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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RequestReader {

	private static final Logger LOG = LoggerFactory.getLogger(RequestReader.class);

	public static final String FORM_ENCTYPE = "multipart/form-data";
	public static final String FORMAT_DAY = "yyyy-MM-dd";

	private record FileInput(String name, InputStream inputStream) {
	}

	private final Map<String, String> m_parameters = new HashMap<>();
	private final Map<String, FileInput> m_files = new HashMap<>();

	public RequestReader(HttpServletRequest request) {
		final String contentType = request.getContentType();
		try {
			if(contentType != null && contentType.startsWith("multipart/")) {
				MultipartConfigElement multipartConfigElement =
						new MultipartConfigElement("/tmp", 1024, 1024, 256);
				request.setAttribute("org.eclipse.multipartConfig", multipartConfigElement);
				request.setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement); // supports for older jetty version

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

	public String getStringParameter(String key, String defaultValue) {
		return m_parameters.getOrDefault(key, defaultValue);
	}

	public String getStringParameter(String key) {
		return m_parameters.getOrDefault(key, null);
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

	public long getLongParameter(String key) {
		final String stringValue = getStringParameter(key);
		if(stringValue == null) {
			throw new NullPointerException(String.format("expected parameter '%s' not found in request", key));
		} else {
			return Long.parseLong(stringValue);
		}
	}

	public Date getDateParameter(String key, Locale locale) throws ParseException {
		final String stringValue = getStringParameter(key);
		if(stringValue == null) {
			throw new NullPointerException(String.format("expected parameter '%s' not found in request", key));
		} else {
			return parseDate(stringValue, locale);
		}
	}

	public Date getDateParameter(String key, Locale locale, Date defaultValue) throws ParseException {
		final String stringValue = getStringParameter(key);
		if(stringValue == null) {
			return defaultValue;
		} else {
			return parseDate(stringValue, locale);
		}
	}

	public static Date parseDate(String yyyMMdd_HHmm, Locale locale) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", locale);
		return formatter.parse(yyyMMdd_HHmm);
	}

	public static Date parseDay(String yyyMMdd_HHmm, Locale locale) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DAY, locale);
		return formatter.parse(yyyMMdd_HHmm);
	}

	public static String toInputString(Date day, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DAY, locale);
		return formatter.format(day);
	}

	public boolean getCheckboxParameter(String key) {
		final Boolean value = getCheckboxParameter(key, null);
		if(value == null) {
			final String valueStr = getStringParameter(key);
			throw new IllegalArgumentException(String.format("Unexpected request parameter value for checkbox '%s': %s", key, valueStr));
		} else {
			return value;
		}
	}

	public Boolean getCheckboxParameter(String key, Boolean defaultValue) {
		final String valueStr = getStringParameter(key);
		return parseCheckboxParameter(valueStr, defaultValue);
	}

	public static Boolean parseCheckboxParameter(String value, Boolean defaultValue) {
		if("on".equals(value)) {
			return true;
		} else if("off".equals(value)) {
			return false;
		} else {
			return defaultValue;
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
			if(!object.isEmpty()) {
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
