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
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;

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
	public void errorOnSubmit() {
		final Form form = new Form("Error will occur on submit", "/form");
		form.createInputString("Message", "message");
		final Page page = new Page("Error on submit", "/index");
		page.append(form);

		final Browser browser = startAndBrowse(page).browser();
		browser.typeField(form.getTitle(), "message", "entered value");
		browser.submit(form.getTitle());
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

		browser.selectRadio(form.getTitle(), inputRadio.getName(), "option2");
		browser.submit(form.getTitle());
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

		browser.typeField(form.getTitle(), "name", "UploadedName.txt");
		browser.selectFile(form.getTitle(), "file", fileToUpload);
		browser.submit(form.getTitle());
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

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Home", "/index");

		final Form form = new Form("Error will occur on submit", "/form");
		form.createInputString("Message", "message");
		page.append(form);

		final TUIBackend backend = new TUIBackend(getRandomAvailablePort());
		backend.registerPage(page);
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}