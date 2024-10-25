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

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Style {

	private static final Logger LOG = LoggerFactory.getLogger(Style.class);

	public record GlobalColors(Color text, Color borders, Color action, Color cancel, Color delete,
							   Color neutralState, Color greenState, Color redState) {
	}

	private final GlobalColors m_globalColors = new GlobalColors(
			new Color(46, 46, 46), // text
			new Color(180, 180, 180), // borders
			new Color(0, 198, 252), // action
			new Color(222, 222, 222), // cancel
			new Color(252, 40, 3), // delete / rollback
			new Color(230, 230, 230), // neutral state
			new Color(115, 250, 70), // green state
			new Color(252, 40, 3) // red state
	);

	public record TableColors(Color rowHover) {
	}

	private final TableColors m_tableStyle = new TableColors(
			new Color(192, 240, 252)
	);

	public Style() {
	}

	public GlobalColors getGlobalColors() {
		return m_globalColors;
	}

	public TableColors getTableStyle() {
		return m_tableStyle;
	}

	public record Padding(int top_px, int right_px, int bottom_px, int left_px) {
	}

	public record Margin(int top_px, int right_px, int bottom_px, int left_px) {
	}

	public String toCSS() {
		final StringBuilder result = new StringBuilder();

		result.append(":root{\n");

		appendGlobalColor(result, "global-color-background", Color.WHITE);

		appendGlobalColor(result, "global-color-text", m_globalColors.text());
		appendGlobalColor(result, "global-color-border", m_globalColors.borders());
		appendGlobalColor(result, "global-color-action", m_globalColors.action());
		appendGlobalColor(result, "global-color-cancel", m_globalColors.cancel());
		appendGlobalColor(result, "global-color-delete", m_globalColors.delete());

		appendGlobalColor(result, "global-color-neutral-state", m_globalColors.neutralState());
		appendGlobalColor(result, "global-color-green-state", m_globalColors.greenState());
		appendGlobalColor(result, "global-color-red-state", m_globalColors.redState());

		appendGlobalColor(result, "global-color-fetch-error", Color.ORANGE);

		result.append("\n");
		appendGlobalColor(result, "table-color-row-hover", m_tableStyle.rowHover());

		result.append("}\n");

		try {
			result.append(getResourceFileContent("css/tui.css"));
		} catch(IOException e) {
			LOG.error(String.format("Error creating CSS: %s", e.getMessage()), e);
			return "";
		}
		return result.toString();
	}

	private static void appendGlobalColor(StringBuilder builder, String name, Color value) {
		appendGlobalVar(builder, name, toCSSHex(value));
		appendGlobalVar(builder, name + "-contrast", toCSSHex(computeContrastColor(value)));
	}

	private static void appendGlobalVar(StringBuilder builder, String name, String value) {
		builder.append(String.format("--%s: %s;\n", name, value));
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
	static Color computeContrastColor(Color color) {
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
