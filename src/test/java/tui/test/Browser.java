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
import tui.test.components.TFormTest;
import tui.test.components.TTableTest;
import tui.ui.components.form.Form;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

	public List<WebElement> getPanels() {
		return m_driver.findElements(By.className("tui-panel"));
	}

	public Collection<WebElement> getFields(String formTitle) {
		final WebElement formElement = getForm(formTitle);
		return TFormTest.getFields(formElement);
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

}
