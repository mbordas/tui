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
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Table;
import tui.ui.components.TableData;
import tui.ui.components.TablePickerTest;
import tui.ui.components.TableTest;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TTableTest extends TestWithBackend {

	public static String getTitle(WebElement tableElement) {
		final WebElement caption = tableElement.findElement(By.tagName("caption"));
		return caption.getText();
	}

	@Test
	public void serialization() {
		final String columnA = "Col A";
		final String columnB = "Col B";
		final Table table = new Table("Table Test Title", List.of(columnA, columnB));
		table.append(row(columnA, "value 1A", columnB, "value 1B"));
		table.append(row(columnA, "value 2A", columnB, "value 2B"));

		final String json = table.toJsonMap().toJson();

		//
		final TTable result = TTable.parseJson(json, null);
		//

		assertEquals("Table Test Title", result.getTitle());
		// Checking columns
		assertEquals(columnA, result.getColumns().get(0));
		assertEquals(columnB, result.getColumns().get(1));
		// Checking rows
		assertEquals(2, result.size());
		final List<List<Object>> rows = result.getRows();
		assertEquals("value 1A", rows.get(0).get(0));
		assertEquals("value 1B", rows.get(0).get(1));
		assertEquals("value 2A", rows.get(1).get(0));
		assertEquals("value 2B", rows.get(1).get(1));
	}

	@Test
	public void paging() throws HttpException {
		final Collection<TablePickerTest.Item> items = TableTest.buildItems(18);

		final Page page = new Page("Home", "/index");

		final Table table = new Table("Table picker", List.of("Id", "Name"));
		TableTest.putItemsInTable(items, table);
		table.setSource("/table");
		table.setPaging(7);
		page.append(table);

		startBackend(page);
		registerWebService(table.getSource(), TableTest.buildWebServiceTableLoad(table.clone()));

		final TClient client = startClient();

		client.open("/index");

		final TTable ttable = client.getTable(table.getTitle());
		assertEquals(7, ttable.size());
		{
			final TableData.PageInfo pageInfo = ttable.getPageInfo();
			assertEquals(7, pageInfo.pageSize());
			assertEquals(1, pageInfo.pageNumber());
			assertEquals(1, pageInfo.firstItemNumber());
			assertEquals(7, pageInfo.lastItemNumber());
		}

		// going to page #2
		ttable.clickNextPage();
		assertEquals(7, ttable.size());
		{
			final TableData.PageInfo pageInfo = ttable.getPageInfo();
			assertEquals(7, pageInfo.pageSize());
			assertEquals(2, pageInfo.pageNumber());
			assertEquals(8, pageInfo.firstItemNumber());
			assertEquals(14, pageInfo.lastItemNumber());
		}

		// going to page #3
		ttable.clickNextPage();
		assertEquals(4, ttable.size());
		{
			final TableData.PageInfo pageInfo = ttable.getPageInfo();
			assertEquals(7, pageInfo.pageSize());
			assertEquals(3, pageInfo.pageNumber());
			assertEquals(15, pageInfo.firstItemNumber());
			assertEquals(18, pageInfo.lastItemNumber());
		}

		// can't go to page #4
		ttable.clickNextPage();
		assertEquals(4, ttable.size());
		{
			final TableData.PageInfo pageInfo = ttable.getPageInfo();
			assertEquals(7, pageInfo.pageSize());
			assertEquals(3, pageInfo.pageNumber());
			assertEquals(15, pageInfo.firstItemNumber());
			assertEquals(18, pageInfo.lastItemNumber());
		}

		// going back to page #2
		ttable.clickPreviousPage();
		assertEquals(7, ttable.size());
		{
			final TableData.PageInfo pageInfo = ttable.getPageInfo();
			assertEquals(7, pageInfo.pageSize());
			assertEquals(2, pageInfo.pageNumber());
			assertEquals(8, pageInfo.firstItemNumber());
			assertEquals(14, pageInfo.lastItemNumber());
		}

		// can't go to page #0
		ttable.clickPreviousPage(); // to page #1
		ttable.clickPreviousPage(); // still page #1
		assertEquals(7, ttable.size());
		{
			final TableData.PageInfo pageInfo = ttable.getPageInfo();
			assertEquals(7, pageInfo.pageSize());
			assertEquals(1, pageInfo.pageNumber());
			assertEquals(1, pageInfo.firstItemNumber());
			assertEquals(7, pageInfo.lastItemNumber());
		}
	}

	private static Map<String, Object> row(String colA, String valA, String colB, String valB) {
		final Map<String, Object> result = new LinkedHashMap<>();
		result.put(colA, valA);
		result.put(colB, valB);
		return result;
	}
}