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
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.utils.TestUtils;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class FormTestHTML {

	@Test
	public void initialValue() {
		final Form form = new Form("Form title", "/form");
		form.createInputDayHHmm("Day", "day")
				.setInitialValue(LocalDateTime.of(2025, Month.SEPTEMBER, 3, 11, 24));

		TestUtils.assertHTMLProcedure(() -> form, (prefix, formElement) -> {
			final WebElement dayInputElement = Browser.getInputChildByName(formElement, "day");

			assertEquals(prefix, "2025-09-03T11:24", dayInputElement.getAttribute("value"));
		});
	}

	@Test
	public void placeholder() {
		final Form form = new Form("Form title", "/form");
		form.createInputString("First name", "first_name")
				.setPlaceHolder("ex: John");

		TestUtils.assertHTMLProcedure(() -> form, (prefix, formElement) -> {
			final WebElement stringInputElement = Browser.getInputChildByName(formElement, "first_name");

			assertEquals(prefix, "ex: John", stringInputElement.getAttribute("placeholder"));
		});
	}

	@Test
	public void supportForTextArea() {
		final Form form = new Form("Form title", "/form");
		form.createInputTextArea("Text area", "text")
				.setInitialValue("Initial value");

		TestUtils.assertHTMLProcedure(() -> form, (prefix, formElement) -> {
			final WebElement textareaElement = Browser.getInputChildByName(formElement, "text");

			assertEquals("textarea", textareaElement.getTagName());
			assertEquals("text", textareaElement.getAttribute("name"));
			assertEquals("Initial value", textareaElement.getText());
		});
	}
}
