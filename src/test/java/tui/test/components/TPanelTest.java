/* Copyright (c) 2026, Mathieu Bordas
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
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TPanelTest extends TestWithBackend {

	@Test
	public void panelCouldBeSelectedByCustomTag() {
		final String customTag = "my custom tag";
		final Page page = new Page("index", "/index");
		page.append(new Panel(Panel.Align.VERTICAL_TOP)).append(new Paragraph.Text(TestUtils.LOREM_IPSUM));
		final Panel panel = page.append(new Panel(Panel.Align.CENTER));
		panel.setCustomTag(customTag);
		panel.append(new Paragraph.Text("Modal text"));

		try(TUIBackend backend = startBackend(page)) {

			final TClient client = new TClient(backend.getPort());
			client.open(page.getSource());

			final List<TPanel> foundPanels = client.getPanels().stream()
					.filter((tpanel) -> customTag.equals(tpanel.getCustomTag()))
					.toList();

			assertEquals(1, foundPanels.size());
			assertEquals(customTag, foundPanels.iterator().next().getCustomTag());

		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
