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

package tui.html;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.ui.Style;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CSSBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CSSBuilder.class);

	public static String toCSS(Style style) {
		final StringBuilder result = new StringBuilder();

		result.append(":root{\n");

		final Style.GlobalColors globalStyle = style.getGlobalColors();
		appendGlobalColor(result, "global-color-background", Color.WHITE);

		appendGlobalColor(result, "global-color-text", globalStyle.text());
		appendGlobalColor(result, "global-color-border", globalStyle.borders());
		appendGlobalColor(result, "global-color-action", globalStyle.action());
		appendGlobalColor(result, "global-color-cancel", globalStyle.cancel());
		appendGlobalColor(result, "global-color-delete", globalStyle.delete());

		appendGlobalColor(result, "global-color-neutral-state", globalStyle.neutralState());
		appendGlobalColor(result, "global-color-green-state", globalStyle.greenState());
		appendGlobalColor(result, "global-color-red-state", globalStyle.redState());

		appendGlobalColor(result, "global-color-fetch-error", Color.ORANGE);

		result.append("\n");
		final Style.TableColors tableStyle = style.getTableStyle();
		appendGlobalColor(result, "table-color-row-hover", tableStyle.rowHover());

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
		appendGlobalVar(builder, name, toCSS(value));
		appendGlobalVar(builder, name + "-contrast", toCSS(computeContrastColor(value)));
	}

	private static void appendGlobalVar(StringBuilder builder, String name, String value) {
		builder.append(String.format("--%s: %s;\n", name, value));
	}

	static String toCSS(Color color) {
		String red = Integer.toHexString(color.getRed());
		String green = Integer.toHexString(color.getGreen());
		String blue = Integer.toHexString(color.getBlue());
		return String.format("#%1$2s%2$2s%3$2s", red, green, blue).replace(' ', '0');
	}

	/**
	 * This code has been found here: https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
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
