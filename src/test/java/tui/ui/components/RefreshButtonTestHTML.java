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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.utils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RefreshButtonTestHTML {

	@Test
	public void html() {
		final RefreshButton button = new RefreshButton("Button label");
		button.setParameter("param1", "value1");

		TestUtils.assertHTMLProcedure(() -> button,
				(prefix, componentRootElement) -> {
					// div
					assertEquals(prefix, "div", componentRootElement.getTagName());
					assertTrue(prefix, Browser.getClasses(componentRootElement).contains(RefreshButton.HTML_CLASS_CONTAINER));

					// input
					final List<WebElement> inputElements = componentRootElement.findElements(By.tagName("input"));
					assertEquals(prefix, 1, inputElements.size());
					final WebElement inputElement = inputElements.get(0);
					assertEquals(prefix, "hidden", inputElement.getAttribute("type"));
					assertEquals(prefix, "param1", inputElement.getAttribute("name"));
					assertEquals(prefix, "value1", inputElement.getAttribute("value"));

					// button
					final WebElement buttonElement = componentRootElement.findElement(By.tagName("button"));
					assertTrue(prefix, Browser.getClasses(buttonElement).contains(RefreshButton.HTML_CLASS));
					assertEquals(prefix, "button", buttonElement.getAttribute("type"));
					assertEquals(prefix, "Button label", buttonElement.getText());
				});
	}
}
