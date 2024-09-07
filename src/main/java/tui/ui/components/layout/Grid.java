/* Copyright (c) 2024, Mathieu Bordas
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

package tui.ui.components.layout;

import tui.html.HTMLNode;
import tui.json.JsonMap;
import tui.ui.components.Paragraph;
import tui.ui.components.UIComponent;
import tui.ui.components.UIRefreshableComponent;

public class Grid extends UIRefreshableComponent {

	public static final String HTML_CLASS_CONTAINER = "tui-container-grid";
	public static final String HTML_CLASS = "tui-grid";
	public static final String HTML_CLASS_FIRST_ROW = "tui-grid-first-row";
	public static final String HTML_CLASS_LAST_ROW = "tui-grid-last-row";

	public static final String HTML_CLASS_FIRST_COLUMN = "tui-grid-first-column";
	public static final String HTML_CLASS_LAST_COLUMN = "tui-grid-last-column";

	public static final String JSON_TYPE = "grid";

	private final UIComponent[][] m_components;

	public Grid(int rows, int columns) {
		assert rows > 0;
		assert columns > 0;
		m_components = new UIComponent[rows][columns];
	}

	public void set(int rowIndex, int columnIndex, UIComponent component) {
		m_components[rowIndex][columnIndex] = component;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("div", HTML_CLASS_CONTAINER);

		final HTMLNode gridElement = containedElement.element();
		gridElement.setClass(HTML_CLASS);
		gridElement.setAttribute("style",
				String.format("grid-template-rows: %s;grid-template-columns: %s",
						"1fr ".repeat(m_components.length),
						"1fr ".repeat(m_components[0].length)));

		for(int row = 0; row < m_components.length; row++) {
			for(int column = 0; column < m_components[0].length; column++) {
				final UIComponent childComponent = m_components[row][column];
				final HTMLNode childElement;
				if(childComponent != null) {
					childElement = childComponent.toHTMLNode();
				} else {
					childElement = new Paragraph("").toHTMLNode();
				}
				if(row == 0) {
					childElement.addClass(HTML_CLASS_FIRST_ROW);
				}
				if(column == 0) {
					childElement.addClass(HTML_CLASS_FIRST_COLUMN);
				}
				if(row == m_components.length - 1) {
					childElement.addClass(HTML_CLASS_LAST_ROW);
				}
				if(column == m_components[0].length) {
					childElement.addClass(HTML_CLASS_LAST_COLUMN);
				}
				gridElement.addChild(childElement);
			}
		}

		return containedElement.getHigherNode();
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE);
		result.setAttribute("rows", m_components.length);
		result.setAttribute("columns", m_components[0].length);
		for(int row = 0; row < m_components.length; row++) {
			for(int column = 0; column < m_components[0].length; column++) {
				final UIComponent child = m_components[row][column];
				if(child != null) {
					final String childName = String.format("%s_%s", row, column);
					result.setChild(childName, child.toJsonMap());
				}
			}
		}
		return result;
	}

}
