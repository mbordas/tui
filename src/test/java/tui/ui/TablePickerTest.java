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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.TUIBackend;
import tui.http.TUIWebService;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Panel;
import tui.ui.components.Paragraph;
import tui.ui.components.Table;
import tui.ui.components.TablePicker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TablePickerTest extends TestWithBackend {

	record Item(String id, String name, String content) {
	}

	/**
	 * Here we create a {@link TablePicker} linked to a simple {@link Paragraph}. When a row from the table is clicked, then the text
	 * of the paragraph is updated with row related content.
	 */
	@Test
	public void browse() {
		final Collection<Item> items = buildItems(3);

		final Page page = new Page("Home");
		final Panel panel = new Panel();
		final Paragraph paragraph = panel.append(new Paragraph("Reloadable panel"));
		paragraph.setSource("/paragraph");

		final TablePicker tablePicker = new TablePicker("Table picker", List.of("Id", "Name"));
		putItemsInTable(items, tablePicker);
		tablePicker.connectListener(paragraph);

		page.append(tablePicker);
		page.append(panel);

		startBackend("/index", page);

		// Web service for paragraph
		registerWebService(paragraph.getSource(), buildWebServiceParagraphLoad(items));

		// Web UI
		final Browser browser = startBrowser();
		browser.open("/index");

		assertEquals("Reloadable panel", getParagraph(browser).getText()); // Initial text

		final WebElement tablepickerElement = browser.getTable(tablePicker.getTitle());

		final WebElement cell = browser.getTableCellByText(tablepickerElement, (text) -> text.equals("Item-2"));

		assert cell != null;
		cell.click();
		wait_s(1);

		assertEquals("This is the content of Item-2", getParagraph(browser).getText());
	}

	private WebElement getParagraph(Browser browser) {
		final List<WebElement> panels = browser.getPanels();
		assertEquals(1, panels.size());
		final WebElement panelElement = panels.get(0);
		return panelElement.findElement(By.tagName("p"));
	}

	private static TUIWebService buildWebServiceParagraphLoad(Collection<Item> items) {
		return (uri, request, response) -> {
			final String id = TUIWebService.getStringParameter(request, "Id");
			final Optional<Item> anyItem = items.stream()
					.filter((item) -> item.id().equals(id))
					.findAny();
			final Paragraph result = new Paragraph(anyItem.get().content());
			return result.toJsonMap();
		};
	}

	private static Collection<Item> buildItems(int count) {
		final Collection<Item> items = new ArrayList<>();
		for(int i = 1; i <= count; i++) {
			items.add(new Item("00" + i, "Item-" + i, "This is the content of Item-" + i));
		}
		return items;
	}

	private static void putItemsInTable(Collection<Item> items, Table table) {
		for(Item item : items) {
			table.append(Map.of("Id", item.id(), "Name", item.name()));
		}
	}

	public static void main(String[] args) throws Exception {
		final Collection<Item> items = buildItems(3);

		final UI ui = new UI();
		final Page page = new Page("Home");
		final Panel panel = new Panel();
		final Paragraph paragraph = panel.append(new Paragraph("Reloadable panel"));
		paragraph.setSource("/paragraph");

		final TablePicker tablePicker = new TablePicker("Table picker", List.of("Id", "Name"));
		putItemsInTable(items, tablePicker);
		tablePicker.connectListener(paragraph);

		page.append(tablePicker);
		page.append(panel);
		ui.add("/index", page);
		ui.setHTTPBackend("localhost", 8080);

		final TUIBackend backend = new TUIBackend(ui);
		backend.registerWebService(paragraph.getSource(), buildWebServiceParagraphLoad(items));
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}
