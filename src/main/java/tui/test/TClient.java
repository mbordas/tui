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

package tui.test;

import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.json.JsonMap;
import tui.json.JsonParser;
import tui.test.components.ATPage;
import tui.test.components.BadComponentException;
import tui.test.components.TComponent;
import tui.test.components.TForm;
import tui.test.components.TPanel;
import tui.test.components.TTabbedPage;
import tui.test.components.TTable;
import tui.test.components.TTablePicker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TClient {

	private static final Logger LOG = LoggerFactory.getLogger(TClient.class);

	private ATPage m_currentPage;
	private TestHTTPClient m_httpClient;

	public TClient(String host, int port) {
		m_currentPage = null;
		m_httpClient = new TestHTTPClient(host, port);
	}

	public void open(String endPoint) throws HttpException {
		final String json = m_httpClient.callBackend(endPoint, Map.of("format", "json"));
		final JsonMap jsonMap = JsonParser.parseMap(json);
		try {
			m_currentPage = ATPage.parse(jsonMap, this);
		} catch(Exception e) {
			LOG.error("Error when opening page '{}': {}", endPoint, e.getMessage());
			LOG.debug("JsonMap:\n{}", jsonMap.toJson());
			throw e;
		}
	}

	public String getTitle() {
		return m_currentPage.getTitle();
	}

	public Collection<TComponent> getReachableSubComponents() {
		return m_currentPage.getReachableSubComponents();
	}

	public String getTabTitle() {
		if(m_currentPage instanceof TTabbedPage page) {
			return page.getTabTitle();
		} else {
			throw new BadComponentException("Current page '%s' has no tabs.", m_currentPage.getTitle());
		}
	}

	public TPanel getPanel(int index) {
		final List<TPanel> panels = getPanels();
		if(panels.isEmpty()) {
			throw new TestExecutionException("No Panel found in current page.");
		} else if(index >= panels.size()) {
			throw new TestExecutionException("Panel #%d does not exist (%d panels in page)", index, panels.size());
		} else {
			return panels.get(index);
		}
	}

	public List<TPanel> getPanels() {
		return m_currentPage.getReachableSubComponents().stream()
				.filter((component) -> component instanceof TPanel)
				.map((panel) -> (TPanel) panel)
				.toList();
	}

	public TTable getTable(String title) {
		final List<TComponent> tables = m_currentPage.getReachableSubComponents().stream()
				.filter((component) -> component instanceof TTable table && title.equals(table.getTitle()))
				.toList();
		if(tables.isEmpty()) {
			throw new TestExecutionException("No table found in current page with title: %s", title);
		} else if(tables.size() > 1) {
			throw new TestExecutionException("Multiple tables found in current page with title: %s", title);
		}
		return (TTable) tables.get(0);
	}

	public TTablePicker getTablePicker(String title) {
		final List<TComponent> tables = m_currentPage.getReachableSubComponents().stream()
				.filter((component) -> component instanceof TTablePicker tablepicker && title.equals(tablepicker.getTitle()))
				.toList();
		if(tables.isEmpty()) {
			throw new TestExecutionException("No table found in current page with title: %s", title);
		} else if(tables.size() > 1) {
			throw new TestExecutionException("Multiple tables found in current page with title: %s", title);
		}
		return (TTablePicker) tables.get(0);
	}

	public TForm getForm(String title) {
		final List<TComponent> forms = m_currentPage.getReachableSubComponents().stream()
				.filter((component) -> component instanceof TForm form && title.equals(form.getTitle()))
				.toList();
		if(forms.isEmpty()) {
			throw new TestExecutionException("No form found in current page with title: %s", title);
		} else if(forms.size() > 1) {
			throw new TestExecutionException("Multiple forms found in current page with title: %s", title);
		}
		return (TForm) forms.get(0);
	}

	public String callBackend(String target, Map<String, Object> parameters) throws HttpException {
		return m_httpClient.callBackend(target, parameters);
	}

	public TComponent find(long tuid) {
		if(m_currentPage.getTUID() == tuid) {
			return m_currentPage;
		} else {
			return m_currentPage.find(tuid);
		}
	}

	/**
	 * Refreshes component by using its source attribute.
	 */
	public void refresh(long tuid, Map<String, Object> data) throws HttpException {
		final TComponent componentToRefresh = find(tuid);
		if(componentToRefresh == null) {
			throw new ComponentNoReachableException("Component with tuid=%d is not reachable", tuid);
		}

		if(componentToRefresh instanceof TTable table) {
			table.refresh(data);
		}
	}

}
