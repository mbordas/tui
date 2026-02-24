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
import tui.ui.components.layout.ModalPanel;
import tui.ui.components.layout.Panel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TModalPanelTest extends TestWithBackend {

	@Test
	public void contentShouldNotBeReachableWhenPanelIsClosed() {

		final Page page = new Page("index", "/index");
		final String openButtonLabel = "Open modal panel";
		final ModalPanel modalPanel = page.append(new ModalPanel(Panel.Align.CENTER, openButtonLabel));
		modalPanel.append(new Paragraph.Text("Modal text"));

		try(TUIBackend backend = startBackend(page)) {

			final TClient client = new TClient(backend.getPort());
			client.open(page.getSource());
			final TModalPanel panel = client.getModalPanel(openButtonLabel);

			assertFalse(panel.isOpened());
			assertTrue(panel.getReachableSubComponents().isEmpty());

			panel.open();

			assertTrue(panel.isOpened());
			assertEquals(1, panel.getReachableSubComponents().size());
			final TParagraph.TText text = panel.finderOfClass(TParagraph.TText.class).getUnique();
			assertEquals("Modal text", text.getText());

		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}