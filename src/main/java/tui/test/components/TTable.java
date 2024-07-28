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
import tui.ui.components.TableData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTable extends TRefreshableComponent {

	private String m_title;
	final List<String> m_columns;
	final List<List<Object>> m_rows = new ArrayList<>();
	private final String m_sourcePath;
	private TableData.PageInfo m_pageInfo = null;
	private Integer m_lastPageNumber = null;

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

	public Map<String, Object> getRow(int index) {
		final List<Object> values = m_rows.get(index);
		int colIndex = 0;

		final Map<String, Object> result = new LinkedHashMap<>();
		for(String column : m_columns) {
			Object value = values.get(colIndex);
			result.put(column, value);
			colIndex++;
		}
		return result;
	}

	/**
	 * Returns the number of displayed rows. When paging is set, then it gives the number of rows in current page. When no paging is set,
	 * then it gives the global size of the table.
	 */
	public int size() {
		return m_rows.size();
	}

	public boolean isEmpty() {
		return m_rows.isEmpty();
	}

	public TableData.PageInfo getPageInfo() {
		return m_pageInfo;
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

	@Override
	public void refresh(Map<String, Object> data) throws HttpException {
		final String json = m_client.callBackend(m_sourcePath, data);
		parseUpdate(json);
	}

	/**
	 * Clicks on button '<' and waits table to load previous page.
	 */
	public void clickPreviousPage() throws HttpException {
		if(m_pageInfo == null) {
			throw new BadComponentException("Table has no paging set");
		}
		final Map<String, Object> data = new HashMap<>();
		data.put(Table.PARAMETER_PAGE_SIZE, m_pageInfo.pageSize());
		data.put(Table.PARAMETER_PAGE_NUMBER, Math.max(m_pageInfo.pageNumber() - 1, 1));
		refresh(data);
	}

	/**
	 * Clicks on button '>' and waits table to load next page.
	 */
	public void clickNextPage() throws HttpException {
		if(m_pageInfo == null) {
			throw new BadComponentException("Table has no paging set");
		}
		final Map<String, Object> data = new HashMap<>();
		data.put(Table.PARAMETER_PAGE_SIZE, m_pageInfo.pageSize());
		data.put(Table.PARAMETER_PAGE_NUMBER, Math.min(m_pageInfo.pageNumber() + 1, m_lastPageNumber));
		refresh(data);
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

	public static TTable parseJson(String json, TClient client) {
		final JsonMap map = JsonParser.parseMap(json);
		return parse(map, client);
	}

	/**
	 * Parses a json map describing a complete table. When you need to parse table update only, you should call parseUpdate.
	 */
	public static TTable parse(JsonMap map, TClient client) {
		final BaseAttributes baseAttributes = parseBaseAttributes(map);
		final TTable result = new TTable(baseAttributes.tuid, baseAttributes.title, baseAttributes.columns, baseAttributes.source, client);
		loadRows(map, baseAttributes.columns, result);
		loadPageInfo(map, result);
		return result;
	}

	private void parseUpdate(String json) {
		final JsonMap map = JsonParser.parseMap(json);
		m_rows.clear();
		loadRows(map, m_columns, this);
		loadPageInfo(map, this);
	}

	private static void loadPageInfo(JsonMap map, TTable result) {
		if(map.hasAttribute(TableData.ATTRIBUTE_PAGE_NUMBER)) {
			result.m_pageInfo = new TableData.PageInfo(
					Integer.parseInt(map.getAttribute(TableData.ATTRIBUTE_PAGE_NUMBER)),
					Integer.parseInt(map.getAttribute(TableData.ATTRIBUTE_PAGE_SIZE)),
					Integer.parseInt(map.getAttribute(TableData.ATTRIBUTE_FIRST_ITEM_NUMBER)),
					Integer.parseInt(map.getAttribute(TableData.ATTRIBUTE_LAST_ITEM_NUMBER))
			);
			result.m_lastPageNumber = Integer.parseInt(map.getAttribute(TableData.ATTRIBUTE_LAST_PAGE_NUMBER));
		}
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
