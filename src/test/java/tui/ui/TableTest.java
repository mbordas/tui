/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui;

import org.junit.Test;
import tui.html.HTMLNode;
import tui.json.JsonObject;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TableTest {

	@Test
	public void toJson() {
		JsonObject.PRETTY_PRINT = true;
		Table table = new Table("Test table", List.of("A", "B"));
		table.append(Map.of("A", "test & co"));

		assertEquals("""
				{
				  "type": "table",
				  "thead": [
				    "A",
				    "B"
				  ],
				  "tbody": [
				    [
				      "test & co",
				      ""
				    ]
				  ]
				}""", table.toJsonObject().toJson());
	}

	@Test
	public void toHTML() {
		HTMLNode.PRETTY_PRINT = true;
		Page page = new Page("Table in page");
		Table table = new Table("Test table", List.of("A", "B"));
		table.append(Map.of("A", "test & co"));

		page.append(table);

		assertEquals("""
				<!DOCTYPE html><?xml version='1.0' encoding='UTF-8'?>
				<html>
				  <head>
				    <meta charset="utf-8"/>
				    <meta name="viewport" content="width=device-width, initial-scale=1"/>
				    <title>Table in page</title>
				  </head>
				  <body>
				    <main>
				      <table>
				        <thead>
				          <tr>
				            <th>A</th>
				            <th>B</th>
				          </tr>
				        </thead>
				        <tbody>
				          <tr>
				            <td>test & co</td>
				            <td/>
				          </tr>
				        </tbody>
				      </table>
				    </main>
				  </body>
				</html>
				""", page.toHTMLNode().toHTML());
	}
}
