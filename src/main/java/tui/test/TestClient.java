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

import tui.ui.Page;
import tui.ui.TUI;
import tui.ui.TUIComponent;
import tui.ui.Table;
import tui.ui.form.Form;

import java.util.List;

public class TestClient {

	private final TUI m_ui;
	private Page m_currentPage;
	private TestHTTPClient m_httpClient;

	public TestClient(TUI ui) {
		m_ui = ui;
		m_currentPage = m_ui.getDefaultPage();
		m_httpClient = new TestHTTPClient(ui.getHTTPHost(), ui.getHTTPPort());
	}

	public String getTitle() {
		return m_currentPage.getTitle();
	}

	public TTable getTable(String title) {
		final List<TUIComponent> tables = m_currentPage.getSubComponents().stream()
				.filter((component) -> component instanceof Table table && title.equals(table.getTitle()))
				.toList();
		if(tables.isEmpty()) {
			throw new TestExecutionException("No table found in current page with title: %s", title);
		} else if(tables.size() > 1) {
			throw new TestExecutionException("Multiple tables found in current page with title: %s", title);
		}
		return new TTable((Table) tables.get(0));
	}

	public TForm getForm(String title) {
		final List<TUIComponent> forms = m_currentPage.getSubComponents().stream()
				.filter((component) -> component instanceof Form form && title.equals(form.getTitle()))
				.toList();
		if(forms.isEmpty()) {
			throw new TestExecutionException("No form found in current page with title: %s", title);
		} else if(forms.size() > 1) {
			throw new TestExecutionException("Multiple forms found in current page with title: %s", title);
		}
		return new TForm((Form) forms.get(0), m_httpClient);
	}

}
