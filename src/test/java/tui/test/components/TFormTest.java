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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.test.Browser;
import tui.test.TestExecutionException;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.form.Form;
import tui.ui.components.form.Search;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TFormTest extends TestWithBackend {

	@Test
	public void browse() {

		// UI: building the page
		final Page page = new Page("Home", "/index");
		final Form form = new Form("Test form", "/form");
		form.createInputString("Name", "name");
		form.createInputNumber("Age", "age");
		page.append(form);

		// Backend
		startBackend(page);
		// Atomic references will store the values sent by the browser to the backend web service
		final AtomicReference<String> submittedName = new AtomicReference<>();
		final AtomicReference<Integer> submittedAge = new AtomicReference<>();
		registerWebService("/form", (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String name = reader.getStringParameter("name");
			final Integer age = reader.getIntegerParameter("age");
			submittedName.set(name);
			submittedAge.set(age);
			return Form.buildSuccessfulSubmissionResponse();
		});

		// Opening the page
		final Browser browser = startBrowser();
		browser.open("/index");

		// Filling the form
		final Collection<WebElement> fields = browser.getFormFields("Test form");
		assertEquals(2, fields.size());
		checkField(fields, "Name", "text", "name");
		checkField(fields, "Age", "number", "age");

		browser.typeFormField("Test form", "Name", "My name");
		browser.typeFormField("Test form", "Age", "42");
		browser.submitForm("Test form");

		// Testing values received by the backend
		wait_s(0.1);
		assertEquals("My name", submittedName.get());
		assertEquals(42, submittedAge.get(), 0);
	}

	static void checkField(Collection<WebElement> fields, String label, String type, String name) {
		final Optional<WebElement> anyFieldName = fields.stream()
				.filter((field) -> label.equals(getFieldLabel(field)))
				.findAny();

		assertTrue(label, anyFieldName.isPresent());
		final WebElement fieldName = anyFieldName.get();
		assertEquals(label, type, getFieldType(fieldName));
		assertEquals(label, name, getFieldName(fieldName));
	}

	public static String getTitle(WebElement formElement) {
		final WebElement fieldset = formElement.findElement(By.tagName("fieldset"));
		final WebElement legend = fieldset.findElement(By.tagName("legend"));
		return legend.getText();
	}

	public static Collection<WebElement> getFields(WebElement formElement) {
		final List<WebElement> result = formElement.findElements(By.className(Form.HTML_CLASS_FIELD));
		result.addAll(formElement.findElements(By.className(Search.HTML_CLASS_FIELD)));
		return result;
	}

	public static String getFieldLabel(WebElement fieldElement) {
		final WebElement labelElement = fieldElement.findElement(By.tagName("label"));
		return labelElement.getText();
	}

	static String getFieldType(WebElement fieldElement) {
		final WebElement inputElement = getInputElementsOfFieldElement(fieldElement).get(0);
		return inputElement.getAttribute("type");
	}

	public static String getFieldName(WebElement fieldElement) {
		final WebElement inputElement = getInputElementsOfFieldElement(fieldElement).get(0);
		return inputElement.getAttribute("name");
	}

	/**
	 * Caution: a radio field element may contain multiple input elements.
	 */
	public static @NotNull List<WebElement> getInputElementsOfFieldElement(WebElement fieldElement) {
		final List<WebElement> foundElements = fieldElement.findElements(By.xpath(".//*")).stream()
				.filter((element) -> element.getTagName().equals("input")
						|| element.getTagName().equals("select")
						|| element.getTagName().equals("textarea"))
				.toList();
		if(foundElements.isEmpty()) {
			throw new TestExecutionException("No input/textarea tag found");
		}
		return foundElements;
	}
}
