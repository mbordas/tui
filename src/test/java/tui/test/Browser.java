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

package tui.test;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.HTMLFetchErrorMessage;
import tui.test.components.TFormTest;
import tui.test.components.TSearchTest;
import tui.test.components.TSectionTest;
import tui.test.components.TTableTest;
import tui.ui.components.NavButton;
import tui.ui.components.NavLink;
import tui.ui.components.RefreshButton;
import tui.ui.components.Table;
import tui.ui.components.form.Form;
import tui.ui.components.form.ModalForm;
import tui.ui.components.form.Search;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;
import tui.ui.components.layout.VerticalScroll;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;

import static tui.test.components.TFormTest.getFieldName;

public class Browser implements Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(Browser.class);

	private final FirefoxDriver m_driver;
	private final String m_host;

	public Browser(int port) {
		m_host = String.format("http://localhost:%d", port);
		m_driver = new FirefoxDriver();
	}

	public void open(String target) {
		if(target.startsWith("/")) {
			m_driver.get(m_host + target);
		} else {
			m_driver.get(m_host + "/" + target);
		}
	}

	public String getTitle() {
		return m_driver.getTitle();
	}

	public void close() {
		m_driver.quit();
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void waitClosedManually() {
		while(true) {
			try {
				m_driver.getTitle();
				Thread.sleep(500);
			} catch(Throwable t) {
				break;
			}
		}
	}

	// SECTIONS

	public List<WebElement> getSections() {
		return m_driver.findElements(By.tagName("section"));
	}

	public WebElement getSection(String title) {
		final Optional<WebElement> anySectionElement = getSections().stream()
				.filter(WebElement::isDisplayed)
				.filter((element) -> title.equals(TSectionTest.getTitle(element)))
				.findAny();

		if(anySectionElement.isPresent()) {
			return anySectionElement.get();
		} else {
			throw new RuntimeException("Section element not found: " + title);
		}
	}

	// PARAGRAPHS

	public List<WebElement> getParagraphs() {
		return m_driver.findElements(By.tagName("p"));
	}

	public List<WebElement> getParagraphsWithText() {
		return getParagraphs().stream()
				.filter((paragraph) -> !paragraph.getText().isEmpty())
				.toList();
	}

	// NAVLINKS

	public List<WebElement> getNavLinks() {
		return m_driver.findElements(By.className(NavLink.HTML_CLASS));
	}

	// NAVBUTTONS

	public List<WebElement> getNavButtons() {
		return m_driver.findElements(By.className(NavButton.HTML_CLASS));
	}

	// REFRESH BUTTONS

	public void clickRefreshButton(String label) {
		final WebElement button = getRefreshButton(label);
		button.click();
	}

	public List<WebElement> getRefreshButtons() {
		return m_driver.findElements(By.className(RefreshButton.HTML_CLASS));
	}

	public WebElement getRefreshButton(String label) {
		final Optional<WebElement> anyButton = getRefreshButtons().stream()
				.filter((element) -> label.equals(element.getText()))
				.findAny();
		if(anyButton.isPresent()) {
			return anyButton.get();
		} else {
			throw new RuntimeException("RefreshButton element not found with label: " + label);
		}
	}

	// TABLES

	public WebElement getTable(String title) {
		final Optional<WebElement> anyTableElement = getTables().stream()
				.filter(WebElement::isDisplayed)
				.filter((element) -> title.equals(TTableTest.getTitle(element)))
				.findAny();

		if(anyTableElement.isPresent()) {
			return anyTableElement.get();
		} else {
			throw new RuntimeException("Table element not found: " + title);
		}
	}

	private WebElement getTableNavigation(String title) {
		final WebElement tableElement = getTable(title);
		final WebElement tableContainer = getParent(tableElement);
		return tableContainer.findElement(By.className(Table.HTML_CLASS_NAVIGATION));
	}

	public WebElement getTableNavigationButtonPrevious(String tableTitle) {
		return getTableNavigationButton(tableTitle, "<");
	}

	public WebElement getTableNavigationButtonNext(String tableTitle) {
		return getTableNavigationButton(tableTitle, ">");
	}

	public String getTableNavigationLocationText(String tableTitle) {
		final WebElement tableNavigation = getTableNavigation(tableTitle);
		final WebElement span = tableNavigation.findElement(By.tagName("span"));
		return span.getText();
	}

	private WebElement getTableNavigationButton(String tableTitle, String label) {
		final WebElement tableNavigation = getTableNavigation(tableTitle);
		final Optional<WebElement> anyButton = tableNavigation.findElements(By.tagName("button")).stream()
				.filter((button) -> button.getText().equals(label))
				.findAny();
		if(anyButton.isEmpty()) {
			throw new NullPointerException(String.format("Table navigation button '%s' not found.", label));
		} else {
			return anyButton.get();
		}
	}

	public List<WebElement> getTables() {
		return m_driver.findElements(By.tagName("table"));
	}

	@Nullable
	public WebElement getTableCellByText(WebElement table, Predicate<String> conditionOnText) {
		final List<WebElement> cells = table.findElements(By.tagName("td"));
		final Optional<WebElement> anyCell = cells.stream()
				.filter((cell) -> conditionOnText.test(cell.getText()))
				.findAny();
		return anyCell.orElse(null);
	}

	//
	// SVG
	//

	public List<WebElement> getSVGs() {
		return m_driver.findElements(By.tagName("svg"));
	}

	//
	// PANELS
	//

	public List<WebElement> getPanels() {
		return m_driver.findElements(By.className("tui-panel"));
	}

	// IMAGES

	public List<WebElement> getImages() {
		return m_driver.findElements(By.tagName("img"));
	}

	// SEARCH

	public List<WebElement> getSearches() {
		return m_driver.findElements(By.className(Search.HTML_CLASS));
	}

	public WebElement getSearch(String title) {
		final Optional<WebElement> anySearchElement = getSearches().stream()
				.filter(WebElement::isDisplayed)
				.filter((element) -> title.equals(TSearchTest.getTitle(element)))
				.findAny();

		if(anySearchElement.isPresent()) {
			return anySearchElement.get();
		} else {
			throw new RuntimeException("Search element not found: " + title);
		}
	}

	public Collection<WebElement> getSearchFields(String searchTitle) {
		final WebElement searchElement = getSearch(searchTitle);
		return TSearchTest.getFields(searchElement);
	}

	public void typeSearch(@NotNull String title, @NotNull String parameterName, String value) {
		final WebElement searchElement = getSearch(title);
		for(WebElement inputElement : searchElement.findElements(By.tagName("input"))) {
			if(parameterName.equals(inputElement.getAttribute("name"))) {
				inputElement.sendKeys(value);
				return;
			}
		}
		throw new RuntimeException("Search input element not found: " + parameterName);
	}

	public void selectSearchRadio(String searchTitle, String name, String option) {
		final Optional<WebElement> anyInput = getSearchFields(searchTitle).stream()
				.filter((inputElement) ->
						name.equals(inputElement.getAttribute("name"))
								&& inputElement.getAttribute("type").equals("radio")
								&& inputElement.getAttribute("value").equals(option))
				.findAny();

		if(anyInput.isEmpty()) {
			throw new RuntimeException(String.format("Option '%s' not found in radio '%s'", option, name));
		} else {
			anyInput.get().click();
		}
	}

	public void submitSearch(@NotNull String title) {
		final WebElement searchElement = getSearch(title);
		final Optional<WebElement> anyButton = searchElement.findElements(By.tagName("button")).stream()
				.findAny();
		if(anyButton.isPresent()) {
			anyButton.get().click();
		} else {
			throw new RuntimeException("Submit button not found.");
		}
	}

	// FORMS

	public void openModalForm(String openButtonLabel) {
		final Optional<WebElement> anyOpenFormButton = m_driver.findElements(By.className("tui-modal-form-open-button")).stream()
				.filter((element) -> element.getText().equals(openButtonLabel))
				.findAny();
		if(anyOpenFormButton.isPresent()) {
			anyOpenFormButton.get().click();
		} else {
			throw new RuntimeException("ModalForm open button not found: " + openButtonLabel);
		}
	}

	public WebElement getForm(String title) {
		final Optional<WebElement> anyFormElement = getForms().stream()
				.filter(WebElement::isDisplayed)
				.filter((element) -> title.equals(TFormTest.getTitle(element)))
				.findAny();

		if(anyFormElement.isPresent()) {
			return anyFormElement.get();
		} else {
			throw new RuntimeException("Form element not found: " + title);
		}
	}

	public List<WebElement> getForms() {
		final List<WebElement> result = new ArrayList<>();
		result.addAll(m_driver.findElements(By.className(Form.HTML_CLASS)));
		result.addAll(m_driver.findElements(By.className(ModalForm.HTML_CLASS)));
		return result;
	}

	public Collection<WebElement> getFormFields(String formTitle) {
		final WebElement formElement = getForm(formTitle);
		return TFormTest.getFields(formElement);
	}

	public void typeFormField(String formTitle, String name, String value) {
		final Optional<WebElement> anyFieldName = getFormFields(formTitle).stream()
				.filter((field) -> name.equals(getFieldName(field)))
				.findAny();

		if(anyFieldName.isPresent()) {
			WebElement inputElement = anyFieldName.get().findElement(By.tagName("input"));
			inputElement.sendKeys(value);
		} else {
			throw new RuntimeException("Field input element not found: " + name);
		}
	}

	public void selectFormRadio(String formTitle, String name, String option) {
		final Optional<WebElement> anyFieldName = getFormFields(formTitle).stream()
				.filter((field) -> name.equals(getFieldName(field)))
				.findAny();

		if(anyFieldName.isPresent()) {
			final Optional<WebElement> anyInput = anyFieldName.get().findElements(By.tagName("input")).stream()
					.filter((input) -> input.getAttribute("value").equals(option))
					.findAny();
			if(anyInput.isEmpty()) {
				throw new RuntimeException(String.format("Option '%s' not found in radio '%s'", option, name));
			} else {
				anyInput.get().click();
			}
		} else {
			throw new RuntimeException("Field input element not found: " + name);
		}
	}

	public void selectFormFile(String formTitle, String name, File localFile) {
		final Optional<WebElement> anyFieldName = getFormFields(formTitle).stream()
				.filter((field) -> name.equals(getFieldName(field)))
				.findAny();

		if(anyFieldName.isPresent()) {
			WebElement inputElement = anyFieldName.get().findElement(By.tagName("input"));
			inputElement.sendKeys(localFile.getAbsolutePath());
		} else {
			throw new RuntimeException("Field input element not found: " + name);
		}
	}

	public void submitForm(String formTitle) {
		final WebElement formElement = getForm(formTitle);
		final Optional<WebElement> anySubmitButton = formElement.findElements(By.tagName("button")).stream()
				.filter((button) -> "submit".equals(button.getAttribute("type")))
				.filter((button) -> !getClasses(button).contains("tui-modal-form-open-button"))
				.findAny();
		if(anySubmitButton.isPresent()) {
			anySubmitButton.get().click();
		} else {
			throw new RuntimeException("Submit button not found.");
		}
	}

	// LAYOUTS

	// VerticalFlow

	public List<WebElement> getVerticalFlows() {
		return m_driver.findElements(By.className(VerticalFlow.HTML_CLASS));
	}

	// VerticalScroll

	public List<WebElement> getVerticalScrolls() {
		return m_driver.findElements(By.className(VerticalScroll.HTML_CLASS));
	}

	// TabbedFlow

	public List<WebElement> getTabbedFlows() {
		return m_driver.findElements(By.className(TabbedFlow.HTML_CLASS));
	}

	// ERRORS

	public boolean isOnError(WebElement element) {
		final WebElement containerElement = getParent(element);
		final WebElement errorElement = containerElement.findElement(By.className(HTMLFetchErrorMessage.HTML_CLASS_ERROR_ELEMENT));
		return errorElement.isDisplayed();
	}

	public String getErrorMessage(WebElement element) {
		final WebElement containerElement = getParent(element);
		final WebElement errorElement = containerElement.findElement(By.className(HTMLFetchErrorMessage.HTML_CLASS_ERROR_ELEMENT));
		return errorElement.getText();
	}

	// UTILS

	public static WebElement getParent(WebElement element) {
		return element.findElement(By.xpath("parent::*"));
	}

	public static WebElement getUniqueChild(WebElement element) {
		final List<WebElement> childrenElements = element.findElements(By.xpath("./*"));
		if(childrenElements.size() != 1) {
			LOG.error("{} children found", childrenElements.size());
			for(WebElement childElement : childrenElements) {
				LOG.debug("Child of type: {}", childElement.getTagName());
			}
			throw new TestExecutionException("Child is not unique");
		}
		return childrenElements.get(0);
	}

	public static WebElement getInputChildByName(WebElement searchElement, String inputName) {
		final List<WebElement> inputElements = searchElement.findElements(By.tagName("input"));
		final Optional<WebElement> anyInputStringElement = inputElements.stream()
				.filter((inputElement) -> inputElement.getAttribute("name").equals(inputName))
				.findAny();
		if(anyInputStringElement.isEmpty()) {
			throw new RuntimeException(String.format("No child element of type 'input' with name='%s'", inputName));
		}
		return anyInputStringElement.get();
	}

	public static Collection<String> getClasses(WebElement element) {
		return Arrays.asList(element.getAttribute("class").split(" "));
	}

	public static Map<String, String> parseStyleProperties(WebElement element) {
		final Map<String, String> result = new TreeMap<>();
		final String styleStr = element.getAttribute("style");
		if(!styleStr.isEmpty()) {
			try {
				final String[] propertiesStrings = styleStr.split(";");
				for(String propertiesString : propertiesStrings) {
					final String[] propertiesWords = propertiesString.split(":");
					result.put(propertiesWords[0].trim(), propertiesWords[1].trim());
				}
			} catch(Throwable t) {
				final String message = String.format("Error parsing style '%s': %s", styleStr, t.getMessage());
				throw new RuntimeException(message, t);
			}
		}
		return result;
	}

	public WebElement getByTUID(long tuid) {
		return m_driver.findElement(By.id(String.valueOf(tuid)));
	}

	public void clickInElement(WebElement svgElement, long xOffset_px, long yOffset_px) {
		// I tried with Selenium 'Actions' but it did not work, maybe because of security restrictions.
		// For now the Javascript way is good enough.
		final String script = "var svg = arguments[0];" +
				"var rect = svg.getBoundingClientRect();" +
				"var x = rect.left + arguments[1];" +
				"var y = rect.top + arguments[2];" +
				"var evt = new MouseEvent('click', {" +
				"  clientX: x," +
				"  clientY: y," +
				"  bubbles: true," +
				"  cancelable: true" +
				"});" +
				"document.elementFromPoint(x, y).dispatchEvent(evt);";
		m_driver.executeScript(script, svgElement, xOffset_px, yOffset_px);
	}

}
