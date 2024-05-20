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

package tui.ui;

import tui.html.HTMLNode;
import tui.html.HTMLTable;
import tui.json.JsonMap;
import tui.json.JsonTable;
import tui.ui.form.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Table extends UIComponent {

	final String m_title;
	final List<String> m_columns = new ArrayList<>();
	final List<List<Object>> m_rows = new ArrayList<>();
	private String m_sourcePath = null;
	private boolean m_isConnectedForRefresh = false;

	public Table(String title, Collection<String> columns) {
		m_title = title;
		m_columns.addAll(columns);
	}

	public String getTitle() {
		return m_title;
	}

	public void setSource(String path) {
		m_sourcePath = path;
	}

	public List<String> getColumns() {
		return m_columns;
	}

	public List<List<Object>> getRows() {
		return m_rows;
	}

	public boolean hasSource() {
		return m_sourcePath != null;
	}

	public String getSource() {
		return m_sourcePath;
	}

	public void append(Map<String, Object> values) {
		final List<Object> row = new ArrayList<>();
		for(String column : m_columns) {
			row.add(values.get(column));
		}
		m_rows.add(row);
	}

	public int size() {
		return m_rows.size();
	}

	/**
	 * This table will automatically refresh each time the form is successfully submitted.
	 */
	public void connectForRefresh(Form form) {
		if(m_sourcePath == null) {
			throw new UIConfigurationException("Cannot connect table for refresh because its source is not set.");
		}
		form.registerRefreshListener(this);
		m_isConnectedForRefresh = true;
	}

	public boolean isConnectedForRefresh() {
		return m_isConnectedForRefresh;
	}

	@Override
	public HTMLNode toHTMLNode() {
		return HTMLTable.toHTML(this);
	}

	public JsonMap toJsonMap() {
		return JsonTable.toJson(this);
	}

}
