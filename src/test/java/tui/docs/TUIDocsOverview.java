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
import tui.ui.components.Section;

public class TUIDocsOverview extends Page {

	public static final String PATH = "overview.html";

	public TUIDocsOverview() {
		super("TUI Overview", PATH);

		final Section chapter = appendSection("Overview");

		chapter.appendParagraph("TUI is a Java library that helps building and testing web UI by using Java code only.");
		chapter.appendParagraph("In this chapter we will see how to create a dynamic page, how to provide it, and how to test it.");

		final Section creatingAPage = chapter.createSubSection("Creating a page");
		creatingAPage.appendParagraph("Here is an example to create a new page made of one section containing a paragraph:");
		creatingAPage.append(new CodeParagraph("""
				Page page = new Page("Index", "/index");
				Section chapter1 = page.append(new Section("Title for first chapter"));
				chapter1.appendParagraph("Here is my text...");
				"""));

		creatingAPage.appendParagraph(
				"Now that our page is ready, we want it to be shown. The first way to get it done is by exporting this page "
						+ "as a static HTML file:");
		creatingAPage.append(new CodeParagraph("""
				Style style = new Style();
				HTMLNode htmlNode = page.toHTMLNode(new Page.Resource(false, style.toCSS()), null);
				String html = htmlNode.toHTML();
				// 'html' string can be put in a .html file.
				"""));
		creatingAPage.appendParagraph("Note that we must instantiate a Style. This object gives the CSS content for the page to be styled"
				+ " in any web browser. If you want to change spaces, colors, borders, you can customize the Style instance with its methods."
				+ " The Style instance is given in a Resource that tells that it is embedded into the HTML content of the page (isExternal=false). If we wanted"
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
				+ "page to help finding pricing data from a large number of items located by their cities.");
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
				+ "service '/table'. The second line tells that the table will be refreshed each time the user validates the search.");
		makingItInteractive.appendParagraph(
				"Because the refresh of the table is triggered by the Search component, the '/table' web service"
						+ " will be called with additional parameter 'nameContains' which value will be the string entered by the user in the Search"
						+ " input.");

		makingItInteractive.appendParagraph("Let's put some data into our backend:");
		makingItInteractive.append(new CodeParagraph("record Item(String reference, String name, double price_Euro) {}"));
		makingItInteractive.append(new CodeParagraph("""
				final List<Item> allItems = new ArrayList<>();
					for(int i = 0; i < 50; i++) {
						allItems.add(new Item(String.format("I%%012d", (int) (Math.random() * 1_000_000_000.0)), TestUtils.getRandomCityName(),
							Math.random() * 10_000.0));
					}"""));
		makingItInteractive.appendParagraph(
				"The list 'allItems' will act as a database. Now we implement the web service that is expected to refresh"
						+ " the table component:");
		makingItInteractive.append(new CodeParagraph("""
				backend.registerWebService("/table",
					(uri, request, response) -> {
						final RequestReader reader = new RequestReader(request);
						final String nameContains = reader.getStringParameter("nameContains", "");
						final Table filteredTable = new Table(table.getTitle(), table.getColumns());
						allItems.stream()
							.filter((item) -> nameContains.trim().isEmpty() || item.name.contains(nameContains))
							.forEach((item) -> filteredTable.append(
								Map.of("Reference", item.reference, "Name", item.name, "Price",
									String.format("%%.2f €", item.price_Euro))));
						return filteredTable.toJsonMap();
					});"""));
		makingItInteractive.appendParagraph("Our page is ready to run. Launch the main of the class ")
				.appendBold(OverviewExample.class.getSimpleName())
				.appendNormal(" and try it by yourself.");

		final Section automatedTesting = chapter.createSubSection("Automated testing");

		automatedTesting.appendParagraph(
				"Let's summarize. Our page contains a table that aims at displaying information of a list of items, and"
						+ " a 'search' component that acts like a filter for the table.");
		{
			final Paragraph paragraph = automatedTesting.appendParagraph("We will see now how to perform this test case:");
			final List list = new List(true)
					.append(new Paragraph.Text("ARRANGE: prepare a complete list of items."))
					.append(new Paragraph.Text("ACT: enter some value in the 'search' and submit."))
					.append(new Paragraph.Text("ASSERT: check that only filtered items are displayed in the table."));
			paragraph.append(list);
		}

		automatedTesting.appendParagraph(
				"We have already coded how to create a bunch of items, we will re-use that code for the ARRANGE phase.");
		automatedTesting.appendParagraph(
				"Now in the ACT phase, we want to use the 'search' component. First of all, we need to open the page, and"
						+ " we do that with a TClient:");
		automatedTesting.append(new CodeParagraph("""
				final TClient client = new TClient(8000);
				client.open(page.getSource());"""));
		automatedTesting.appendParagraph("The TClient will replace a web browser in our test. It is a light client implementation that "
				+ " almost behaves like a real browser running the TUI's javascript. The main difference is that the TClient doesn't "
				+ "manage any style.");
		automatedTesting.appendParagraph("Let's try the filter with an empty value. We will expected to get all the items.");
		automatedTesting.append(new CodeParagraph("""
				final TSearch search = client.getSearch("Search of items");
				search.enterInput("nameContains", ""); // will select all items
				search.submit();"""));
		automatedTesting.appendParagraph("We can then add a simple assertion to check that now the table is loaded with all the items:");
		automatedTesting.append(new CodeParagraph("""
				TTable table = client.getTable("Found items");
				assertEquals(50, table.size());"""));
		automatedTesting.appendParagraph("And we can go further and check the filtering functionality of our page by checking that all "
				+ "items of the table match the filter (the following code may not be accurate for a real test, but it shows capabilities):");
		automatedTesting.append(new CodeParagraph("""
				// Testing with one filter -> the table should only contain filtered values
				search.enterInput("nameContains", "al");
				search.submit();
				
				table = client.getTable("Found items");
				assertFalse(table.isEmpty());
				for(List<Object> row : table.getRows()) {
					final String name = row.get(1).toString();
					assertTrue(name.contains("al"));
				}"""));
		automatedTesting.appendParagraph("You can find a running version of that code in the test class ")
				.appendBold(OverviewExample.class.getSimpleName())
				.appendNormal(".");

	}
}
