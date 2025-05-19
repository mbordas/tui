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
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;

import static org.junit.Assert.assertEquals;

public class ModalFormTest extends TestWithBackend {

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
		browser.submitForm(form.getTitle());

		assertEquals(page2.getTitle(), browser.getTitle());
	}
}