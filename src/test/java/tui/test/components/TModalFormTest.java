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

package tui.test.components;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.test.Browser;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.form.Form;
import tui.ui.components.form.ModalForm;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tui.test.components.TFormTest.checkField;

public class TModalFormTest extends TestWithBackend {

	private void buildPageAndStartBackend() {
		// UI: building the page
		final Page page = new Page("Home", "/index");
		final ModalForm form = new ModalForm("Test form", "Open form", "/form");
		form.createInputString("Name", "name");
		form.createInputNumber("Age", "age");
		page.append(form);

		// Backend
		startBackend(page);
	}

	@Test
	public void client() {
		buildPageAndStartBackend();

		final TClient tClient = startClient();

		tClient.open("/index");

		final TModalForm modalForm = tClient.getModalForm("Open form");
		assertFalse(modalForm.isOpened());
		modalForm.open();
		assertTrue(modalForm.isOpened());

		// Atomic references will store the values sent by the browser to the backend web service
		final AtomicReference<String> submittedName = new AtomicReference<>();
		final AtomicReference<Integer> submittedAge = new AtomicReference<>();
		registerWebService(submittedName, submittedAge);

		modalForm.enterInput("Name", "My name");
		modalForm.enterInput("Age", "42");
		modalForm.submit();

		assertEquals("My name", submittedName.get());
		assertEquals(42, submittedAge.get(), 0);

		modalForm.close();
		assertFalse(modalForm.isOpened());
	}

	@Test
	public void browse() {
		buildPageAndStartBackend();

		// Opening the page
		final Browser browser = startBrowser();
		browser.open("/index");

		browser.openModalForm("Open form");

		// Filling the form
		final Collection<WebElement> fields = browser.getModalFormFields("Open form");
		assertEquals(2, fields.size());
		checkField(fields, "Name", "text", "name");
		checkField(fields, "Age", "number", "age");

		// Atomic references will store the values sent by the browser to the backend web service
		final AtomicReference<String> submittedName = new AtomicReference<>();
		final AtomicReference<Integer> submittedAge = new AtomicReference<>();
		registerWebService(submittedName, submittedAge);

		browser.typeModalFormField("Open form", "Name", "My name");
		browser.typeModalFormField("Open form", "Age", "42");
		browser.submitModalForm("Open form");

		// Testing values received by the backend
		wait_s(0.1);
		assertEquals("My name", submittedName.get());
		assertEquals(42, submittedAge.get(), 0);

		browser.closeModalForm("Open form");
	}

	private void registerWebService(AtomicReference<String> submittedName, AtomicReference<Integer> submittedAge) {
		registerWebService("/form", (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final String name = reader.getStringParameter("name");
			final Integer age = reader.getIntegerParameter("age");
			submittedName.set(name);
			submittedAge.set(age);
			return Form.buildSuccessfulSubmissionResponse();
		});
	}

	public static String getOpenButtonLabel(WebElement modalFormElement) {
		final WebElement openButton = modalFormElement.findElement(By.tagName("button"));
		return openButton.getText();
	}

}