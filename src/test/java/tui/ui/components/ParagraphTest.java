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
import tui.html.HTMLNode;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.layout.Panel;
import tui.utils.TUIColors;
import tui.utils.TestUtils;

import java.awt.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParagraphTest {

	@Test
	public void html() {
		final Paragraph paragraph = new Paragraph("This is a paragraph.");
		paragraph.setSource("/paragraph");
		paragraph.addParameter("param1", "value1");

		TestUtils.assertHTMLProcedure(() -> paragraph, (prefix, element) -> {
			assertEquals(prefix, "div", element.getTagName());
			assertTrue(prefix, Browser.getClasses(element).contains(Paragraph.HTML_CLASS_CONTAINER));

			final WebElement paragraphElement = element.findElement(By.tagName("p"));
			assertEquals(prefix, "p", paragraphElement.getTagName());

			final List<WebElement> spans = paragraphElement.findElements(By.tagName("span"));
			assertEquals(prefix, 1, spans.size());
			assertEquals(prefix, "This is a paragraph.", spans.get(0).getText());
		});
	}

	@Test
	public void setTextWithoutArgsShouldNotInterpretFormat() {
		new Paragraph.Text("rate = 50%"); // should not throw exception
	}

	@Test
	public void setTextWithFormat() {
		final Paragraph.Text text = new Paragraph.Text("rate = %.0f%%", 50.23);
		assertEquals("rate = 50%", text.toJsonMap().getAttribute(Paragraph.Text.JSON_ATTRIBUTE_CONTENT));
	}

	@Test
	public void htmlWithParameters() {
		final Paragraph paragraph = new Paragraph("This is a paragraph with parameter");
		paragraph.setSource("/source");
		paragraph.addParameter("param1", "value1");
		paragraph.addParameter("param2", "value2");

		HTMLNode.PRETTY_PRINT = true;
		assertEquals(String.format("""
						<div class="tui-container-paragraph tui-refreshable-container">
						  <div class="fetch-error-message" style="display:none;"></div>
						  <div class="fetch-parameters">
						    <input type="hidden" name="param1" value="value1"/>
						    <input type="hidden" name="param2" value="value2"/>
						  </div>
						  <p id="%d" tui-source="/source" class="tui-align-left">
						    <span>This is a paragraph with parameter</span>
						  </p>
						</div>
						""", paragraph.getTUID()),
				paragraph.toHTMLNode().toHTML());
	}

	@Test
	public void htmlMultiLine() {
		final Paragraph paragraph = new Paragraph("""
				Multi-line
				content""");

		HTMLNode.PRETTY_PRINT = false;
		assertEquals("<p class=\"tui-align-left\"><span>Multi-line<br/>content</span></p>",
				paragraph.toHTMLNode().toHTML());
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Home", "/index");
		final Panel panel = new Panel();
		final RefreshButton refreshButton = panel.append(new RefreshButton("Refresh"));
		final Paragraph paragraph = panel.append(new Paragraph())
				.appendNormal("This paragraph contains ")
				.appendBold("strong")
				.appendNormal(" text.");
		paragraph.setSource("/paragraph");
		refreshButton.connectListener(paragraph);

		page.append(panel);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);
		backend.registerWebService(paragraph.getSource(), (uri, request, response) -> {
			final Color backgroundColor = new Color((int) (Math.random() * 250), (int) (Math.random() * 250), (int) (Math.random() * 250));
			final Paragraph result = new Paragraph()
					.appendNormal("Current time is")
					.append((layoutStyle, textStyle) -> {
						textStyle.setTextColor(TUIColors.computeContrastColor(backgroundColor));
						layoutStyle.setBackgroundColor(backgroundColor);
					}, " " + System.currentTimeMillis() + " ")
					.appendNormal("ms.");
			return result.toJsonMap();
		});

		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}