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

package tui.docs;

import tui.ui.components.List;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.Section;
import tui.ui.components.Table;
import tui.ui.components.TablePicker;
import tui.ui.components.form.Form;
import tui.ui.components.form.ModalForm;
import tui.ui.components.form.Search;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Panel;
import tui.ui.components.svg.SVG;

public class TUIDocsUpdatingAPage extends Page {

	public TUIDocsUpdatingAPage() {
		super("Updating a page", "updating_a_page.html");

		final Section chapter = appendSection("Updating a page");

		chapter.appendParagraph("In that chapter we will see how you can make a page respond to user's actions.");

		createOverviewSection(chapter);
		createConnectionSection(chapter);
		createParametersSection(chapter);
	}

	private static void createOverviewSection(Section chapter) {
		final Section section = chapter.createSubSection("Overview");

		section.appendParagraph("""
				When you want to update a component in a TUI Page, let's say the text of a paragraph, you have to configure the page so
				 that something will trigger the complete reload of this component.""");
		section.appendParagraph("Let's see how it looks like in a simple code:");
		section.append(new CodeParagraph("""
				Page page = new Page("my title");
				page.append(new Paragraph("my initial text here"));"""));
		section.appendParagraph("That code will build a very simple page that contains a text. But it is still static. In order to"
				+ " make the paragraph 'updatable', we will add a few things:");
		section.append(new List(false))
				.append(new Paragraph.Text("The paragraph will need to be reloaded. To do that, we need a backend with a web service which "
						+ "will serve the new content of the paragraph."))
				.append(new Paragraph.Text(
						"The page needs something so that the user could trigger the update of the paragraph. Here we use "
								+ "the simplest TUI component for that purpose: a RefreshButton."));
		section.appendParagraph("The code for the page becomes as follow:");
		section.append(new CodeParagraph("""
				Page page = new Page("my title");
				Paragraph paragraph = page.append(new Paragraph("my initial text here"));
				paragraph.setSource("/end_point_paragraph");
				RefreshButton button = page.append(new RefreshButton("Reload paragraph"));
				button.connectListener(paragraph);"""));
		section.appendParagraph(
				"And the code for the web service (where you want in your backend) must serve the content of the new version "
						+ "of the paragraph in the appropriate Json format of TUI:");
		section.append(new CodeParagraph("""
				Paragraph updatedParagraph = new Paragraph("my text provided by the backend");
				String responseContent = updatedParagraph.toJsonMap().toJson();
				String responseContentType = "application/json";"""));
	}

	private static void createConnectionSection(Section chapter) {
		final Section section = chapter.createSubSection("Triggers and updates");

		section.appendParagraph("The following TUI components are able to trigger other components' updates:");
		section.append(new List(false))
				.appendText("%s", RefreshButton.class.getSimpleName())
				.appendText("%s", TablePicker.class.getSimpleName())
				.appendText("%s", Search.class.getSimpleName())
				.appendText("%s and %s", Form.class.getSimpleName(), ModalForm.class.getSimpleName());

		section.appendParagraph("The following TUI components (and layout components) can be updated:");
		section.append(new List(false))
				.appendText("%s", Paragraph.class.getSimpleName())
				.appendText("%s", SVG.class.getSimpleName())
				.appendText("%s and %s", Table.class.getSimpleName(), TablePicker.class.getSimpleName())
				.appendText("%s (layout)", Grid.class.getSimpleName())
				.appendText("%s (layout)", Panel.class.getSimpleName());

		section.appendParagraph("Any triggering component can be connected to one or several refreshable components. And any refreshable "
				+ "component can be connected to one or several triggering components (that is not obvious, we will talk about that later).");
		section.append(
				new Paragraph().appendBold("""
						In order to be updated, a component must have a source set and a proper web service must be available in the backend.
						 If you don't provide source to a component, you will not even be able to connect a triggering component to it. But
						  if no suitable web service is available in the back, the user will only see an error appear on the component when 
						  the update process fails."""));
	}

	private static void createParametersSection(Section chapter) {
		final Section section = chapter.createSubSection("Parameters sent to backend");

		section.appendParagraph("""
				When a component update is triggered, some parameters are sent to the web service which provides the updated version
				 of the component.""");
		section.appendParagraph("There a 3 kind of parameters that will be added to the update request:");
		section.append(new List(true))
				.appendText("The session parameters. They are configured at the page level.")
				.appendText(
						"The parameters given by the triggering component. They depend on the type of triggering component and its configuration.")
				.appendText(
						"The parameters previously given by any triggering component which has been activated. This allows to manage filtering.");

		{
			final Section subSection = section.createSubSection("Session parameters");
			subSection.appendParagraph("Parameters are added to a %s as key-value pairs:", Page.class.getSimpleName());
			subSection.append(new CodeParagraph("page.setSessionParameter(\"sessionId\", \"jr4cNuOn6m20ab8kSVgU0mpkF\");"));
		}

		{
			final Section subSection = section.createSubSection("Parameters of the %s", RefreshButton.class.getSimpleName());
			subSection.appendParagraph("Parameters are added to a %s as key-value pairs:", RefreshButton.class.getSimpleName());
			subSection.append(new CodeParagraph("button.setParameter(\"name\", \"value\");"));
			subSection.appendParagraph("Of course they are not displayed in the page");
		}

		{
			final Section subSection = section.createSubSection("Parameters of the %s", TablePicker.class.getSimpleName());
			subSection.appendParagraph("""
					When a row is clicked, the parameters sent to the backend correspond to the values of that row where keys are the columns.
					The values of the hidden columns are sent too.""");
		}

		{
			final Section subSection = section.createSubSection("Parameters of the %s", Search.class.getSimpleName());
			subSection.appendParagraph("""
							One parameter is sent for each input. Additional hidden parameters can be set like with a %s.""",
					RefreshButton.class.getSimpleName());
		}

		{
			final Section subSection = section.createSubSection("Parameters of the %s and %s",
					Form.class.getSimpleName(), ModalForm.class.getSimpleName());
			subSection.appendParagraph("No parameter related to the form is sent to the web service that updates the component.");
			subSection.append(new Paragraph().appendBold(
					"Important note: the connected components are refreshed only when the form has been successfully"
							+ " submitted. When the submission fails, no component is refreshed."));
		}
	}

}
