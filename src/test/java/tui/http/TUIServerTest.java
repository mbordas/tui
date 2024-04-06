package tui.http;

import org.junit.Ignore;
import org.junit.Test;
import tui.html.HTMLNode;
import tui.ui.Page;
import tui.ui.Section;
import tui.ui.TUI;

public class TUIServerTest {

	@Ignore
	@Test
	public void defaultPage() throws Exception {
		HTMLNode.PRETTY_PRINT = true;
		final TUI ui = new TUI();
		final Page page = new Page("Server default page");
		final Section subSection = page.createSection("Title 1").createSubSection("Title 2");
		subSection.createParagraph("Lorem ipsum");

		ui.add(page);

		final TUIServer server = new TUIServer(ui);

		server.start(8080);

		Thread.sleep(60_000);
	}

}