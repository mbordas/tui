/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui;

import org.junit.Test;
import tui.ui.Page;
import tui.ui.TUI;

import static org.junit.Assert.assertEquals;

public class TUISimpleTest {

	@Test
	public void start() {
		final TUI ui = new TUI();
		ui.add(new Page("home"));

		final UIClient client = new UIClient(ui);

		assertEquals("home", client.getTitle());
	}
}
