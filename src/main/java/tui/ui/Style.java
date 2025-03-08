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

package tui.ui;

import tui.ui.components.layout.Layouts;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class Style {

	static class GlobalColors {
		Color text = new Color(46, 46, 46);
		Color borders = new Color(180, 180, 180);
		Color action = new Color(0, 198, 252);
		Color cancel = new Color(222, 222, 222);
		Color delete = new Color(252, 40, 3);
		Color neutral = new Color(230, 230, 230);
		Color greenState = new Color(115, 250, 70);
		Color redState = new Color(252, 40, 3);
		Color tableRowHover = new Color(192, 240, 252);
	}

	public record Padding(int top_px, int right_px, int bottom_px, int left_px) {
	}

	public record Margin(int top_px, int right_px, int bottom_px, int left_px) {
	}

	private final GlobalColors m_globalColors = new GlobalColors();

	private final StyleSet m_global = new StyleSet();
	private final StyleSet m_body = new StyleSet();
	private final StyleSet m_header = new StyleSet();
	private final StyleSet m_header_link = new StyleSet();
	private final StyleSet m_header_submitButton = new StyleSet();
	private final StyleSet m_footer = new StyleSet();
	private final Map<Integer, StyleSet> m_headings = new TreeMap<>();
	private final StyleSet m_section = new StyleSet();
	private final StyleSet m_paragraph = new StyleSet();
	private final StyleSet m_link = new StyleSet();
	private final StyleSet m_button = new StyleSet();
	private final StyleSet m_submitButton = new StyleSet();

	public Style() {

		global().setTextColor("var(--global-color-text)");
		global().setFontFamily(
				"Public Sans Web, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol");

		body().overrideProperty("min-height", "100vh");
		body().setPadding(0, 0, 0, 0);
		body().setMargin(0, 0, 0, 0);
		body().setFontFamily("Arial, sans-serif");

		header().setBackgroundColor("var(--global-color-background)");
		header().setTextColor("var(--global-color-background-contrast)");
		header().setPadding(10, 10, 10, 10);
		header().overrideProperty("vertical-align", "middle");
		header().setBorderWidth_px(0, 0, 1, 0);

		headerLink().setTextAlign(Layouts.TextAlign.CENTER);
		headerLink().setNoTextDecoration();

		headerSubmitButton().setNoBackground();
		headerSubmitButton().setPadding(0, 5, 0, 5);
		headerSubmitButton().setNoBorder();
		headerSubmitButton().setTextUnderlined();
		headerSubmitButton().setCursorPointingHand();

		footer().setBackgroundColor("var(--global-color-background)");
		footer().setTextColor("var(--global-color-background-contrast)");
		footer().setPadding(10, 10, 10, 10);
		footer().setBorderWidth_px(1, 0, 0, 0);

		section().setTextAlign(Layouts.TextAlign.LEFT);
		section().setMargin(30, 0, 0, 0);

		heading(1).setPadding(0, 0, 0, 0);
		heading(1).setTextSize_em(2);

		heading(2).setPadding(0, 0, 0, 0);
		heading(2).setTextSize_em(1.5f);

		heading(3).setPadding(0, 0, 0, 0);
		heading(3).setTextSize_em(1.2f);
		heading(3).setFontWeight("lighter");

		heading(4).setPadding(0, 0, 0, 0);
		heading(4).setTextSize_em(1.1f);
		heading(4).setTextItalic();
		heading(4).setFontWeight("lighter");

		paragraph().setTextAlign(Layouts.TextAlign.LEFT);
		paragraph().setMargin(0, 0, 10, 0);
		paragraph().setPadding(0, 0, 0, 0);

		link().setTextColor("var(--global-color-action)");

		button().setBorderRadius_px(2);
		button().setPadding(5, 20, 5, 20);
		button().setTextAlign(Layouts.TextAlign.CENTER);
		button().setCursorPointingHand();
		button().setBackgroundColor("var(--global-color-cancel)");
		button().setTextColor("var(--global-color-cancel-contrast)");
		button().setBorderWidth_px(1);
		button().setBorderColor("var(--global-color-borders)");

		submitButton().setBackgroundColor("var(--global-color-action)");
		submitButton().setTextColor("var(--global-color-action-contrast)");
		submitButton().setBorderColor("var(--global-color-action)");
	}

	public void setColorForAction(Color color) {
		m_globalColors.action = color;
	}

	public void setColorForTableRowHover(Color color) {
		m_globalColors.tableRowHover = color;
	}

	public StyleSet global() {
		return m_global;
	}

	public StyleSet body() {
		return m_body;
	}

	public StyleSet header() {
		return m_header;
	}

	public StyleSet headerLink() {
		return m_header_link;
	}

	public StyleSet headerSubmitButton() {
		return m_header_submitButton;
	}

	public StyleSet footer() {
		return m_footer;
	}

	public StyleSet button() {
		return m_button;
	}

	public StyleSet submitButton() {
		return m_submitButton;
	}

	public StyleSet section() {
		return m_section;
	}

	/**
	 * @param depth 1 (h1) or more.
	 */
	public StyleSet heading(int depth) {
		assert depth >= 1;
		return m_headings.computeIfAbsent(depth, (_depth) -> new StyleSet());
	}

	public StyleSet paragraph() {
		return m_paragraph;
	}

	public StyleSet link() {
		return m_link;
	}

	public String toCSS() {
		final StringBuilder result = new StringBuilder();

		appendGlobalVariables(result);

		result.append(global().toCSS("*")).append("\n");
		result.append(body().toCSS("body")).append("\n");

		for(Map.Entry<Integer, StyleSet> headingEntry : m_headings.entrySet()) {
			final String selector = String.format("h%d", headingEntry.getKey());
			result.append(headingEntry.getValue().toCSS(selector)).append("\n");
		}

		result.append(link().toCSS("a")).append("\n");
		result.append(header().toCSS("header")).append("\n");
		result.append(headerLink().toCSS("header a")).append("\n");
		result.append(headerSubmitButton().toCSS("header button[type='submit']")).append("\n");
		result.append(footer().toCSS("footer")).append("\n");
		result.append(section().toCSS("section")).append("\n");
		result.append(paragraph().toCSS("p")).append("\n");

		result.append("""
				
				.tui-border-on {
				    border: 1px solid var(--global-color-border);
				}
				
				.tui-reading-normal-area { /* In NORMAL layout, the width of central area is given. */
				    width: 70em;
				}
				
				.tui-reading-wide-margin { /* In WIDE layout, the width of margins are given. */
				   width: 20px;
				}
				
				.tui-align-left {
				    text-align: left;
				}
				
				.tui-align-right {
				    text-align: right;
				}
				
				.tui-align-center {
				    text-align: center;
				}
				
				.tui-align-stretch {
				    text-align: justify;
				}
				
				/* TABS */
				
				.tui-tabnav {
				    display: flex;
				    justify-content: left;
				    align-items: center;
				}
				.tui-tablink {
				    font-size: 1em;
				    padding-left: 30px;
				    padding-right: 30px;
				    cursor: pointer;
				    background: none;
				    border-radius: 0px;
				    border-left: none;
				    border-top: none;
				    border-right: none;
				    border-bottom: 1px solid var(--global-color-border);
				}
				.tui-tablink-active {
				    border-bottom: 3px solid var(--global-color-action);
				}
				
				/* LAYOUTS */
				
				.tui-vertical-spacing-fit {
				    padding-top: 0px;
				}
				.tui-vertical-spacing-compact {
				    padding-top: 10px;
				}
				.tui-vertical-spacing-normal {
				    padding-top: 20px;
				}
				.tui-vertical-spacing-large {
				    padding-top: 30px;
				}
				
				.tui-horizontal-spacing-fit {
				    margin-right: 0px;
				}
				.tui-horizontal-spacing-compact {
				    margin-right: 10px;
				}
				.tui-horizontal-spacing-normal {
				    margin-right: 20px;
				}
				.tui-horizontal-spacing-large {
				    margin-right: 30px;
				}
				
				.tui-grid {
				    display: grid;
				    width: 100%;
				    margin: 0px;
				    padding: 0px;
				    justify-items: stretch;
				}
				
				.tui-vertical-scroll {
				    overflow-y: auto;
				}
				
				/* SEARCH */
				
				tui-search-form {
					margin: 10px 0px 10px 0px;
				}
				
				search>div {
					display: inline-block;
				}
				search>label {
					margin-right: 10px;
				}
				
				/* FORM */
				
				tui-form {
				    margin: 10px 0px 10px 0px;
				    width: 400px;
				}
				fieldset {
				    border: 1px solid var(--global-color-border);
				    background: var(--global-color-background);
				    position: relative;
				}
				/* https://www.youtube.com/watch?v=ezP4kbOvs_E */
				@property --angle {
				    syntax: "<angle>";
				    initial-value: 0deg;
				    inherits: false;
				}
				.form-pending::after {
				    content: '';
				    position: absolute;
				    height: 100%;
				    width: 100%;
				    top: 50%;
				    left: 50%;
				    translate: -50% -50%;
				    z-index: -1;
				    padding: 3px;
				    background-image: conic-gradient(from var(--angle), var(--global-color-background) 0% 50%, var(--global-color-action) 50% 100%);
				    animation: 1s spin linear infinite;
				}
				@keyframes spin {
				    from {
				        --angle: 0deg;
				    }
				    to {
				        --angle: 360deg;
				    }
				}
				.tui-form-input {
				    margin-top: 10px;
				    text-align: left;
				}
				.tui-form-input>label {
				    display: block;
				    width: 100%;
				    text-align: left;
				    vertical-align: top;
				}
				.tui-form-input>input {
				    width: 100%;
				    padding-left: 15px;
				    box-sizing: border-box;
				}
				.tui-form-input>.label-checkbox {
				    display: inline;
				    width: auto;
				}
				.tui-form-input>input[type='checkbox'] {
				    display: inline;
				    width: auto;
				    text-align: left;
				    margin-left: 15px;
				}
				.tui-form-input-invalid {
				    border: 2px solid var(--global-color-red-state);
				}
				.tui-input-error {
				    color: red;
				    font-size: smaller;
				}
				.tui-form-footer {
				    margin-top: 15px;
				}
				.tui-form-message {
				    margin-top: 5px;
				    margin-bottom: 5px;
				}
				.tui-form-close-button, .tui-form-reset-button {
					background: none;
					padding-left: 5px;
					padding-right: 5px;
					border: none;
					color: var(--global-color-background-contrast);
					text-decoration: underline;
					cursor: pointer;
				}
				.tui-form-radio-inline {
					display: inline-block;
				     margin-right: 10px;
				}
				.modal {
				    max-width: 50ch;
				}
				.modal::backdrop {
				    background: var(--global-color-background-contrast);
				    opacity: .5
				}
				
				/* BUTTON */
				
				.tui-refresh-button-container {
					display: inline-block;
				}
				""");

		result.append(button().toCSS("button")).append("\n");
		result.append(submitButton().toCSS("button[type=submit],.tui-modal-form-submit-button,.tui-modal-form-open-button")).append("\n");

		result.append("""
				
				/* NAV BUTTON */
				
				.tui-navbutton {
				    display: inline;
				}
				/* Overriding some style of submit buttons */
				.tui-navbutton>button {
					background-color: var(--global-color-cancel);
					border: 1px solid var(--global-color-border);
				}
				
				/* TABLE */
				
				table {
				    width: 100%;
				    border-collapse: collapse;
				    border-radius: 2px;
				    border: 1px solid var(--global-color-border);
				    margin: 10px 0px 10px 0px;
				}
				th, td {
				    border: 1px solid var(--global-color-border);;
				}
				th {
				    text-align: center;
				    padding: 5px 10px 5px 10px;
				    font-weight: bold;
				}
				td {
				    text-align: left;
				    padding: 2px 10px 2px 10px;
				    vertical-align: middle;
				}
				.tui-hidden-column {
				    display: none;
				}
				.tui-hidden-head {
				    display: none;
				}
				tr:hover {
				    background-color: var(--table-color-row-hover);
				}
				.tui-tablepicker td {
				    cursor: pointer;
				}
				.tui-table-navigation {
				    padding-top: 5px;
				    padding-bottom: 5px;
				}
				.tui-table-navigation>button {
					margin-right: 5px;
				}
				
				/* FETCH ERRORS */
				
				.fetch-error {
				    border: 2px solid var(--global-color-fetch-error);
				}
				.fetch-error-message {
				    font-size: 0.7em;
				    background-color: var(--global-color-fetch-error);
				    color: var(--global-color-fetch-error-contrast);
				}""");

		return result.toString();
	}

	/**
	 * Defines main theme colors as global variables.
	 */
	private void appendGlobalVariables(StringBuilder cssContentBuilder) {
		cssContentBuilder.append(":root{\n");
		appendGlobalColor(cssContentBuilder, "global-color-background", Color.WHITE);
		appendGlobalColor(cssContentBuilder, "global-color-text", m_globalColors.text);
		appendGlobalColor(cssContentBuilder, "global-color-border", m_globalColors.borders);
		appendGlobalColor(cssContentBuilder, "global-color-action", m_globalColors.action);
		appendGlobalColor(cssContentBuilder, "global-color-cancel", m_globalColors.cancel);
		appendGlobalColor(cssContentBuilder, "global-color-delete", m_globalColors.delete);
		appendGlobalColor(cssContentBuilder, "global-color-neutral-state", m_globalColors.neutral);
		appendGlobalColor(cssContentBuilder, "global-color-green-state", m_globalColors.greenState);
		appendGlobalColor(cssContentBuilder, "global-color-red-state", m_globalColors.redState);
		appendGlobalColor(cssContentBuilder, "global-color-fetch-error", Color.ORANGE);
		appendGlobalColor(cssContentBuilder, "table-color-row-hover", m_globalColors.tableRowHover);
		cssContentBuilder.append("}\n");
	}

	private static void appendGlobalColor(StringBuilder cssContentBuilder, String name, Color value) {
		appendGlobalVar(cssContentBuilder, name, toCSSHex(value));
		appendGlobalVar(cssContentBuilder, name + "-contrast", toCSSHex(computeContrastColor(value)));
	}

	private static void appendGlobalVar(StringBuilder cssContentBuilder, String name, String value) {
		cssContentBuilder.append(String.format("--%s: %s;\n", name, value));
	}

	public static String toCSSHex(Color color) {
		String red = Integer.toHexString(color.getRed());
		String green = Integer.toHexString(color.getGreen());
		String blue = Integer.toHexString(color.getBlue());
		return String.format("#%1$2s%2$2s%3$2s", red, green, blue).replace(' ', '0');
	}

	/**
	 * This code has been found <a href="https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color">here</a>
	 */
	public static Color computeContrastColor(Color color) {
		double perceptiveLuminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
		if(perceptiveLuminance > 0.5) {
			return Color.BLACK; // bright colors - black font
		} else {
			return Color.WHITE; // dark colors - white font
		}
	}
}
