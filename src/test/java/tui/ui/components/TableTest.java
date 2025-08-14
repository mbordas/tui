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
import tui.json.JsonObject;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TableTest extends TestWithBackend {

	@Test
	public void createComponent() {
		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();
		final String tableTitle = "Table title";

		try(TUIBackend backend = startBackend(updatablePage.page())) {
			backend.registerWebService(updatablePage.panel().getSource(), (uri, request, response) -> {
				final Panel result = new Panel(Panel.Align.CENTER);
				final Table table = result.append(new Table(tableTitle, List.of("A", "B")));
				table.append(Map.of("A", "1", "B", "0"));
				table.append(Map.of("A", "1", "B", "1"));
				return result.toJsonMap();
			});

			try(final Browser browser = startBrowser()) {
				browser.open(updatablePage.page().getSource());
				assertTrue(browser.getTables().isEmpty());

				browser.clickRefreshButton(updatablePage.button().getLabel());

				final WebElement tableElement = browser.getTable(tableTitle);
				assertEquals(2, tableElement.findElements(By.tagName("th")).size());
				assertTrue(Browser.getClasses(tableElement).contains(Table.HTML_CLASS));
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createComponentWithHiddenTitle() {
		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();
		final String tableTitle = "Table title";

		try(TUIBackend backend = startBackend(updatablePage.page())) {
			backend.registerWebService(updatablePage.panel().getSource(), (uri, request, response) -> {
				final Panel result = new Panel(Panel.Align.CENTER);
				final Table table = result.append(new Table(tableTitle, List.of("A", "B")));
				table.append(Map.of("A", "1", "B", "0"));
				table.append(Map.of("A", "1", "B", "1"));
				table.hideTitle();
				return result.toJsonMap();
			});

			try(final Browser browser = startBrowser()) {
				browser.open(updatablePage.page().getSource());
				assertTrue(browser.getTables().isEmpty());

				browser.clickRefreshButton(updatablePage.button().getLabel());

				final List<WebElement> tables = browser.getTables();
				assertEquals(1, tables.size());
				tables.forEach((tableElement) -> assertNull(tableElement.findElement(By.tagName("caption"))));
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void computeHiddenColumnsIndexes() {
		final Table table = new Table("title", List.of("A", "B", "C"));
		table.hideColumn("B");
		table.hideColumn("A");

		//
		final Set<Integer> hiddenColumnsIndexes = table.computeHiddenColumnsIndexes();
		//

		assertEquals(2, hiddenColumnsIndexes.size());
		final Iterator<Integer> iterator = hiddenColumnsIndexes.iterator();
		assertEquals(0, (int) iterator.next());
		assertEquals(1, (int) iterator.next());
	}

	@Test
	public void toJson() {
		JsonObject.PRETTY_PRINT = true;
		Table table = new Table("Test table", List.of("A", "B"));
		table.append(Map.of("A", "test & co"));

		assertEquals(String.format("""
								{
								  "type": "table",
								  "tuid": %d,
								  "title": "Test table",
								  "tableSize": "1",
								  "thead": [
								    "A",
								    "B"
								  ],
								  "tbody": [
								    [
								      "test & co",
								      ""
								    ]
								  ]
								}""",
						table.getTUID()),
				table.toJsonMap().toJson());
	}

	@Test
	public void browseNoPaging() {
		final Collection<TablePickerTest.Item> items = TableTest.buildItems(3);

		final Page page = new Page("Home", "/index");

		final Table table = new Table("Table picker", List.of("Id", "Name"));
		TableTest.putItemsInTable(items, table);
		page.append(table);

		startBackend(page);

		// Web UI
		final Browser browser = startBrowser();
		browser.open("/index");

		final WebElement tableElement = browser.getTable(table.getTitle());
		final WebElement cell = browser.getTableCellByText(tableElement, (text) -> text.equals("Item-2"));

		assert cell != null;
	}

	@Test
	public void browseWithPaging() {
		final Collection<TablePickerTest.Item> items = TableTest.buildItems(18);

		final Page page = new Page("Home", "/index");

		final Table table = new Table("Table picker", List.of("Id", "Name"));
		TableTest.putItemsInTable(items, table);
		table.setSource("/table");
		table.setPaging(7);
		page.append(new Panel(Panel.Align.CENTER)).append(table);

		startBackend(page);
		registerWebService(table.getSource(), buildWebServiceTableLoad(table.clone()));

		// Web UI
		final Browser browser = startBrowser();
		browser.open("/index");

		assertEquals("1 - 7 (18)", browser.getTableNavigationLocationText(table.getTitle()));

		// Clicking on button to previous page should do nothing
		browser.getTableNavigationButtonPrevious(table.getTitle()).click();
		wait_s(1);
		assertEquals("1 - 7 (18)", browser.getTableNavigationLocationText(table.getTitle()));

		// Clicking on button to next page should go to page #2
		browser.getTableNavigationButtonNext(table.getTitle()).click();
		wait_s(1);
		assertEquals("8 - 14 (18)", browser.getTableNavigationLocationText(table.getTitle()));

		// Clicking on button to next page should go to page #3
		browser.getTableNavigationButtonNext(table.getTitle()).click();
		wait_s(1);
		assertEquals("15 - 18 (18)", browser.getTableNavigationLocationText(table.getTitle()));

		// Clicking on button to next page should do nothing
		browser.getTableNavigationButtonNext(table.getTitle()).click();
		wait_s(1);
		assertEquals("15 - 18 (18)", browser.getTableNavigationLocationText(table.getTitle()));

		// Clicking on button to previous page should go to page #2
		browser.getTableNavigationButtonPrevious(table.getTitle()).click();
		wait_s(1);
		assertEquals("8 - 14 (18)", browser.getTableNavigationLocationText(table.getTitle()));
	}

	public static Collection<TablePickerTest.Item> buildItems(int count) {
		final Collection<TablePickerTest.Item> items = new ArrayList<>();
		for(int i = 1; i <= count; i++) {
			items.add(new TablePickerTest.Item("00" + i, "Item-" + i, "This is the content of Item-" + i));
		}
		return items;
	}

	public static void putItemsInTable(Collection<TablePickerTest.Item> items, Table table) {
		for(TablePickerTest.Item item : items) {
			table.append(Map.of("Id", item.id(), "Name", item.name()));
		}
	}

	public static TUIWebService buildWebServiceTableLoad(Table table) {
		return (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final int pageSize = requestReader.getIntParameter(Table.PARAMETER_PAGE_SIZE);
			final int pageNumber = requestReader.getIntParameter(Table.PARAMETER_PAGE_NUMBER);
			final TableData result = table.getPage(pageNumber, pageSize);
			return result.toJsonMap();
		};
	}

	public static void main(String[] args) throws Exception {
		final Collection<TablePickerTest.Item> items = buildItems(30);

		final Page page = new Page("Home", "/index");

		final Table table = new Table("Table", List.of("Id", "Name"));
		table.setSource("/table");
		table.setPaging(7);
		putItemsInTable(items, table);

		page.append(table);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);
		backend.registerWebService(table.getSource(), buildWebServiceTableLoad(table.clone()));
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}
