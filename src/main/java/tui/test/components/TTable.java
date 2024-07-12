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

import org.apache.http.HttpException;
import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonException;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.json.JsonParser;
import tui.json.JsonString;
import tui.test.TClient;
import tui.ui.components.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTable extends TComponent {

	private String m_title;
	final List<String> m_columns;
	final List<List<Object>> m_rows = new ArrayList<>();
	private final String m_sourcePath;

	public TTable(long tuid, String title, Collection<String> columns, String sourcePath, TClient tClient) {
		super(tuid, tClient);
		m_title = title;
		m_columns = new ArrayList<>(columns);
		m_sourcePath = sourcePath;
	}

	public String getTitle() {
		return m_title;
	}

	public List<String> getColumns() {
		return new ArrayList<>(m_columns);
	}

	public List<List<Object>> getRows() {
		return m_rows;
	}

	public int size() {
		return m_rows.size();
	}

	public boolean isEmpty() {
		return m_rows.isEmpty();
	}

	public boolean anyCellMatch(String columnName, Object valueEquals) {
		int columnIndex = 0;
		for(String column : m_columns) {
			if(column.equals(columnName)) {
				break;
			}
			columnIndex++;
		}

		for(List<Object> row : m_rows) {
			final Object testedValue = row.get(columnIndex);
			if(valueEquals == null && testedValue == null) {
				return true;
			} else if(valueEquals != null && valueEquals.equals(testedValue)) {
				return true;
			}
		}
		return false;
	}

	public void append(Map<String, Object> values) {
		final List<Object> row = new ArrayList<>();
		for(String column : m_columns) {
			row.add(values.get(column));
		}
		m_rows.add(row);
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	public void refresh(TClient client) throws HttpException {
		final String json = client.callBackend(m_sourcePath, null);
		final TTable freshTable = parseJson(json, client);
		m_rows.clear();
		m_rows.addAll(freshTable.getRows());
	}

	public static TTable parseJson(String json, TClient client) {
		final JsonMap map = JsonParser.parseMap(json);
		return parse(map, client);
	}

	public record BaseAttributes(long tuid, String title, Collection<String> columns, String source) {
	}

	/**
	 * Reads attributes for Table and any inherited class.
	 */
	public static BaseAttributes parseBaseAttributes(JsonMap map) {
		final String title = map.getAttribute("title");
		final long tuid = JsonConstants.readTUID(map);
		final String sourcePath = map.getAttributeOrNull(Table.ATTRIBUTE_SOURCE);

		final JsonArray thead = map.getArray("thead");
		final Collection<String> columns = new ArrayList<>();
		final Iterator<JsonObject> theadIterator = thead.iterator();
		while(theadIterator.hasNext()) {
			final JsonObject columnObject = theadIterator.next();
			if(columnObject instanceof JsonString columnString) {
				columns.add(columnString.getValue());
			} else {
				throw new JsonException("Unexpected json type: %s", columnObject.getClass().getCanonicalName());
			}
		}
		return new BaseAttributes(tuid, title, columns, sourcePath);
	}

	public static TTable parse(JsonMap map, TClient client) {
		final BaseAttributes baseAttributes = parseBaseAttributes(map);

		final TTable result = new TTable(baseAttributes.tuid, baseAttributes.title, baseAttributes.columns, baseAttributes.source, client);

		loadRows(map, baseAttributes.columns, result);

		return result;
	}

	public static void loadRows(JsonMap map, Collection<String> columns, TTable result) {
		final JsonArray array = map.getArray("tbody");
		final Iterator<JsonObject> rowIterator = array.iterator();
		while(rowIterator.hasNext()) {
			final JsonObject rowObject = rowIterator.next();
			if(rowObject instanceof JsonArray rowArray) {
				Map<String, Object> row = new LinkedHashMap<>();
				int c = 0;
				for(String column : columns) {
					final JsonObject cellObject = rowArray.get(c++);
					if(cellObject instanceof JsonString cellString) {
						row.put(column, cellString.getValue());
					} else {
						throw new JsonException("Unexpected json type: %s", cellObject.getClass().getCanonicalName());
					}
				}
				result.append(row);
			} else {
				throw new JsonException("Unexpected json type: %s", rowObject.getClass().getCanonicalName());
			}
		}
	}
}
