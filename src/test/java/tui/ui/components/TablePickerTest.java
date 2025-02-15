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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.http.TUIWebService;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.form.Search;
import tui.ui.components.layout.Panel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TablePickerTest extends TestWithBackend {

	public record Item(String id, String name, String content) {
	}

	/**
	 * Here we test a {@link TablePicker} that is filled with filtered rows (using a {@link Search}), and which uses pagination.
	 */
	@Test
	public void refreshWithPagination() throws InterruptedException {
		final Page page = new Page("Home", "/index");
		final Search search = page.append(new Search("Filter", "filter", "filter"));
		final TablePicker tablePicker = page.append(new TablePicker("Table picker", List.of("Id", "Name")));
		tablePicker.setSource("/tablePicker");
		tablePicker.setPaging(5);
		search.connectListener(tablePicker);

		final Paragraph paragraph = page.append(new Paragraph("nothing is selected"));
		paragraph.setSource("/paragraph");
		tablePicker.connectListener(paragraph);

		final Collection<Item> items = TableTest.buildItems(50);

		startBackend(page);

		registerWebService(tablePicker.getSource(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String filter = reader.getStringParameter("filter", ""); // Filtering parameter
			final int pageSize = reader.getIntParameter(Table.PARAMETER_PAGE_SIZE, 5); // Pagination parameters
			final int pageNumber = reader.getIntParameter(Table.PARAMETER_PAGE_NUMBER, 1); // Pagination parameters

			// Getting filtered items
			final List<Item> filteredItems = items.stream()
					.filter((item -> item.id.contains(filter)
							|| item.name.contains(filter)
							|| item.content.contains(filter)))
					.toList();

			// Building table with all filtered items
			final TableData data = new TableData(tablePicker.getColumns(), filteredItems.size());
			filteredItems.forEach((item -> data.append(Map.of("Id", item.id, "Name", item.name))));

			// Returning only the selected page
			return data.getPage(pageNumber, pageSize, data.size()).toJsonMap();
		});

		registerWebService(paragraph.getSource(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			Paragraph result = new Paragraph();
			result.appendNormal("%s - %s",
					reader.getStringParameter("Id"),
					reader.getStringParameter("Name"));
			return result.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());

		browser.typeSearch("Filter", "filter", "2");
		browser.submitSearch("Filter");

		WebElement table = browser.getTable(tablePicker.getTitle());
		assertEquals(1 + 5, table.findElements(By.tagName("tr")).size()); // 1 header row + 5 data rows
		// Expecting 14 filtered items in total: 2, 12, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 32, 42
		assertEquals("1 - 5 (14)", browser.getTableNavigationLocationText(tablePicker.getTitle()));

		browser.getTableNavigationButtonNext(tablePicker.getTitle()).click(); // Going to next page
		assertEquals("6 - 10 (14)", browser.getTableNavigationLocationText(tablePicker.getTitle()));

		browser.getTableCellByText(browser.getTable(tablePicker.getTitle()),
						(value) -> value.equals("Item-23"))
				.click();

		WebElement paragraphElement = browser.getByTUID(paragraph.getTUID());
		assertEquals("0023 - Item-23", paragraphElement.getText());
	}

	/**
	 * Here we create a {@link TablePicker} linked to a simple {@link Paragraph}. When a row from the table is clicked, then the text
	 * of the paragraph is updated with row related content.
	 */
	@Test
	public void browse() {
		final Collection<Item> items = TableTest.buildItems(3);

		final Page page = new Page("Home", "/index");
		final Panel panel = new Panel();
		final Paragraph paragraph = panel.append(new Paragraph("Reloadable panel"));
		paragraph.setSource("/paragraph");

		final TablePicker tablePicker = new TablePicker("Table picker", List.of("Id", "Name"));
		TableTest.putItemsInTable(items, tablePicker);
		tablePicker.connectListener(paragraph);

		page.append(tablePicker);
		page.append(panel);

		startBackend(page);

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

	public static TUIWebService buildWebServiceParagraphLoad(Collection<Item> items) {
		return (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final String id = requestReader.getStringParameter("Id");
			final Optional<Item> anyItem = items.stream()
					.filter((item) -> item.id().equals(id))
					.findAny();
			if(anyItem.isEmpty()) {
				throw new NullPointerException(String.format("No item present with id: %s", id));
			}
			final Paragraph result = new Paragraph(anyItem.get().content());
			return result.toJsonMap();
		};
	}

	public static void main(String[] args) throws Exception {
		final Collection<Item> items = TableTest.buildItems(3);

		final Page page = new Page("Home", "/index");
		final Panel panel = new Panel();
		final Paragraph paragraph = panel.append(new Paragraph("Reloadable panel"));
		paragraph.setSource("/paragraph");

		final TablePicker tablePicker = new TablePicker("Table picker", List.of("Id", "Name"));
		TableTest.putItemsInTable(items, tablePicker);
		tablePicker.connectListener(paragraph);

		page.append(tablePicker);
		page.append(panel);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);
		backend.registerWebService(paragraph.getSource(), buildWebServiceParagraphLoad(items));
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}
