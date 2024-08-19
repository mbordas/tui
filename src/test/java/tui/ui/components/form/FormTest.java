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
import tui.html.HTMLNode;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.UI;
import tui.ui.components.Page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormTest extends TestWithBackend {

	@Test
	public void toHTML() {
		final Form form = new Form("test form", null);
		form.createInputString("string", "input1");
		form.createInputNumber("number", "input2");

		final HTMLNode html = form.toHTMLNode();

		HTMLNode.PRETTY_PRINT = true;
		assertEquals("""
				<form class="tui-form" action method="post" enctype="multipart/form-data">
				  <div class="fetch-error-message"> </div>
				  <fieldset>
				    <legend>test form</legend>
				    <p>
				      <label for="input1">string</label>
				      <input name="input1" placeholder="Text input" type="text"/>
				    </p>
				    <p>
				      <label for="input2">number</label>
				      <input name="input2" placeholder="Number" type="number"/>
				    </p>
				  </fieldset>
				  <button type="submit">Submit</button>
				</form>
				""", html.toHTML());
	}

	@Test
	public void errorOnSubmit() {
		final Form form = new Form("Error will occur on submit", "/form");
		form.createInputString("Message", "message");
		final Page page = new Page("Error on submit");
		page.append(form);
		final Browser browser = startAndBrowse(page);
		browser.typeField(form.getTitle(), "message", "entered value");
		browser.submit(form.getTitle());
		wait_s(0.1);

		final WebElement formElement = browser.getForm(form.getTitle());

		assertTrue(browser.isOnError(formElement));
		assertEquals("Error: HTTP error, status = 500", browser.getErrorMessage(formElement));
	}

	public static void main(String[] args) throws Exception {
		final UI ui = new UI();
		final Page page = new Page("Home");

		final Form form = new Form("Error will occur on submit", "/form");
		form.createInputString("Message", "message");
		page.append(form);

		final TUIBackend backend = new TUIBackend(ui);
		ui.add("/index", page);
		ui.setHTTPBackend("localhost", getRandomAvailablePort());
		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}