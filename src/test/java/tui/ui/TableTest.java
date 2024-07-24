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
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.http.TUIWebService;
import tui.json.JsonObject;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Table;
import tui.ui.components.TableData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TableTest {

	@Test
	public void toJson() {
		JsonObject.PRETTY_PRINT = true;
		Table table = new Table("Test table", List.of("A", "B"));
		table.append(Map.of("A", "test & co"));

		assertEquals("""
				{
				  "type": "table",
				  "tuid": " """ + table.getTUID() + """
				",
				  "title": "Test table",
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
				}""", table.toJsonMap().toJson());
	}

	@Test
	public void toHTML() {
		HTMLNode.PRETTY_PRINT = true;
		Page page = new Page("Table in page");
		Table table = new Table("Test table", List.of("A", "B"));
		table.append(Map.of("A", "test & co"));

		page.append(table);

		assertEquals("""
				<!DOCTYPE html><?xml version='1.0' encoding='UTF-8'?>
				<html>
				  <head>
				    <meta charset="utf-8"/>
				    <meta name="viewport" content="width=device-width, initial-scale=1"/>
				    <title>Table in page</title>
				  </head>
				  <body>
				    <main>
				      <table class="tui-table">
				        <caption>Test table</caption>
				        <thead>
				          <tr>
				            <th>A</th>
				            <th>B</th>
				          </tr>
				        </thead>
				        <tbody>
				          <tr>
				            <td>test & co</td>
				            <td/>
				          </tr>
				        </tbody>
				      </table>
				    </main>
				  </body>
				</html>
				""", page.toHTMLNode().toHTML());
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

		final UI ui = new UI();
		final Page page = new Page("Home");

		final Table table = new Table("Table", List.of("Id", "Name"));
		table.setSource("/table");
		table.setPaging(7);
		putItemsInTable(items, table);

		page.append(table);
		ui.add("/index", page);
		ui.setHTTPBackend("localhost", 8080);

		final TUIBackend backend = new TUIBackend(ui);
		backend.registerWebService(table.getSource(), buildWebServiceTableLoad(table.clone()));
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}
