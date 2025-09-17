package tui.ui.components.form;

import org.junit.Test;
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class FormInputTextAreaTest {

	@Test
	public void toHTML() {
		final FormInputTextArea formInputTextArea = new FormInputTextArea("label", "name");

		final HTMLNode htmlNode = formInputTextArea.toHTMLNode();

		HTMLNode.PRETTY_PRINT = true;
		assertEquals("""
				<textarea name="name" placeholder="Text">
				</textarea>
				""", htmlNode.toHTML());
	}

	@Test
	public void textAreaValueShouldBeSubmitted() throws Exception {
		final Form form = new Form("form", "/target");
		form.createInputTextArea("Text", "text");

		final Page page = new Page("textAreaValueShouldBeSubmitted", "/index");
		page.append(form);

		final String expectedValue = "Value\nto\nsubmit";
		final AtomicReference<String> submittedValue = new AtomicReference<>();

		try(final TUIBackend backend = new TUIBackend(8080)) {
			backend.start();
			backend.registerPage(page);
			backend.registerWebService(form.getTarget(), (uri, request, response) -> {
				final RequestReader reader = new RequestReader(request);
				submittedValue.set(reader.getStringParameter("text"));
				return Form.buildSuccessfulSubmissionResponse();
			});

			try(final Browser browser = new Browser(backend.getPort())) {
				browser.open(page.getSource());
				browser.typeFormField(form.getTitle(), "text", expectedValue);
				browser.submitForm(form.getTitle());
			}
		}

		assertEquals(expectedValue, submittedValue.get());
	}

}