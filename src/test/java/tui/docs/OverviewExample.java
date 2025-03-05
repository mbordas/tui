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

package tui.docs;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.TClient;
import tui.test.components.TSearch;
import tui.test.components.TTable;
import tui.ui.components.Page;
import tui.ui.components.Table;
import tui.ui.components.form.Search;
import tui.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OverviewExample {

	private static Page createPage() {
		Page page = new Page("Items", "/items");
		Search search = page.append(new Search("Search of items", "Go", "nameContains"));
		Table table = page.append(new Table("Found items", List.of("Reference", "Name", "Price")));
		table.setSource("/table");
		search.connectListener(table);
		return page;
	}

	record Item(String reference, String name, double price_Euro) {
	}

	private static @NotNull List<Item> createBunchOfItems() {
		final List<Item> allItems = new ArrayList<>();
		for(int i = 0; i < 50; i++) {
			allItems.add(new Item(String.format("I%012d", (int) (Math.random() * 1_000_000_000.0)), TestUtils.getRandomCityName(),
					Math.random() * 10_000.0));
		}
		return allItems;
	}

	private static void addWebServiceToBackend(TUIBackend backend, final List<Item> allItems) {
		backend.registerWebService("/table",
				(uri, request, response) -> {
					final RequestReader reader = new RequestReader(request);
					final String nameContains = reader.getStringParameter("nameContains", "");
					final Table filteredTable = new Table("Found items", List.of("Reference", "Name", "Price"));
					allItems.stream()
							.filter((item) -> nameContains.trim().isEmpty() || item.name.contains(nameContains))
							.forEach((item) -> filteredTable.append(
									Map.of("Reference", item.reference, "Name", item.name, "Price",
											String.format("%.2f €", item.price_Euro))));
					return filteredTable.toJsonMap();
				});
	}

	@Test
	public void example() throws Exception {
		final Page page = createPage();
		final List<Item> allItems = createBunchOfItems();

		try(final TUIBackend backend = new TUIBackend(8000)) {
			backend.registerPage(page);
			backend.start();
			addWebServiceToBackend(backend, allItems);

			final TClient client = new TClient(8000);
			client.open(page.getSource());
			final TSearch search = client.getSearch("Search of items");

			// Testing without filter -> the table should contain all 50 items
			assertNotNull(search);
			search.enterInput("nameContains", ""); // will select all items
			search.submit();

			TTable table = client.getTable("Found items");
			assertEquals(50, table.size());

			// Testing with one filter -> the table should only contain filtered values
			search.enterInput("nameContains", "al");
			search.submit();

			table = client.getTable("Found items");
			assertFalse(table.isEmpty());
			for(List<Object> row : table.getRows()) {
				final String name = row.get(1).toString();
				assertTrue(name.contains("al"));
			}

		}
	}

	public static void main(String[] args) throws Exception {

		final Page page = createPage();

		final TUIBackend backend = new TUIBackend(8000);
		backend.registerPage(page);
		backend.start();

		final List<Item> allItems = createBunchOfItems();
		addWebServiceToBackend(backend, allItems);

		// Now you can browse http://localhost:8000/items
	}

}
