package tui.ui;

import org.junit.Test;
import tui.html.HTMLNode;

import static org.junit.Assert.assertEquals;

public class PageTest {

	@Test
	public void emptyHTML() {
		HTMLNode.PRETTY_PRINT = true;

		Page page = new Page("Empty page");
		assertEquals("""
						<!DOCTYPE html><?xml version='1.0' encoding='UTF-8'?>
						<html>
						  <head>
						    <meta charset="utf-8"/>
						    <meta name="viewport" content="width=device-width, initial-scale=1"/>
						    <title>Empty page</title>
						  </head>
						  <body>
						    <main/>
						  </body>
						</html>
						""",
				page.toHTMLNode().toHTML().replaceAll("\r\n", "\n"));
	}

}