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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import tui.test.Browser;
import tui.utils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchTestHTML {

	@Test
	public void nominal() {
		final Search search = new Search("Search title", "Submit label", "inputName");
		search.createInputString("String label", "inputString");
		search.addParameter("parameterName", "Parameter Value");

		TestUtils.assertHTMLProcedure(() -> search,
				(prefix, searchElement) -> {
					assertEquals("search", searchElement.getTagName());
					assertTrue(Browser.getClasses(searchElement).contains(Search.HTML_CLASS));

					final WebElement titleElement = searchElement.findElement(By.tagName("label"));
					assertEquals("Search title", titleElement.getText());

					final WebElement inputElement = Browser.getInputChildByName(searchElement, "inputString");
					assertEquals("text", inputElement.getAttribute("type"));
					final WebElement inputLabelElement = Browser.getParent(inputElement).findElement(By.tagName("label"));
					assertEquals("String label", inputLabelElement.getText());
					assertEquals(inputElement.getAttribute("id"), inputLabelElement.getAttribute("for"));

					final WebElement parameter = Browser.getFormParameterByName(searchElement, "parameterName");
					assertEquals("Parameter Value", parameter.getAttribute("value"));
					assertEquals("hidden", parameter.getAttribute("type"));
				});
	}

	@Test
	public void checkedBox() {
		final Search search = new Search("Search title", "Submit");
		search.createInputCheckbox("Checkbox ON", "checkboxON").check();
		search.createInputCheckbox("Checkbox OFF", "checkboxOFF");

		TestUtils.assertHTMLProcedure(() -> search,
				(prefix, searchElement) -> {

					final WebElement checkboxOnElement = Browser.getInputChildByName(searchElement, "checkboxON");
					assertEquals("checkbox", checkboxOnElement.getAttribute("type"));
					assertTrue(checkboxOnElement.isSelected());

					final WebElement checkboxOffElement = Browser.getInputChildByName(searchElement, "checkboxOFF");
					assertEquals("checkbox", checkboxOffElement.getAttribute("type"));
					assertFalse(checkboxOffElement.isSelected());
				});
	}

	@Test
	public void displayLikeForm() {
		final Search search = new Search("Search title", "Submit");
		search.createInputDayHHmm("Day", "day");
		search.createInputRadio("Radio", "radio")
				.addOption("Option 1", "opt1")
				.addOption("Option 2", "opt2")
				.addOption("Option 3", "opt3");
		search.createInputNumber("Number", "number");
		search.displayLikeForm(2);

		TestUtils.assertHTMLProcedure(() -> search,
				(prefix, componentRootElement) -> {

					final WebElement tableElement = componentRootElement.findElement(By.tagName("table"));
					final List<WebElement> rowElements = tableElement.findElements(By.tagName("tr"));

					assertEquals(2, rowElements.size());
					final List<WebElement> cellsOfRow1 = rowElements.get(0).findElements(By.tagName("td"));
					final WebElement dayInputElement = Browser.getInputChildByName(cellsOfRow1.get(0), "day");
					TestUtils.assertClasses(List.of("tui-form-input"), Browser.getParent(dayInputElement));
				});
	}

}