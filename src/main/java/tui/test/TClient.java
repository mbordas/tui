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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.json.JsonMap;
import tui.json.JsonParser;
import tui.test.components.TComponent;
import tui.test.components.TForm;
import tui.test.components.TPage;
import tui.test.components.TPanel;
import tui.test.components.TRefreshButton;
import tui.test.components.TRefreshableComponent;
import tui.test.components.TSearch;
import tui.test.components.TTable;
import tui.test.components.TTablePicker;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TClient {

	private static final Logger LOG = LoggerFactory.getLogger(TClient.class);

	private TPage m_currentPage;
	private TestHTTPClient m_httpClient;

	/**
	 * @param host Host name without protocol (ex: localhost).
	 * @param port The HTTP port
	 */
	public TClient(String host, int port) {
		m_currentPage = null;
		m_httpClient = new TestHTTPClient(host, port);
	}

	/**
	 * Builds a client that will browse a local server (serving on http://localhost:<localPort>).
	 *
	 * @param localPort The HTTP port
	 */
	public TClient(int localPort) {
		this("localhost", localPort);
	}

	public void open(String endPoint) {
		open(endPoint, Map.of());
	}

	public void open(String endPoint, Map<String, String> parameters) {
		Map<String, Object> completedParameters = new HashMap<>(parameters);
		completedParameters.put("format", "json");
		final String json = m_httpClient.callBackend(endPoint, completedParameters, false);
		final JsonMap jsonMap = JsonParser.parseMap(json);
		try {
			m_currentPage = TPage.parse(jsonMap, this);
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
		final Optional<TComponent> anyFoundForm = m_currentPage.findReachableSubComponent(
				(component) -> component instanceof TForm form && title.equals(form.getTitle()));
		if(anyFoundForm.isPresent()) {
			return (TForm) anyFoundForm.get();
		} else {
			throw new NullPointerException(String.format("Form '%s' not present in page or not reachable", title));
		}
	}

	public TSearch getSearch(String title) {
		final Optional<TComponent> anyFoundSearch = m_currentPage.findReachableSubComponent(
				(component) -> component instanceof TSearch search && title.equals(search.getTitle()));
		if(anyFoundSearch.isPresent()) {
			return (TSearch) anyFoundSearch.get();
		} else {
			throw new NullPointerException(String.format("Search '%s' not present in page or not reachable", title));
		}
	}

	public TRefreshButton getRefreshButton(String label) {
		final Optional<TComponent> anyFoundButton = m_currentPage.findReachableSubComponent(
				(component) -> component instanceof TRefreshButton button && label.equals(button.getLabel()));
		if(anyFoundButton.isPresent()) {
			return (TRefreshButton) anyFoundButton.get();
		} else {
			throw new NullPointerException(String.format("RefreshButton '%s' not present in page or not reachable", label));
		}
	}

	/**
	 * @param multipart Should be set for form submission
	 */
	public String callBackend(String target, Map<String, Object> parameters, boolean multipart) {
		Map<String, Object> params = new HashMap<>();
		params.putAll(m_currentPage.getSessionParameters());
		params.putAll(parameters);
		return m_httpClient.callBackend(target, params, multipart);
	}

	public TComponent find(long tuid) {
		return m_currentPage.find(tuid);
	}

	/**
	 * Refreshes component by using its source attribute.
	 */
	public void refresh(long tuid, Map<String, Object> data) {
		final TComponent componentToRefresh = find(tuid);
		if(componentToRefresh == null) {
			throw new ComponentNoReachableException("Component with tuid=%d is not reachable", tuid);
		}

		if(componentToRefresh instanceof TRefreshableComponent refreshableComponent) {
			refreshableComponent.refresh(data);
		} else {
			throw new TestExecutionException("Component not refreshable: %s", componentToRefresh.getClass().getSimpleName());
		}
	}

}
