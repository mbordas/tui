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

	public static final String JSON_TYPE = "grid";
	public static final String HTML_CLASS_CONTAINER = "tui-container-grid";
	public static final String HTML_CLASS = "tui-grid";

	private final UIComponent[][] m_components;
	private final String[] m_rowsHeight;
	private final String[] m_columnsWidth;

	public Grid(int rows, int columns) {
		assert rows > 0;
		assert columns > 0;
		m_components = new UIComponent[rows][columns];
		m_rowsHeight = new String[rows];
		m_columnsWidth = new String[columns];
	}

	public Grid setRowHeightAuto(int rowIndex) {
		m_rowsHeight[rowIndex] = "auto";
		return this;
	}

	public Grid setColumnWidth_px(int columnIndex, int width_px) {
		m_columnsWidth[columnIndex] = String.format("%dpx", width_px);
		return this;
	}

	public Grid setColumnWidthAuto(int columnIndex) {
		m_columnsWidth[columnIndex] = "auto";
		return this;
	}

	public Grid setColumnWidthMaxContent(int columnIndex) {
		m_columnsWidth[columnIndex] = "min-content";
		return this;
	}

	public <C extends UIComponent> C set(int rowIndex, int columnIndex, C component) {
		m_components[rowIndex][columnIndex] = component;
		return component;
	}

	@Override
	public HTMLNode toHTMLNode() {
		final ContainedElement containedElement = createContainedNode("div", HTML_CLASS_CONTAINER);

		final HTMLNode gridElement = containedElement.element();
		gridElement.setClass(HTML_CLASS);

		gridElement.setStyleProperty("grid-template-rows", computeGridTemplateRows());
		gridElement.setStyleProperty("grid-template-columns", computeGridTemplateColumns());

		for(final UIComponent[] row : m_components) {
			for(final UIComponent cell : row) {
				final HTMLNode childElement;
				if(cell != null) {
					childElement = cell.toHTMLNode();
				} else {
					childElement = new Paragraph("").toHTMLNode();
				}
				gridElement.append(childElement);
			}
		}

		return containedElement.getHigherNode();
	}

	String computeGridTemplateRows() {
		final StringBuilder result = new StringBuilder();
		for(String height : m_rowsHeight) {
			if(height == null) {
				result.append("auto ");
			} else {
				result.append(height).append(" ");
			}
		}
		return result.toString();
	}

	String computeGridTemplateColumns() {
		final StringBuilder result = new StringBuilder();
		for(String width : m_columnsWidth) {
			if(width == null) {
				result.append("1fr ");
			} else {
				result.append(width).append(" ");
			}
		}
		return result.toString();
	}

	@Override
	public JsonMap toJsonMap() {
		final JsonMap result = new JsonMap(JSON_TYPE, getTUID());
		result.setAttribute("rows", m_components.length);
		result.setAttribute("columns", m_components[0].length);
		result.setAttribute("rows-width", computeGridTemplateRows());
		result.setAttribute("columns-width", computeGridTemplateColumns());
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
