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

package tui.ui.components.layout;

import org.junit.Test;
import tui.http.TUIBackend;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GridTest extends TestWithBackend {

	@Test
	public void toJson() {
		final Grid grid = new Grid(2, 3);
		final Paragraph paragraph = grid.set(0, 0, new Paragraph("paragraph text"));

		final JsonMap jsonMap = grid.toJsonMap();

		assertTrue(jsonMap.hasAttribute("rows"));
		assertEquals("2", jsonMap.getAttribute("rows"));

		JsonObject.PRETTY_PRINT = true;
		final String expectedJson = String.format(
				"""
						{
						  "type": "grid",
						  "tuid": %d,
						  "rows": 2,
						  "columns": 3,
						  "0_0": {
						    "type": "paragraph",
						    "tuid": %d,
						    "textAlign": "LEFT",
						    "border": "off",
						    "content": [
						      [
						        "text",
						        "paragraph text"
						      ]
						    ]
						}
						}""",
				grid.getTUID(), paragraph.getTUID(),
				paragraph.toJsonMap().toJson());
		assertEquals(expectedJson, jsonMap.toJson());
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Home", "/index");

		final Grid grid = new Grid(5, 2);

		grid.set(1, 1, new Paragraph(Layouts.TextAlign.LEFT).appendNormal("1,1"));
		grid.set(4, 0, new Paragraph(Layouts.TextAlign.RIGHT).appendNormal("4,0"));
		page.append(grid);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}
}