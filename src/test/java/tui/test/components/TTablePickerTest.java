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
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.TablePicker;
import tui.ui.components.TablePickerTest;
import tui.ui.components.layout.Panel;

import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static tui.ui.components.TableTest.buildItems;
import static tui.ui.components.TableTest.putItemsInTable;

public class TTablePickerTest extends TestWithBackend {

	private static final Logger LOG = LoggerFactory.getLogger(TTablePickerTest.class);

	@Test
	public void browseAndClick() throws HttpException {
		final Collection<TablePickerTest.Item> items = buildItems(3);

		final Page page = new Page("Home", "/index");
		final Panel panel = new Panel();
		final Paragraph paragraph = panel.append(new Paragraph("Reloadable panel"));
		paragraph.setSource("/paragraph");

		final TablePicker tablePicker = new TablePicker("Table picker", List.of("Id", "Name"));
		putItemsInTable(items, tablePicker);
		tablePicker.connectListener(paragraph);

		page.append(tablePicker);
		page.append(panel);

		startBackend(page);

		// Web service for paragraph
		registerWebService(paragraph.getSource(), TablePickerTest.buildWebServiceParagraphLoad(items));

		// Browse with TClient

		final TClient client = startClient();
		client.open("/index");

		final TPanel tpanel = client.getPanel(0);

		final TParagraph tparagraph = TComponent.getContent(TParagraph.class, tpanel.getContent()).get(0);

		assertNotNull(tparagraph);
		LOG.debug("Id of paragraph: {}", tparagraph.getTUID());

		assertEquals("Reloadable panel", tparagraph.getText()); // Initial text

		final TTablePicker ttablepicker = TComponent.getContent(TTablePicker.class, client.getReachableSubComponents()).get(0);

		assertEquals(tablePicker.getTitle(), ttablepicker.getTitle());

		ttablepicker.clickOnRow(1);

		wait_s(1);

		Assert.assertEquals("This is the content of Item-2", tparagraph.getText());
	}
}
