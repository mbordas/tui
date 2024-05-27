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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.form.Form;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TFormTest extends TestWithBackend {

	@Test
	public void browse() {
		final Page page = new Page("Home");
		final Form form = new Form("Test form", "/form");
		form.createInputString("Name", "name");
		form.createInputNumber("Age", "age");
		page.append(form);

		startBackend("/index", page);

		final Browser browser = startBrowser();
		browser.open("/index");

		browser.getTitle();

		final WebElement formElement = browser.getForms().get(0);

		assertTrue(formElement.isDisplayed());
		assertEquals("Test form", getTitle(formElement));

		final Collection<WebElement> fields = getFields(formElement);
		assertEquals(2, fields.size());

		checkField(fields, "Name", "text", "name");
		checkField(fields, "Age", "number", "age");
	}

	private static void checkField(Collection<WebElement> fields, String label, String type, String name) {
		final Optional<WebElement> anyFieldName = fields.stream()
				.filter((field) -> label.equals(getFieldLabel(field)))
				.findAny();

		assertTrue(label, anyFieldName.isPresent());
		final WebElement fieldName = anyFieldName.get();
		assertEquals(label, type, getFieldType(fieldName));
		assertEquals(label, name, getFieldName(fieldName));
	}

	static String getTitle(WebElement formElement) {
		final WebElement fieldset = formElement.findElement(By.tagName("fieldset"));
		final WebElement legend = fieldset.findElement(By.tagName("legend"));
		return legend.getText();
	}

	static Collection<WebElement> getFields(WebElement formElement) {
		return formElement.findElements(By.tagName("p"));
	}

	static String getFieldLabel(WebElement fieldElement) {
		final WebElement labelElement = fieldElement.findElement(By.tagName("label"));
		return labelElement.getText();
	}

	static String getFieldType(WebElement fieldElement) {
		final WebElement inputElement = fieldElement.findElement(By.tagName("input"));
		return inputElement.getAttribute("type");
	}

	static String getFieldName(WebElement fieldElement) {
		final WebElement inputElement = fieldElement.findElement(By.tagName("input"));
		return inputElement.getAttribute("name");
	}
}
