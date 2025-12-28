/*
 * Copyright (c) 2012-2025 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.docs;

import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.Section;
import tui.ui.components.layout.Panel;
import tui.utils.TUIColors;

import java.awt.*;

public class TUIDocsStyle extends Page {

	public TUIDocsStyle() {
		super("Style", "style.html");

		final Section layoutsStyle = appendSection("Layouts style");
		layoutsStyle.appendParagraph(
				"You can customize the style of any layout components. These are the components that can contain other components.");
		layoutsStyle.appendParagraph("Example:");
		layoutsStyle.append(new CodeParagraph("""
				final Panel panel = new Panel(Panel.Align.CENTER);
						panel.append(new Paragraph("Hello, world!"));
						panel.customStyle().setBorderColor(Color.black);
				  		panel.customStyle().setBorderWidth_px(1);
						panel.customStyle().setPadding(10, 10, 10, 10);
						panel.customStyle().setBoxShadow(5, 5, 5, 0, TUIColors.toHSL(Color.gray));
						layoutsStyle.append(panel);"""));

		final Panel panel = layoutsStyle.append(new Panel(Panel.Align.CENTER)).append(new Panel());
		panel.append(new Paragraph("Hello, world!"));
		panel.customStyle().setBorderColor(Color.black);
		panel.customStyle().setBorderWidth_px(1);
		panel.customStyle().setPadding(10, 10, 10, 10);
		panel.customStyle().setBoxShadow(5, 5, 5, 0, TUIColors.toHSL(Color.gray));

		layoutsStyle.appendParagraph(
						"The options for style customization are named from the CSS properties. Not all of CSS properties are available.")
				.customStyle().setMargin(20, 0, 0, 0);
	}
}
