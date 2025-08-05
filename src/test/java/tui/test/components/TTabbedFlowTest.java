/* Copyright (c) 2025, Mathieu Bordas
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

import org.junit.Test;
import tui.http.TUIBackend;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.Table;
import tui.ui.components.layout.Panel;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;
import tui.utils.TestUtils;

import java.util.List;

public class TTabbedFlowTest extends TestWithBackend {

	/**
	 * Here we check that the components under the TTabbedFlow can be found.
	 */
	@Test
	public void getChildrenComponents() throws Exception {
		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();

		final Table table = new Table("Table A", List.of("A", "B"));

		final TabbedFlow tabbedFlow = new TabbedFlow();
		final VerticalFlow tabA = tabbedFlow.createTab("TAB A");
		tabA.append(table);
		tabbedFlow.createTab("TAB B");

		try(final TUIBackend backend = startBackend(updatablePage.page())) {
			registerWebService(updatablePage.panel().getSource(), (uri, request, response) -> {
				final Panel panel = new Panel();
				panel.append(tabbedFlow);
				return panel.toJsonMap();
			});

			final TClient tClient = new TClient(backend.getPort());
			tClient.open(updatablePage.page().getSource());
			tClient.getRefreshButton(updatablePage.button().getLabel()).click();

			tClient.getTable(table.getTitle());
		}
	}

}