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

import tui.html.HTMLNode;
import tui.http.TUIBackend;
import tui.test.TClient;
import tui.test.components.TSearch;
import tui.test.components.TTable;
import tui.ui.Style;
import tui.ui.StyleSet;
import tui.ui.UIConfigurationException;
import tui.ui.components.DownloadButton;
import tui.ui.components.Image;
import tui.ui.components.NavButton;
import tui.ui.components.NavLink;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButtonTest;
import tui.ui.components.Section;
import tui.ui.components.Table;
import tui.ui.components.TableData;
import tui.ui.components.TablePicker;
import tui.ui.components.UIComponent;
import tui.ui.components.UIRefreshableComponent;
import tui.ui.components.form.Form;
import tui.ui.components.form.FormInput;
import tui.ui.components.form.FormInputCheckbox;
import tui.ui.components.form.FormInputDay;
import tui.ui.components.form.FormInputDayHHmm;
import tui.ui.components.form.FormInputEmail;
import tui.ui.components.form.FormInputFile;
import tui.ui.components.form.FormInputNumber;
import tui.ui.components.form.FormInputPassword;
import tui.ui.components.form.FormInputRadio;
import tui.ui.components.form.FormInputSearch;
import tui.ui.components.form.FormInputString;
import tui.ui.components.form.ModalForm;
import tui.ui.components.form.Search;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.Panel;
import tui.ui.components.layout.TabbedFlow;
import tui.ui.components.layout.VerticalFlow;
import tui.ui.components.layout.VerticalScroll;
import tui.ui.components.svg.CoordinatesComputer;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGCircle;
import tui.ui.components.svg.SVGComponent;
import tui.ui.components.svg.SVGPath;
import tui.ui.components.svg.SVGRectangle;
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGMarker;
import tui.ui.components.svg.graph.DataSerie;
import tui.ui.components.svg.graph.LineSerie;
import tui.ui.components.svg.graph.StepLineSerie;
import tui.ui.components.svg.graph.UIGraph;
import tui.utils.TestUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CodeParagraph extends Paragraph {

	public static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
	public static final Color BORDER_COLOR = new Color(200, 200, 200);

	public static final Color COLOR_JAVA_KEYS = new Color(182, 96, 37);
	public static final Color COLOR_TUI_CLASS = new Color(18, 61, 195);
	public static final Color COLOR_STRING_LITERALS = new Color(34, 193, 67);

	private Map<String, Color> m_colorMap = new HashMap<>();

	public CodeParagraph() {
		customStyle().setPadding(5, 5, 5, 5);
		customStyle().setBackgroundColor(BACKGROUND_COLOR);
		customStyle().setFontFamily("Consolas");
		customStyle().setLineHeight(1.4);
		customStyle().setBorderWidth_px(1);
		customStyle().setBorderColor(BORDER_COLOR);
		customStyle().setBorderRadius(3);

		setColorizedWords(COLOR_JAVA_KEYS, "new", "private", "final", "static", "void", "record");

		registerClassForColor(UIComponent.class, UIRefreshableComponent.class);
		registerClassForColor(HTMLNode.class);
		registerClassForColor(Grid.class, Layouts.class, Panel.class, TabbedFlow.class, VerticalFlow.class, VerticalScroll.class);
		registerClassForColor(DownloadButton.class, Page.class, Image.class, NavButton.class, NavLink.class, Paragraph.class,
				RefreshButtonTest.class, Section.class, Table.class, TableData.class, TablePicker.class);
		registerClassForColor(Form.class, FormInput.class, FormInputCheckbox.class, FormInputDay.class, FormInputDayHHmm.class,
				FormInputEmail.class, FormInputFile.class, FormInputNumber.class, FormInputPassword.class, FormInputRadio.class,
				FormInputSearch.class, FormInputString.class, ModalForm.class, Search.class);
		registerClassForColor(SVG.class, SVGCircle.class, SVGComponent.class, SVGPath.class, SVGRectangle.class, SVGText.class,
				CoordinatesComputer.class, DataSerie.class, LineSerie.class, StepLineSerie.class, UIGraph.class, SVGMarker.class);
		registerClassForColor(Style.class, StyleSet.class);
		registerClassForColor(TestUtils.class, TClient.class, TTable.class, TSearch.class);
		registerClassForColor(UIConfigurationException.class);

		registerClassForColor(TUIBackend.class);
	}

	private void registerClassForColor(Class<?>... clazz) {
		for(Class<?> _clazz : clazz) {
			setColorizedWords(COLOR_TUI_CLASS, _clazz.getSimpleName());
		}
	}

	public CodeParagraph(String content) {
		this();

		append(content);
	}

	public void append(String code) {
		StringBuilder currentSpace = null;
		StringBuilder currentWord = null;
		StringBuilder currentStringLiteral = null;

		for(int i = 0; i < code.length(); i++) {
			char c = code.charAt(i);
			if('"' == c) {
				if(currentStringLiteral == null) {
					currentStringLiteral = new StringBuilder().append(c);
				} else {
					currentStringLiteral.append(c);
					currentStringLiteral = flushLiteralString(currentStringLiteral);
				}

				currentWord = flushWord(currentWord);
				currentSpace = flushSpace(currentSpace);
			} else if(currentStringLiteral != null) {
				currentStringLiteral.append(c);
			} else if(Character.isLetterOrDigit(c)) {
				currentWord = currentWord == null ? new StringBuilder().append(c) : currentWord.append(c);
				currentSpace = flushSpace(currentSpace);
			} else {
				currentSpace = currentSpace == null ? new StringBuilder().append(c) : currentSpace.append(c);
				currentWord = flushWord(currentWord);
			}
		}

		// Handling the last character
		flushLiteralString(currentStringLiteral);
		flushSpace(currentSpace);
		flushWord(currentWord);
	}

	private StringBuilder flushLiteralString(StringBuilder currentStringLiteral) {
		if(currentStringLiteral != null) {
			append((style) -> style.setTextColor(COLOR_STRING_LITERALS), currentStringLiteral.toString());
		}
		return null;
	}

	private StringBuilder flushWord(StringBuilder currentWord) {
		if(currentWord != null) {
			final String word = currentWord.toString();
			final Color color = m_colorMap.get(word);
			append(color == null ? null : (style) -> style.setTextColor(color), currentWord.toString());
		}
		return null;
	}

	private StringBuilder flushSpace(StringBuilder currentSpace) {
		if(currentSpace != null) {
			append(null, currentSpace.toString());
		}
		return null;
	}

	public void setColorizedWords(Color color, String... words) {
		for(String word : words) {
			m_colorMap.put(word.trim(), color);
		}
	}
}
