/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.HTMLFetchErrorMessage;
import tui.test.components.TFormTest;
import tui.test.components.TTableTest;
import tui.ui.components.NavButton;
import tui.ui.components.NavLink;
import tui.ui.components.RefreshButton;
import tui.ui.components.Table;
import tui.ui.components.form.Form;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;
import tui.ui.components.layout.VerticalScroll;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;

import static tui.test.components.TFormTest.getFieldName;

public class Browser {

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

	public void stop() {
		m_driver.quit();
	}

	public void clickRefreshButton(String label) {
		final List<WebElement> buttonElements = m_driver.findElements(By.className(RefreshButton.HTML_CLASS));
		final Optional<WebElement> anyButton = buttonElements.stream()
				.filter((element) -> label.equals(element.getText()))
				.findAny();
		WebElement button;
		if(anyButton.isEmpty()) {
			throw new NullPointerException(String.format("Refresh button '%s' not found.", label));
		} else {
			button = anyButton.get();
		}
		button.click();
	}

	// NAVLINKS

	public List<WebElement> getNavLinks() {
		return m_driver.findElements(By.className(NavLink.HTML_CLASS));
	}

	// NAVBUTTONS

	public List<WebElement> getNavButtons() {
		return m_driver.findElements(By.className(NavButton.HTML_CLASS));
	}

	// TABLES

	public WebElement getTable(String title) {
		final Optional<WebElement> anyFormElement = getTables().stream()
				.filter(WebElement::isDisplayed)
				.filter((element) -> title.equals(TTableTest.getTitle(element)))
				.findAny();

		if(anyFormElement.isPresent()) {
			return anyFormElement.get();
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

	public List<WebElement> getSVGs() {
		return m_driver.findElements(By.tagName("svg"));
	}

	public List<WebElement> getPanels() {
		return m_driver.findElements(By.className("tui-panel"));
	}

	public Collection<WebElement> getFields(String formTitle) {
		final WebElement formElement = getForm(formTitle);
		return TFormTest.getFields(formElement);
	}

	// IMAGES

	public List<WebElement> getImages() {
		return m_driver.findElements(By.tagName("img"));
	}

	// FORMS

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
		return m_driver.findElements(By.className(Form.HTML_CLASS));
	}

	public void typeField(String formTitle, String name, String value) {
		final Optional<WebElement> anyFieldName = getFields(formTitle).stream()
				.filter((field) -> name.equals(getFieldName(field)))
				.findAny();

		if(anyFieldName.isPresent()) {
			WebElement inputElement = anyFieldName.get().findElement(By.tagName("input"));
			inputElement.sendKeys(value);
		} else {
			throw new RuntimeException("Field input element not found: " + name);
		}
	}

	public void selectRadio(String formTitle, String name, String option) {
		final Optional<WebElement> anyFieldName = getFields(formTitle).stream()
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

	public void selectFile(String formTitle, String name, File localFile) {
		final Optional<WebElement> anyFieldName = getFields(formTitle).stream()
				.filter((field) -> name.equals(getFieldName(field)))
				.findAny();

		if(anyFieldName.isPresent()) {
			WebElement inputElement = anyFieldName.get().findElement(By.tagName("input"));
			inputElement.sendKeys(localFile.getAbsolutePath());
		} else {
			throw new RuntimeException("Field input element not found: " + name);
		}
	}

	public void submit(String formTitle) {
		final WebElement formElement = getForm(formTitle);
		final Optional<WebElement> anySubmitButton = formElement.findElements(By.tagName("button")).stream()
				.filter((button) -> "submit".equals(button.getAttribute("type")))
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

}
