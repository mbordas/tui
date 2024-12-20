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

package tui.ui.components.form;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchTest {

	private static final Logger LOG = LoggerFactory.getLogger(SearchTest.class);

	@Test
	public void toHTML() {
		final Search search = new Search("Test title", "Test label", "search");

		HTMLNode.PRETTY_PRINT = true;
		LOG.info(search.toHTMLNode().toHTML());
	}

	record Item(long serialNumber, String manufacturer, String code, String label, double price_Euro) {
	}

	public static void main(String[] args) throws Exception {
		final String[] manufacturers = new String[] { "Best items", "Items inc.", "European items", "Useless things" };
		final String[] labelPrefixes = new String[] { "I ", "T " };
		final List<Item> items = new ArrayList<>();
		for(int i = 0; i < 100; i++) {
			final Item item = new Item(i,
					manufacturers[(int) (Math.random() * manufacturers.length - 1)],
					String.format("C_%d", 4 * i),
					String.format("%s %d", labelPrefixes[(int) (Math.random() * labelPrefixes.length - 1)], 100 + i),
					Math.random() * 100.0);
			items.add(item);
		}

		final Page page = new Page("Search", "/index");

		final Search search = new Search("Search items", "Search", "search");
		page.append(search);

		final Table table = new Table("Found items", List.of("S/N", "Manufacturer", "code", "label", "Price (€)"));
		items.forEach((item) -> table.append(Map.of("S/N", String.valueOf(item.serialNumber),
				"Manufacturer", item.manufacturer, "code", item.code, "label", item.label,
				"Price (€)", String.valueOf(item.price_Euro))));
		table.setSource("/table/search");
		table.setPaging(15);
		page.append(table);
		search.connectListener(table);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerWebService(table.getSource(), (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final String searched = requestReader.getStringParameter(search.getParameterName());
			final List<Item> foundItems = items.stream()
					.filter((item) -> searched == null
							|| searched.isEmpty()
							|| item.code.contains(searched)
							|| String.valueOf(item.serialNumber).contains(searched)
							|| String.valueOf(item.price_Euro).contains(searched)
							|| item.label.contains(searched)
							|| item.manufacturer.contains(searched))
					.toList();

			final int pageNumber = requestReader.getIntParameter(Table.PARAMETER_PAGE_NUMBER, 1);
			final int pageSize = requestReader.getIntParameter(Table.PARAMETER_PAGE_SIZE, 15);

			final Table responseTable = new Table(table.getTitle(), table.getColumns());

			foundItems.forEach((item) -> responseTable.append(Map.of("S/N", String.valueOf(item.serialNumber),
					"Manufacturer", item.manufacturer, "code", item.code, "label", item.label,
					"Price (€)", String.valueOf(item.price_Euro))));

			return responseTable.getPage(pageNumber, pageSize).toJsonMap();
		});
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}