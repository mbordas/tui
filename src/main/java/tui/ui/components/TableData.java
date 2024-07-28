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

package tui.ui.components;

import tui.json.JsonArray;
import tui.json.JsonException;
import tui.json.JsonMap;
import tui.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Stores columns and rows values as it is displayed on UI. It may be an entire table or a page when {@link #m_pageInfo} is set.
 * This class is used for building UI (with {@link Table} and for web services responses as well.
 */
public class TableData {

	public static final String JSON_TYPE = "table-data";
	public static final String ATTRIBUTE_TABLE_SIZE = "tableSize";
	public static final String ATTRIBUTE_PAGE_SIZE = "pageSize";
	public static final String ATTRIBUTE_PAGE_NUMBER = "pageNumber";
	public static final String ATTRIBUTE_LAST_PAGE_NUMBER = "lastPageNumber";
	public static final String ATTRIBUTE_FIRST_ITEM_NUMBER = "firstItemNumber";
	public static final String ATTRIBUTE_LAST_ITEM_NUMBER = "lastItemNumber";

	final List<String> m_columns = new ArrayList<>();
	final List<List<Object>> m_rows = new ArrayList<>();
	private int m_tableSize; // The size of the whole table.

	public record PageInfo(int pageNumber, int pageSize, int firstItemNumber, int lastItemNumber) {
	}

	private PageInfo m_pageInfo = null; // When set, it means that the instance only contains one page of a table.

	public TableData(Collection<String> columns, int tableSize) {
		m_columns.addAll(columns);
		m_tableSize = tableSize;
	}

	public List<String> getColumns() {
		return m_columns;
	}

	public List<List<Object>> getRows() {
		return m_rows;
	}

	public void setPaging(int pageSize) {
		m_pageInfo = new PageInfo(1, pageSize, 1, Math.min(tableSize(), pageSize));
	}

	public TableData getPage(int pageNumber, int pageSize, int tableSize) {
		final TableData result = new TableData(m_columns, tableSize);
		int rowIndex = pageSize * (pageNumber - 1);
		while(rowIndex < pageSize * pageNumber && rowIndex < m_rows.size()) {
			result.m_rows.add(m_rows.get(rowIndex));
			rowIndex++;
		}
		result.m_pageInfo = new PageInfo(pageNumber, pageSize, pageSize * (pageNumber - 1) + 1, rowIndex);
		return result;
	}

	public void copy(TableData data) {
		for(List<Object> row : data.getRows()) {
			m_rows.add(List.copyOf(row));
		}
		m_pageInfo = data.m_pageInfo;
		m_tableSize = data.m_tableSize;
	}

	public void append(Map<String, Object> values) {
		final List<Object> row = new ArrayList<>();
		for(String column : m_columns) {
			row.add(values.get(column));
		}
		m_rows.add(row);
	}

	public void incrementTableSize() {
		m_tableSize++;
	}

	/**
	 * Gives the size of this instance. When it contains the rows of a table 'page', then this returns the size of that page.
	 */
	public int size() {
		return m_rows.size();
	}

	/**
	 * Gives the size of the complete table from which this instance has been built.
	 */
	private int tableSize() {
		return m_tableSize;
	}

	public static int computeLastPageNumber(int tableSize, int pageSize) {
		return Math.floorDiv(tableSize - 1, pageSize) + 1;
	}

	public JsonObject toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		fill(result, this);
		return result;
	}

	public static void fill(JsonMap jsonMap, TableData data) {
		jsonMap.setAttribute(ATTRIBUTE_TABLE_SIZE, Integer.toString(data.tableSize()));
		if(data.m_pageInfo != null) {
			jsonMap.setAttribute(ATTRIBUTE_PAGE_NUMBER, Integer.toString(data.m_pageInfo.pageNumber()));
			jsonMap.setAttribute(ATTRIBUTE_PAGE_SIZE, Integer.toString(data.m_pageInfo.pageSize()));
			jsonMap.setAttribute(ATTRIBUTE_LAST_PAGE_NUMBER,
					Integer.toString(computeLastPageNumber(data.m_tableSize, data.m_pageInfo.pageSize())));
			jsonMap.setAttribute(ATTRIBUTE_FIRST_ITEM_NUMBER, Integer.toString(data.m_pageInfo.firstItemNumber()));
			jsonMap.setAttribute(ATTRIBUTE_LAST_ITEM_NUMBER, Integer.toString(data.m_pageInfo.lastItemNumber()));
		}
		final JsonArray thead = jsonMap.createArray("thead");
		for(String column : data.getColumns()) {
			thead.add(column);
		}
		final JsonArray tbody = jsonMap.createArray("tbody");
		int rowNumber = 1;
		for(List<Object> _row : data.getRows()) {
			final JsonArray row = tbody.createArray();
			for(Object _cell : _row) {
				if(_cell == null) {
					row.add("");
				} else if(_cell instanceof String cellString) {
					row.add(cellString);
				} else {
					throw new JsonException("Unsupported type: %s", _cell.getClass().getCanonicalName());
				}
			}
			if(data.m_pageInfo != null && rowNumber >= data.m_pageInfo.pageSize()) {
				break;
			}
			rowNumber++;
		}
	}
}
