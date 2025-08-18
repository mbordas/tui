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

package tui.ui.components;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.utils.TestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TableTestHTML {

	@Test
	public void hiddenHead() {
		final Table table = createTable4Cells();
		table.hideHead();

		TestUtils.assertHTMLProcedure(() -> table,
				(prefix, componentRootElement) -> {
					final WebElement headElement = componentRootElement.findElement(By.tagName("thead"));
					final WebElement headRowElement = Browser.getUniqueChild(headElement);
					assertEquals(prefix, "tr", headRowElement.getTagName());
					assertTrue(prefix, Browser.getClasses(headRowElement).contains("tui-hidden-head"));

					checkTableTag(prefix, componentRootElement);
					checkCaptionTag(prefix, componentRootElement);
					checkTBodyTag(prefix, componentRootElement);
				});
	}

	@Test
	public void hiddenTitle() {
		final Table table = createTable4Cells();
		table.hideTitle();

		TestUtils.assertHTMLProcedure(() -> table,
				(prefix, componentRootElement) -> {
					final WebElement captionElement = componentRootElement.findElement(By.tagName("caption"));
					assertEquals("none", captionElement.getCssValue("display"));

					checkTableTag(prefix, componentRootElement);
					checkTHeadTag(prefix, componentRootElement);
					checkTBodyTag(prefix, componentRootElement);
				});
	}

	@Test
	public void regular() {
		final Table table = createTable4Cells();

		TestUtils.assertHTMLProcedure(() -> table,
				(prefix, componentRootElement) -> {
					checkTableTag(prefix, componentRootElement);
					checkCaptionTag(prefix, componentRootElement);
					checkTHeadTag(prefix, componentRootElement);
					checkTBodyTag(prefix, componentRootElement);
				});
	}

	private static void checkTBodyTag(String prefix, WebElement tableElement) {
		final WebElement bodyElement = tableElement.findElement(By.tagName("tbody"));
		final List<WebElement> bodyRowElements = bodyElement.findElements(By.tagName("tr"));
		assertEquals(prefix, 2, bodyRowElements.size());
		checkRow(prefix, bodyRowElements.get(0), "1", "2");
		checkRow(prefix, bodyRowElements.get(1), "3", "4");
	}

	private static void checkTHeadTag(String prefix, WebElement tableElement) {
		final WebElement headElement = tableElement.findElement(By.tagName("thead"));
		final WebElement headRowElement = Browser.getUniqueChild(headElement);
		assertEquals(prefix, "tr", headRowElement.getTagName());

		final List<WebElement> headCells = headRowElement.findElements(By.tagName("th"));
		assertEquals(prefix, 2, headCells.size());
		assertEquals(prefix, "A", headCells.get(0).getText());
		assertEquals(prefix, "B", headCells.get(1).getText());
	}

	private static void checkCaptionTag(String prefix, WebElement tableElement) {
		final WebElement captionElement = tableElement.findElement(By.tagName("caption"));
		assertEquals(prefix, "Table title", captionElement.getText());
	}

	private static void checkTableTag(String prefix, WebElement tableElement) {
		assertEquals(prefix, "table", tableElement.getTagName());
		assertTrue(prefix, Browser.getClasses(tableElement).contains(Table.HTML_CLASS));
	}

	/**
	 * Creates a {@link Table} without option like this:
	 * <table>
	 *     <th><tr><td>A</td><td>B</td></tr></th>
	 *     <tbody>
	 *         <tr><td>1</td><td>2</td></tr>
	 *         <tr><td>3</td><td>4</td></tr>
	 *     </tbody>
	 * </table>
	 */
	private static @NotNull Table createTable4Cells() {
		final Table table = new Table("Table title", List.of("A", "B"));
		table.append(Map.of("A", "1", "B", "2"));
		table.append(Map.of("A", "3", "B", "4"));
		return table;
	}

	private static void checkRow(String prefix, WebElement rowElement, String... values) {
		final List<WebElement> cellElements = rowElement.findElements(By.tagName("td"));
		assertEquals(prefix, values.length, cellElements.size());
		for(int i = 0; i < values.length; i++) {
			assertEquals(values[i], cellElements.get(i).getText());
		}
	}
}
