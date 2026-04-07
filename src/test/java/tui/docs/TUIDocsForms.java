/*
 * Copyright (c) 2012-2026 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.docs;

import tui.ui.components.List;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.Section;

public class TUIDocsForms extends Page {

	public TUIDocsForms() {
		super("Forms", "forms.html");

		final Section chapter = appendSection("Form");

		chapter.appendParagraph("A form has the special ability to send parameters to the backend with values entered by the user.");
		chapter.append(new Paragraph.Text("Here is the life cycle of a form in TUI:"));
		chapter.append(new List(false))
				.append(new Paragraph.Text("The user enters inputs."))
				.append(new Paragraph.Text("The form inputs are sent to backend for validation."))
				.append(new Paragraph.Text("When successful, the validation triggers components updates."));
		chapter.appendParagraph("In this chapter we will look these steps in details.");

		final Section formValidation = chapter.createSubSection("Form validation");
	}
}
