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

package tui.ui.components.form;

import org.junit.Test;
import tui.http.RequestReader;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class ModalFormTest extends TestWithBackend {

	@Test
	public void refresh() {
		final String openButtonLabel = "Open form";
		final String formTitle = "Form title";
		final String inputStringLabel = "String";
		final String inputStringName = "string";
		final String hiddenParameterName = "hiddenParameter";
		final String hiddenParameterValue = "hidden value";

		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();

		try(final Browser browser = startAndBrowse(updatablePage.page()).browser()) {

			registerWebService(updatablePage.panel().getSource(), (uri, request, response) -> {
				final Panel panel = new Panel();
				final ModalForm form = new ModalForm(formTitle, openButtonLabel, "/form");
				form.createInputString(inputStringLabel, inputStringName);
				form.addParameter(hiddenParameterName, hiddenParameterValue);
				panel.append(form);
				return panel.toJsonMap();
			});
			final AtomicReference<RequestReader> referenceToReader = new AtomicReference<>();
			registerWebService("/form", (uri, request, response) -> {
				referenceToReader.set(new RequestReader(request));
				return Form.buildSuccessfulSubmissionResponse();
			});

			browser.clickRefreshButton(updatablePage.button().getLabel());
			// panel then should contain the ModalForm
			browser.openModalForm(openButtonLabel);
			browser.typeModalFormField(openButtonLabel, inputStringLabel, "my string");
			browser.submitModalForm(openButtonLabel);

			final RequestReader reader = referenceToReader.get();
			assertEquals("my string", reader.getStringParameter(inputStringName));
			assertEquals(hiddenParameterValue, reader.getStringParameter(hiddenParameterName));
		}
	}

	@Test
	public void opensPage() {
		final Page page2 = new Page("Page 2", "/page2");
		page2.append(new Paragraph.Text("Welcome to page 2."));

		final Page page1 = new Page("Page 1", "/page1");
		final ModalForm form = page1.append(new ModalForm("Going to page 2", "open form", "/form"));
		form.opensPage(page2.getSource());

		final BackendAndBrowser backendAndBrowser = startAndBrowse(page1);
		backendAndBrowser.backend().registerPage(page2);
		backendAndBrowser.backend().registerWebService(form.getTarget(),
				(uri, request, response) -> Form.buildSuccessfulSubmissionResponse());

		final Browser browser = backendAndBrowser.browser();
		browser.openModalForm(form.getOpenButtonLabel());
		browser.submitModalForm(form.getOpenButtonLabel());

		assertEquals(page2.getTitle(), browser.getTitle());
	}
}