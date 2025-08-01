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

package tui.ui.components.form;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormTest extends TestWithBackend {

	@Test
	public void refresh() {
		final String formTitle = "Form title";
		final String inputStringName = "string";

		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();

		try(final Browser browser = startAndBrowse(updatablePage.page()).browser()) {
			final AtomicReference<RequestReader> referenceToReader = new AtomicReference<>();

			registerWebServiceToRefreshPanelWithForm(updatablePage.panel().getSource(), formTitle, inputStringName);
			registerWebServiceForFormSubmission(referenceToReader);

			// Refreshing the form (frontend javascript)
			browser.clickRefreshButton(updatablePage.button().getLabel()); // should fill the panel with the form given in json

			// Testing the form
			browser.typeFormField(formTitle, inputStringName, "my string");
			browser.submitForm(formTitle);

			final RequestReader reader = referenceToReader.get();
			assertEquals("my string", reader.getStringParameter(inputStringName));
		}
	}

	private void registerWebServiceForFormSubmission(AtomicReference<RequestReader> referenceToReader) {
		registerWebService("/form", (uri, request, response) -> {
			referenceToReader.set(new RequestReader(request));
			return Form.buildSuccessfulSubmissionResponse();
		});
	}

	private void registerWebServiceToRefreshPanelWithForm(String panelSource, String formTitle, String inputStringName) {
		registerWebService(panelSource, (uri, request, response) -> {
			final Panel result = new Panel(Panel.Align.CENTER);
			final Form form = result.append(new Form(formTitle, "/form"));
			form.createInputString("String", inputStringName);
			return result.toJsonMap();
		});
	}

	@Test
	public void usesSessionParameters() {
		final Page page = new Page("FormTest", "/index");
		page.setSessionParameter("sessionId", "mysessionid");
		page.append(new Form("Test form", "/form"));

		final Browser browser = startAndBrowse(page).browser();

		final AtomicReference<RequestReader> reader = new AtomicReference<>();
		registerWebServiceForFormSubmission(reader);

		browser.submitForm("Test form");

		assertEquals("mysessionid", reader.get().getStringParameter("sessionId"));
	}

	@Test
	public void update() {
		final Page page = new Page("Form update", "/index");
		final Form form1 = page.append(new Form("Update 1/2", "/form1"));
		form1.createInputString("login", "login");

		final Form form2 = new Form("Update 2/2", "/form2");
		form2.createInputEmail("email", "email");

		final AtomicReference<String> param1 = new AtomicReference<>();
		final AtomicReference<String> param2 = new AtomicReference<>();
		registerWebService(form1.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			param1.set(reader.getStringParameter("login"));
			return Form.buildFormUpdateSubmissionResponse(form2);
		});
		registerWebService(form2.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			param2.set(reader.getStringParameter("email"));
			return Form.buildSuccessfulSubmissionResponse();
		});

		final Browser browser = startAndBrowse(page).browser();
		browser.typeFormField(form1.getTitle(), "login", "test login");
		browser.submitForm(form1.getTitle());
		wait_s(0.1);

		browser.typeFormField(form2.getTitle(), "email", "test@email.fr");
		browser.submitForm(form2.getTitle());
		wait_s(0.1);

		assertEquals("test login", param1.get());
		assertEquals("test@email.fr", param2.get());
	}

	@Test
	public void errorOnSubmit() {
		final Form form = new Form("Error will occur on submit", "/form");
		form.createInputString("Message", "message");
		final Page page = new Page("Error on submit", "/index");
		page.append(form);

		final Browser browser = startAndBrowse(page).browser();
		browser.typeFormField(form.getTitle(), "message", "entered value");
		browser.submitForm(form.getTitle());
		wait_s(0.1);

		final WebElement formElement = browser.getForm(form.getTitle());

		assertTrue(browser.isOnError(formElement));
		assertEquals("Error: HTTP error, status = 500", browser.getErrorMessage(formElement));
	}

	@Test
	public void supportForRadio() {
		final Form form = new Form("Select radio option", "/form/radio");
		final FormInputRadio inputRadio = form.createInputRadio("Options", "radio");
		inputRadio.addOption("First option", "option1");
		inputRadio.addOption("Second option", "option2");
		inputRadio.addOption("Third option", "option3");
		final Page page = new Page("supportForRadio", "/index");
		page.append(form);

		final Browser browser = startAndBrowse(page).browser();

		final AtomicReference<String> valueSentToBackend = new AtomicReference<>();
		m_backend.registerWebService(form.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			valueSentToBackend.set(reader.getStringParameter(inputRadio.getName()));
			return Form.buildSuccessfulSubmissionResponse();
		});

		browser.selectFormRadio(form.getTitle(), inputRadio.getName(), "option2");
		browser.submitForm(form.getTitle());
		wait_s(0.1);

		assertEquals("option2", valueSentToBackend.get());
	}

	@Test
	public void uploadFile() {
		final Form form = new Form("Error will occur on submit", "/form");
		form.createInputString("Name", "name");
		form.createInputFile("File", "file");
		final Page page = new Page("Upload", "/upload");
		page.append(form);

		final File fileToUpload = new File("target/test-classes/form/file_to_upload.txt");
		final File folderWhereToUpload = new File("target/test-classes/temp");
		folderWhereToUpload.mkdir();

		final Browser browser = startAndBrowse(page).browser();

		registerWebService(form.getTarget(), (uri, request, response) -> {
			System.out.println("URI = " + uri);
			final RequestReader reader = new RequestReader(request);

			final String newName = reader.getStringParameter("name");
			final InputStream inputStream = reader.getFileInputStream("file");

			File uploadedFile = new File(folderWhereToUpload, newName);
			try(FileOutputStream fos = new FileOutputStream(uploadedFile)) {
				fos.write(inputStream.readAllBytes());
			}

			return Form.buildSuccessfulSubmissionResponse();
		});

		browser.typeFormField(form.getTitle(), "name", "UploadedName.txt");
		browser.selectFormFile(form.getTitle(), "file", fileToUpload);
		browser.submitForm(form.getTitle());
		wait_s(1);

		File expectedUploadedFile = new File(folderWhereToUpload, "UploadedName.txt");
		assertTrue(expectedUploadedFile.exists());

		String testedContent;
		try(FileInputStream fis = new FileInputStream(expectedUploadedFile)) {
			testedContent = new String(fis.readAllBytes());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		assertEquals("Uploaded content.", testedContent);
	}

}