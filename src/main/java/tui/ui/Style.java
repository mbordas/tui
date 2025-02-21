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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.ui.components.layout.Layouts;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Style {

	private static final Logger LOG = LoggerFactory.getLogger(Style.class);

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
	private final StyleSet m_header = new StyleSet();
	private final StyleSet m_footer = new StyleSet();
	private final StyleSet m_paragraph = new StyleSet();

	public Style() {
		m_header.setBackgroundColor("var(--global-color-background)");
		m_header.setTextColor("var(--global-color-background-contrast)");
		m_header.setPadding(10, 10, 10, 10);

		m_footer.setBackgroundColor("var(--global-color-background)");
		m_footer.setTextColor("var(--global-color-background-contrast)");
		m_footer.setPadding(10, 10, 10, 10);
		m_footer.setBorderWidth_px(1, 0, 0, 0);

		m_paragraph.setTextAlign(Layouts.TextAlign.LEFT);
		m_paragraph.setMargin(0, 0, 10, 0);
		m_paragraph.setPadding(0, 0, 0, 0);
	}

	public void setColorForAction(Color color) {
		m_globalColors.action = color;
	}

	public void setColorForTableRowHover(Color color) {
		m_globalColors.tableRowHover = color;
	}

	public StyleSet header() {
		return m_header;
	}

	public StyleSet footer() {
		return m_footer;
	}

	public StyleSet paragraph() {
		return m_paragraph;
	}

	public String toCSS() {
		final StringBuilder result = new StringBuilder();

		appendGlobalVariables(result);

		result.append("""
				* {
				    color: var(--global-color-text);
				}
				
				body {
				    min-height: 100vh;
				    padding: 0px;
				    margin: 0px;
				    font-family: Arial, sans-serif;
				}
				
				main {
				    /* justify-self: stretch; */
				}
				
				h1 {
					padding-left: 0px;
					font-size: 2em;
				}
				
				h2 {
					padding-left: 0px;
					font-size: 1.5em;
				}
				
				h3 {
					padding-left: 0px;
					font-size: 1.2em;
					font-weight: lighter;
				}
				
				h4 {
					padding-left: 0px;
					font-size: 1.1em;
					font-style: italic;
					font-weight: lighter;
				}
				
				a {
					color: var(--global-color-action);
				}
				
				header {
				""");
		result.append(m_header.toCSS()).append("\n");
		result.append("""
				    vertical-align: middle;
				    border-bottom: solid 1px;
				}
				header a {
				    text-align: center;
				    text-decoration: none;
				}
				header button[type='submit'] {
				      background: none;
				      padding-left: 5px;
				      padding-right: 5px;
				      border: none;
				      text-decoration: underline;
				      cursor: pointer;
				}
				
				footer {
				""");
		result.append(m_footer.toCSS()).append("\n");
		result.append("""
				}
				
				section {
					text-align: left;
					margin-top: 30px;
				}
				
				p {
				""");
		result.append(m_paragraph.toCSS());
		result.append("""
				 				}
				
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
				    /* background: linear-gradient(var(--angle), var(--global-color-background) 0%, var(--global-color-background) 50%, var(--global-color-action) 100%); */
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
				
				button {
				    border-radius: 2px;
				    padding: 5px 20px 5px 20px;
				    text-align: center;
				    cursor: pointer;
				    background-color: var(--global-color-cancel);
				    color: var(--global-color-cancel-contrast);
				    border: 1px solid var(--global-color-border);
				}
				button[type=submit],.tui-modal-form-submit-button,.tui-modal-form-open-button {
				    background-color: var(--global-color-action);
				    color: var(--global-color-action-contrast);
				    border-color: var(--global-color-action);
				}
				
				/* NAV BUTTON */
				
				.tui-navbutton {
				    display: inline;
				}
				/* Overriding some style of submit buttons */
				.tui-navbutton>button {
					background-color: var(--global-color-cancel);
					border: 1px solid var(--global-color-border);
				}
				
				/* MONITORING */
				
				.tui-monitor-fieldset {
				    border-radius: 2px;
				    display: grid;
				    border: 1px solid var(--global-color-border);
				}
				.tui-monitor-field {
				}
				.tui-monitor-field-label {
				    display: inline-block;
				    width: 150px;
				    text-align: right;
				    padding-left: 10px;
				    padding-right: 10px;
				    margin: 2px;
				}
				.tui-monitor-field-value {
				    display: inline-block;
				    width: 80px;
				    text-align: center;
				    padding-left: 10px;
				    padding-right: 10px;
				    margin: 1px;
				    border: 1px solid var(--global-color-border);
				}
				.tui-monitor-field-value-neutral {
				    background-color: var(--global-color-neutral-state);
				    color: var(--global-color-neutral-state-contrast);
				}
				.tui-monitor-field-value-green {
				    background-color: var(--global-color-green-state);
				    color: var(--global-color-green-state-contrast);
				}
				.tui-monitor-field-value-red {
				    background-color: var(--global-color-red-state);
				    color: var(--global-color-red-state-contrast);
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

	private static String getResourceFileContent(String resourcePath) throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
		String result = null;
		if(is != null) {
			result = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			is.close();
		}
		return result;
	}
}
