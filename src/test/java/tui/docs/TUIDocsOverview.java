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

import tui.ui.components.Page;
import tui.ui.components.Section;

public class TUIDocsOverview extends Page {

	public static final String PATH = "overview.html";

	public TUIDocsOverview() {
		super("TUI Overview", PATH);

		final Section chapter = appendSection("Overview");

		chapter.appendParagraph("TUI is a Java library that helps building and testing web UI by using Java code only.");

		final Section creatingAPage = chapter.createSubSection("Creating a page");
		creatingAPage.appendParagraph("Here is an example to create a new page made of one section containing a paragraph:");
		creatingAPage.append(new CodeParagraph("""
				Page page = new Page("Index", "/index");
				Section chapter1 = page.append(new Section("Title for first chapter");
				chapter1.appendParagraph("Here is my text...");
				"""));

		creatingAPage.appendParagraph(
				"Now that our page is ready, we want it to be shown. The first way to get it done is by exporting this page "
						+ "as a static HTML file:");
		creatingAPage.append(new CodeParagraph("""
				Style style = new Style();
				HTMLNode htmlNode = page.toHTMLNode(new Page.Resource(false, style.toCSS()), null);
				String html = htmlNode.toHTML();
				// 'html' string can be put in a file.
				"""));
		creatingAPage.appendParagraph("Note that we must instantiate a Style. This object gives the CSS content for the page to be styled"
				+ " in any web browser. If you want to change spaces, colors, borders, you can customize the Style instance with its methods."
				+ " The Style instance is given in a Resource that tells that it is embedded into the HTML content of the page. If we wanted"
				+ " the style to be served as a CSS file by the backend of our application, then we would have set the Resource as external.");

		creatingAPage.appendParagraph("An other way to provide our page is to use a server application. You may already have your favorite "
				+ "backend solution, but here we will see how it goes using TUI's backend which is initially design for test purpose.");
		creatingAPage.append(new CodeParagraph("""
				final TUIBackend backend = new TUIBackend(8000);
				backend.registerPage(page);
				backend.start();"""));
		creatingAPage.appendParagraph(
				"If you run this program, you will be able to see our page by browsing to http://localhost:8000/index");

		final Section makingItInteractive = chapter.createSubSection("Making it interactive");

		makingItInteractive.appendParagraph("Now we want to make our page adapt its content against user's actions. Let's say we want our "
				+ "page to help finding geographical data from a large number of cities.");
		makingItInteractive.appendParagraph("A page is made of components. Almost any component has the ability to be refreshed within the"
				+ " page without the need for reloading the entire page. One thing more, some components handle the user's actions to trigger "
				+ "updates.");
		makingItInteractive.appendParagraph("Let's create a new page, which will display a list of items from the database. We don't want "
				+ "to see all the items at once, so the page gives a field that can be used to filter the items.");
		makingItInteractive.append(new CodeParagraph("""
				Page page = new Page("Items", "/items");
				Search search = page.append(new Search("Search of items", "Go", "nameContains"));
				Table table = page.append(new Table("Found items", List.of("Reference", "Name", "Price")));
				"""));
		makingItInteractive.appendParagraph("Note how components are created and added to the page at the same time.");
		makingItInteractive.appendParagraph("The constructor of Search component takes 3 parameters: the title, the label of the button,"
				+ " and the name of the parameter that will be sent to the backend.");
		makingItInteractive.appendParagraph("Now we add what will make our page really dynamic:");
		makingItInteractive.append(new CodeParagraph("""
				table.setSource("/table");
				search.connectListener(table);"""));
		makingItInteractive.appendParagraph("The first line tells that the table can be refreshed, its content given by web "
				+ "service /table'. The second line tells that the table must be refreshed when th user validates the search.");
		makingItInteractive.appendParagraph(
				"Because the refresh of the table is triggered by the Search component, the '/table' web service"
						+ " will be called with additional parameter 'nameContains' which value will be the string entered by the user in the Search"
						+ " input.");
		makingItInteractive.appendParagraph(
				"Note that this parameter 'nameContains' and its value belong now to the refresh parameters of the table component. Any further "
						+ "refresh of that table will send this parameter to the backend, even if it's not triggered by the search component.");
	}
}
