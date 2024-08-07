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

import tui.html.HTMLConstants;
import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.UIConfigurationException;
import tui.ui.components.form.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Table extends UIComponent {

	public static final String JSON_TYPE = "table";

	public static final String HTML_CLASS = "tui-table";
	public static final String HTML_CLASS_NAVIGATION = "tui-table-navigation";
	public static final String ATTRIBUTE_SOURCE = "source";
	public static final String PARAMETER_PAGE_NUMBER = "page_number";
	public static final String PARAMETER_PAGE_SIZE = "page_size";

	final String m_title;

	final TableData m_data;
	private String m_sourcePath = null;
	private Integer m_pageSize = null;

	public Table(String title, Collection<String> columns) {
		m_title = title;
		m_data = new TableData(columns, 0);
	}

	public String getTitle() {
		return m_title;
	}

	public void setSource(String path) {
		m_sourcePath = path;
	}

	public void setPaging(int pageSize) {
		if(!hasSource()) {
			throw new UIConfigurationException("Can't set paging for a Table without source.");
		}
		m_pageSize = pageSize;
		m_data.setPaging(pageSize);
	}

	public List<String> getColumns() {
		return m_data.getColumns();
	}

	public List<List<Object>> getRows() {
		return m_data.getRows();
	}

	public TableData getPage(int pageNumber, int pageSize) {
		return m_data.getPage(pageNumber, pageSize, size());
	}

	public Table clone() {
		final Table result = new Table(m_title, new ArrayList<>(m_data.getColumns()));
		result.m_data.copy(m_data);
		result.m_sourcePath = m_sourcePath;
		result.m_pageSize = m_pageSize;
		return result;
	}

	public boolean hasSource() {
		return m_sourcePath != null;
	}

	public String getSource() {
		return m_sourcePath;
	}

	public void append(Map<String, Object> values) {
		m_data.append(values);
		m_data.incrementTableSize();
	}

	public int size() {
		return m_data.size();
	}

	/**
	 * This table will automatically refresh each time the form is successfully submitted.
	 */
	public void connectForRefresh(Form form) {
		if(m_sourcePath == null) {
			throw new UIConfigurationException("Cannot connect table for refresh because its source is not set.");
		}
		form.registerRefreshListener(this);
	}

	@Override
	public HTMLNode toHTMLNode() {
		final HTMLNode result = new HTMLNode("table")
				.setAttribute("class", HTML_CLASS);

		if(hasSource()) {
			result.setAttribute("id", HTMLConstants.toId(getTUID()));
			result.setAttribute("tui-source", getSource());

			if(m_pageSize != null) {
				result.setAttribute("tui-page-size", m_pageSize);
				result.setAttribute("tui-table-size", size());
				result.setAttribute("tui-page-number", 1);
				result.setAttribute("tui-last-page-number", TableData.computeLastPageNumber(size(), m_pageSize));
				result.setAttribute("tui-first-item-number", 1);
				result.setAttribute("tui-last-item-number", Math.min(m_pageSize, size()));
			}
		}

		result.createChild("caption").setText(getTitle());

		final HTMLNode head = result.createChild("thead");
		final HTMLNode headRow = head.createChild("tr");
		for(String column : getColumns()) {
			headRow.createChild("th").setText(column);
		}

		final HTMLNode body = result.createChild("tbody");
		int rowNumber = 1;
		for(List<Object> _row : getRows()) {
			final HTMLNode row = body.createChild("tr");
			for(Object _cell : _row) {
				row.createChild("td").setText(_cell == null ? "" : String.valueOf(_cell));
			}
			rowNumber++;
			if(m_pageSize != null && rowNumber >= m_pageSize) {
				break;
			}
		}

		return result;
	}

	public JsonMap toJsonMap() {
		return toJsonMap(JSON_TYPE);
	}

	protected JsonMap toJsonMap(String type) {
		JsonMap result = new JsonMap(type, getTUID());
		result.setAttribute("title", getTitle());
		if(getSource() != null) {
			result.setAttribute(ATTRIBUTE_SOURCE, getSource());
		}

		TableData.fill(result, m_data);

		return result;
	}

}
