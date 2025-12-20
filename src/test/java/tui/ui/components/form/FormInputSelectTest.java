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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.RefreshButton;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class FormInputSelectTest extends TestWithBackend {

	@Test
	public void html() {
		final String inputName = "inputName";
		final Form form = new Form("Form title", "/form");
		final FormInputSelect select = form.createInputSelect("Label", inputName);
		select.addOption("opt1", "Option 1");
		select.addOption("opt2", "Option 2");

		TestUtils.assertHTMLProcedure(() -> form, (prefix, formElement) -> {
			final WebElement selectElement = formElement.findElement(By.tagName("select"));

			assertEquals(prefix, inputName, selectElement.getAttribute("name"));

			final List<WebElement> options = selectElement.findElements(By.tagName("option"));

			assertEquals(prefix, 2, options.size());
			assertEquals(prefix, "opt1", options.get(0).getAttribute("value"));
			assertEquals(prefix, "opt2", options.get(1).getAttribute("value"));
		});
	}

	@Test
	public void submit() {
		final String formTitle = "Form title";
		final String selectLabel = "Select your option";
		final String selectName = "selected_option";

		final TestUtils.UpdatablePage updatablePage = TestUtils.createPageWithUpdatablePanel();

		try(final Browser browser = startAndBrowse(updatablePage.page()).browser()) {
			final AtomicReference<RequestReader> referenceToReader = new AtomicReference<>();

			final LinkedHashMap<String, String> optionsMap = new LinkedHashMap<>();
			optionsMap.put("opt 1", "Option 1");
			optionsMap.put("opt 2", "Option 2");
			registerWebServiceToRefreshPanelWithForm(updatablePage.panel().getSource(), formTitle, selectLabel, selectName, optionsMap);
			registerWebServiceForFormSubmission(referenceToReader);

			// Refreshing the form (frontend javascript)
			browser.clickRefreshButton(updatablePage.button().getLabel()); // should fill the panel with the form given in json

			// Testing the form
			browser.selectFormOption(formTitle, selectName, "Option 2");
			browser.submitForm(formTitle);

			final RequestReader reader = referenceToReader.get();
			assertEquals("opt 2", reader.getStringParameter(selectName));
		}
	}

	private void registerWebServiceForFormSubmission(AtomicReference<RequestReader> referenceToReader) {
		registerWebService("/form", (uri, request, response) -> {
			referenceToReader.set(new RequestReader(request));
			return Form.buildSuccessfulSubmissionResponse();
		});
	}

	private void registerWebServiceToRefreshPanelWithForm(String panelSource, String formTitle,
			String selectLabel, String selectName,
			Map<String /* value */ , String /* label */> options) {
		registerWebService(panelSource, (uri, request, response) -> {
			final Panel result = new Panel(Panel.Align.CENTER);
			final Form form = result.append(new Form(formTitle, "/form"));
			final FormInputSelect select = form.createInputSelect(selectLabel, selectName);
			options.entrySet().forEach(entry -> select.addOption(entry.getKey(), entry.getValue()));
			return result.toJsonMap();
		});
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Select", "/index");
		final Panel panel = page.append(new Panel());
		panel.setSource("/panel");
		final Form form = panel.append(new Form("Form", "/form"));
		form.createInputSelect("Label", "inputName")
				.addOption("opt1", "Option 1")
				.addOption("opt2", "Option 2");

		page.append(new RefreshButton("Refresh"))
				.connectListener(panel);

		try(final TUIBackend backend = new TUIBackend(8080)) {
			backend.start();
			backend.registerPage(page);
			backend.registerWebService(panel.getSource(), (uri, request, response) -> panel.toJsonMap());

			try(final Browser browser = new Browser(backend.getPort())) {
				browser.open(page.getSource());

				browser.waitClosedManually();
			}
		}
	}
}