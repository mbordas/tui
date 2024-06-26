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
import tui.json.JsonTable;
import tui.test.TClient;

import java.util.ArrayList;
import java.util.Collection;
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
		final TTable freshTable = JsonTable.parseJson(json, client);
		m_rows.clear();
		m_rows.addAll(freshTable.getRows());
	}
}
