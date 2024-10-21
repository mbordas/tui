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

package tui.demo;

import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.UIRefreshableComponent;
import tui.ui.components.form.Form;
import tui.ui.components.form.ModalForm;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;

import java.util.Map;

public class Forms {

	private static void createModalForm(TUIBackend backend, VerticalFlow tabRegularForm) {
		final Form formModal = tabRegularForm.append(new ModalForm("Modal", "Open", "/forms/modal"));
		createInputs(formModal);
		registerWebService(backend, formModal);
	}

	private static void createRegularForm(TUIBackend backend, VerticalFlow tabRegularForm) {
		final Form formRegular = tabRegularForm.append(new Form("Regular", "/forms/regular"));
		createInputs(formRegular);
		registerWebService(backend, formRegular);
	}

	static class InfoSubmittedWithWizard {
		String string;
		String day;
		Boolean checkbox;
		Integer number;
	}

	static final InfoSubmittedWithWizard m_infoSubmittedWithWizard = new InfoSubmittedWithWizard();

	private static void createWizardForm(TUIBackend backend, VerticalFlow tabWizardForm, UIRefreshableComponent componentToRefresh) {
		final Form formWizard1 = tabWizardForm.append(new Form("Wizard 1/2", "/forms/wizard/1"));
		formWizard1.createInputString("String", "string");
		formWizard1.createInputDay("Day", "day");
		formWizard1.setSubmitLabel("Next");
		formWizard1.registerRefreshListener(componentToRefresh);

		// formWizard2 instance will not be part of the page, it used sent in json to the frontend, where formWizard1 will be updated
		final Form formWizard2 = new Form("Wizard 2/2", "/forms/wizard/2");
		formWizard2.createInputCheckbox("Checkbox", "checkbox");
		formWizard2.createInputNumber("Number", "number");
		formWizard2.setSubmitLabel("Validate");

		backend.registerWebService(formWizard1.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			m_infoSubmittedWithWizard.string = reader.getStringParameter("string");
			m_infoSubmittedWithWizard.day = reader.getStringParameter("day");
			return Form.getFormUpdateSubmissionResponse(formWizard2);
		});

		backend.registerWebService(formWizard2.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			m_infoSubmittedWithWizard.checkbox = reader.getCheckboxParameter("checkbox");
			m_infoSubmittedWithWizard.number = reader.getIntegerParameter("number");
			return Form.getSuccessfulSubmissionResponse();
		});
	}

	private static void registerWebService(TUIBackend backend, Form form) {
		backend.registerWebService(form.getTarget(), (uri, request, response) -> {
			try {
				Thread.sleep(1000);
				final RequestReader reader = new RequestReader(request);
				final String password = reader.getStringParameter("password");
				if(password == null || password.isEmpty()) {
					return Form.getFailedSubmissionResponse("Form rejected", Map.of("password", "Password can't be empty"));
				} else {
					return Form.getSuccessfulSubmissionResponse();
				}
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static void createInputs(Form form) {
		form.createInputCheckbox("Checkbox", "checkbox");
		form.createInputString("String", "string");
		form.createInputNumber("Number", "number");
		form.createInputDay("Day", "day");
		form.createInputDayHHmm("Day HH:mm", "dayHHmm");
		form.createInputPassword("Password *", "password");
		form.createInputRadio("Radio", "radio")
				.addOption("Option 1", "option1")
				.addOption("Option 2", "option2")
				.addOption("Option 3", "option3");
		form.createInputEmail("Email", "email");
		form.createInputFile("File", "file");
	}

	static String emptyWhenNull(String input) {
		return input == null ? "" : input;
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Forms");
		page.setSource("/index");

		final TabbedFlow tabbedFlow = page.append(new TabbedFlow());

		final UI ui = new UI();
		ui.setHTTPBackend("http://localhost", 8080);
		ui.add(page);
		final TUIBackend backend = new TUIBackend(ui);

		createRegularForm(backend, tabbedFlow.createTab("Regular form"));
		createModalForm(backend, tabbedFlow.createTab("Modal form"));

		final VerticalFlow wizardTab = tabbedFlow.createTab("Wizard form");
		final Paragraph wizardParagraph = wizardTab.append(new Paragraph("No information submitted yet."));
		wizardParagraph.setSource("/forms/wizard/paragraph");
		backend.registerWebService(wizardParagraph.getSource(), (uri, request, response) -> {
			wizardParagraph.clear();
			wizardParagraph.appendStrong("String: ").appendNormal(emptyWhenNull(m_infoSubmittedWithWizard.string));
			wizardParagraph.appendStrong("Day: ").appendNormal(emptyWhenNull(m_infoSubmittedWithWizard.day));
			wizardParagraph.appendStrong("Checkbox: ").appendNormal(emptyWhenNull(String.valueOf(m_infoSubmittedWithWizard.checkbox)));
			wizardParagraph.appendStrong("Number: ").appendNormal(emptyWhenNull(String.valueOf(m_infoSubmittedWithWizard.number)));
			return wizardParagraph.toJsonMap();
		});
		createWizardForm(backend, wizardTab, wizardParagraph);

		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open(page.getSource());
	}
}
