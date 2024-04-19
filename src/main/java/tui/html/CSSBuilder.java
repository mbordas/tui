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

		final Style.ButtonStyle buttonStyle = style.getButtonActionColor();
		appendGlobalColor(result, "button-border-color", buttonStyle.border());
		appendGlobalColor(result, "button-action-color", buttonStyle.action());
		appendGlobalColor(result, "button-cancel-color", buttonStyle.cancel());
		appendGlobalColor(result, "button-hover-color", buttonStyle.hover());
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
