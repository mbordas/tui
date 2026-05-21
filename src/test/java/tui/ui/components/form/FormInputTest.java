/*
 * Copyright (c) 2012-2026 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui.components.form;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.utils.TestUtils;

import static org.junit.Assert.assertEquals;

public class FormInputTest {

	@Test
	public void defaultHintShouldBeTheLabel() {
		final String inputName = "name";
		final Form form = new Form("Title", "/form");
		form.createInputNumber("Number label", inputName);

		TestUtils.assertHTMLProcedure(() -> form, (prefix, formElement) -> {
			final WebElement numberElement = formElement.findElement(By.tagName("input"));

			assertEquals(prefix, inputName, numberElement.getAttribute("name"));
			assertEquals(prefix, "Number label", numberElement.getAttribute("title"));
		});
	}

	@Test
	public void customizedHint() {
		final String inputName = "name";
		final Form form = new Form("Title", "/form");
		form.createInputString("String label", inputName)
				.setHint("Custom hint");

		TestUtils.assertHTMLProcedure(() -> form, (prefix, formElement) -> {
			final WebElement numberElement = formElement.findElement(By.tagName("input"));

			assertEquals(prefix, inputName, numberElement.getAttribute("name"));
			assertEquals(prefix, "Custom hint", numberElement.getAttribute("title"));
		});
	}
}
