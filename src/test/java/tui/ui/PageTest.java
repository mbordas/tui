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
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.components.TComponent;
import tui.test.components.TComponentFactory;
import tui.test.components.TPage;
import tui.ui.components.Page;
import tui.ui.components.Section;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PageTest {

	@Test
	public void emptyHTML() {
		HTMLNode.PRETTY_PRINT = true;

		Page page = new Page("Empty page");
		assertEquals("""
						<!DOCTYPE html><?xml version='1.0' encoding='UTF-8'?>
						<html>
						  <head>
						    <meta charset="utf-8"/>
						    <meta name="viewport" content="width=device-width, initial-scale=1"/>
						    <title>Empty page</title>
						  </head>
						  <body>
						    <main>
						      <div class="tui-grid" style="grid-template-rows: auto;grid-template-columns: 1fr min-content 1fr">
						        <p/>
						        <div class="tui-reading-normal-area"> </div>
						        <p/>
						      </div>
						    </main>
						  </body>
						</html>
						""",
				page.toHTMLNode().toHTML().replaceAll("\r\n", "\n"));
	}

	@Test
	public void toJsonAndTPage() {
		Page page = new Page("Empty page");
		page.createSection("section A");

		final JsonMap jsonMap = page.toJsonMap();

		final TComponent _page = TComponentFactory.parse(jsonMap, null);

		assertTrue(_page instanceof TPage);
	}

	@Test
	public void toJsonMap() {
		final Page page = new Page("Empty page");
		final Section section = page.createSection("section A");

		//
		final JsonMap jsonMap = page.toJsonMap();
		//

		assertEquals(Page.JSON_TYPE, jsonMap.getType());

		//
		JsonObject.PRETTY_PRINT = false;
		final String json = jsonMap.toJson();
		//

		assertEquals(
				String.format(
						"{\"type\": \"page\",\"tuid\": \"%d\",\"title\": \"Empty page\",\"content\": [{\"type\": \"section\",\"tuid\": \"%d\",\"title\": \"section A\",\"content\": []}]}",
						page.getTUID(), section.getTUID()),
				json);

	}

}