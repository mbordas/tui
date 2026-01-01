/*
 * Copyright (c) 2012-2025 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.docs;

import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.Section;
import tui.ui.components.layout.Panel;
import tui.utils.TUIColors;

import java.awt.*;

public class TUIDocsStyle extends Page {

	public TUIDocsStyle() {
		super("Style", "style.html");

		final Section globalStyle = appendSection("Global style");
		globalStyle.appendParagraph(
				"TUI offers the ability to customize the global style of your web application. It is done either by serving a CSS file or by including "
						+ "the CSS text into each page's source code. You can chose one of these two options by using the Page.Resource class.");

		final Section externalCssFile = globalStyle.createSubSection("External CSS file");
		externalCssFile.appendParagraph(
				"Here are a few lines of code which will produce an HTML code for the page that points to an external CSS file.");
		externalCssFile.append(new CodeParagraph("""
				Style style = new Style();
				Page.Resource cssResource = new Page.Resource(true, "/css/tui.css"); // CSS file is available at '/css/tui.css'
				HTMLNode htmlNode = page.toHTMLNode(cssResource, null);"""));
		externalCssFile.appendParagraph(
				"In order to provider the page with its style, your backend must serve the page and the CSS file separately.");
		externalCssFile.append(new CodeParagraph("""
				String pageHTMLcontent = htmlNode.toHTML();
				String cssFileContent = style.toCSS();"""));
		externalCssFile.appendParagraph("These two strings can be served by web services or as static files.");

		final Section includedCssContent = globalStyle.createSubSection("Included CSS content");
		includedCssContent.appendParagraph(
				"Now if you better want to include the CSS content directly into the HTML page (maybe to make your backend simpler or"
						+ " to create a static HTML file), you just need to configure the Page.Resource instance as non 'external'.");
		globalStyle.append(new CodeParagraph("""
				Style style = new Style();
				Page.Resource cssResource = new Page.Resource(false, style.toCSS()); // CSS content is embedded into the page
				HTMLNode htmlNode = page.toHTMLNode(cssResource, null);
				String html = htmlNode.toHTML(); // This text contains both HTML and CSS"""));

		final Section customizationSection = globalStyle.createSubSection("Customization");
		customizationSection.appendParagraph(
				"The Style class gives methods to change the appearance of most of the components. All the pages of this documentation are "
						+ "styled using the default parameters of the Style class.");
		customizationSection.appendParagraph(
				"Note that the paragraphs that contain Java code are not standard components from TUI. They are "
						+ "custom components with a custom text styling algorithm. You can find the source code in the class CodeParagraph.");

		final Section customStyles = appendSection("Custom styles");
		customStyles.appendParagraph(
				"In the next sections we will see how to override the global style and customize the appearance of components.");

		final Section textsStyle = customStyles.createSubSection("Texts style");
		textsStyle.appendParagraph(
				"You can override the appearance of the text of any component by calling its customTextStyle() method, for example:");
		textsStyle.append(new CodeParagraph("""
				RefreshButton customButton = new RefreshButton("Click here");
				customButton.customTextStyle()
						.setSize_em(0.6f)
						.setItalic();"""));

		RefreshButton customButton = new RefreshButton("Click here");
		customButton.customTextStyle()
				.setSize_em(0.6f)
				.setItalic();
		{
			final Panel panel = textsStyle.append(new Panel(Panel.Align.CENTER)).append(new Panel());
			panel.append(customButton);
		}

		textsStyle.appendParagraph(
				"The options for style customization are named from the CSS properties. Not all of CSS properties are available.");

		final Section layoutsStyle = customStyles.createSubSection("Layouts style");
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

		{
			final Panel panel = layoutsStyle.append(new Panel(Panel.Align.CENTER)).append(new Panel());
			panel.append(new Paragraph("Hello, world!"));
			panel.customStyle().setBorderColor(Color.black);
			panel.customStyle().setBorderWidth_px(1);
			panel.customStyle().setPadding(10, 10, 10, 10);
			panel.customStyle().setBoxShadow(5, 5, 5, 0, TUIColors.toHSL(Color.gray));
		}

		layoutsStyle.appendParagraph(
						"The options for style customization are named from the CSS properties. Not all of CSS properties are available.")
				.customStyle().setMargin(20, 0, 0, 0);
	}
}
