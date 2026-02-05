/* Copyright (c) 2026, Mathieu Bordas
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Paragraph;
import tui.utils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ModalPanelTest extends TestWithBackend {

	@Test
	public void htmlOfPanelWithoutSource() {
		final ModalPanel panel = new ModalPanel(Panel.Align.VERTICAL_TOP, "Open modal panel");
		panel.append(new Paragraph.Text("First text"));
		panel.append(new Paragraph.Text("Second text"));

		TestUtils.assertHTMLProcedure(() -> panel, (prefix, modalPanelElement) -> {

			assertEquals(prefix, "div", modalPanelElement.getTagName());

			// open button
			final WebElement openButtonElement = modalPanelElement.findElement(By.tagName("button"));
			assertEquals(prefix, "Open modal panel", openButtonElement.getText());
			assertTrue(prefix, Browser.getClasses(openButtonElement).contains(ModalPanel.HTML_CLASS_MODAL_PANEL_OPEN_BUTTON));

			// dialog
			final WebElement dialogElement = modalPanelElement.findElement(By.tagName("dialog"));
			assertTrue(prefix, Browser.getClasses(dialogElement).contains("modal"));

			// dialog div
			final WebElement flowElement = Browser.getFirstMatchingChild(dialogElement,
					(element) -> element.getTagName().equals("div")
							&& Browser.getClasses(element).contains(Panel.Align.VERTICAL_TOP.getHTMLClass()));
			assertNotNull(flowElement);

			// components
			final List<WebElement> paragraphElements = flowElement.findElements(By.tagName(Paragraph.Text.HTML_TAG));
			assertEquals(2, paragraphElements.size());

			// footer
			final WebElement dialogFooterElement = dialogElement.findElement(By.className(Panel.Align.RIGHT.getHTMLClass()));
			assertTrue(prefix, Browser.getClasses(dialogFooterElement).contains(ModalPanel.HTML_CLASS_FOOTER));
		});
	}

}