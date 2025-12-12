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

package tui.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TUIColors {

	/**
	 * @param hue        Hue in degrees 0 to 360.
	 * @param saturation Saturation in percent 0 to 100.
	 * @param lightness  Lightness in percent 0 to 100.
	 */
	public record ColorHSL(int hue, int saturation, int lightness) {
		public Color toRGB() {
			return TUIColors.toRGB(this);
		}

		/**
		 * Computes a color with an increased lightness.
		 *
		 * @param percent The distance from current lightness to its maximum (100) will be reduced by this amount.
		 */
		public ColorHSL lighter(int percent) {
			return new ColorHSL(hue, saturation, lightness + percent * (100 - lightness) / 100);
		}
	}

	/**
	 * Source: https://www.rapidtables.com/convert/color/hsl-to-rgb.html
	 */
	public static Color toRGB(ColorHSL hsl) {
		float h = hsl.hue % 360.0f;
		float s = hsl.saturation / 100f;
		float l = hsl.lightness / 100f;

		float c = (1 - Math.abs(2 * l - 1)) * s;
		float x = c * (1 - Math.abs((h / 60) % 2 - 1));
		float m = l - c / 2f;

		float r, g, b;
		if(h < 60f) {
			r = c;
			g = x;
			b = 0f;
		} else if(h < 120f) {
			r = x;
			g = c;
			b = 0f;
		} else if(h < 180) {
			r = 0f;
			g = c;
			b = x;
		} else if(h < 240f) {
			r = 0f;
			g = x;
			b = c;
		} else if(h < 300f) {
			r = x;
			g = 0f;
			b = c;
		} else {
			r = c;
			g = 0f;
			b = x;
		}
		return new Color((int) ((r + m) * 255f), (int) ((g + m) * 255f), (int) ((b + m) * 255f));
	}

	/**
	 * Source: https://www.rapidtables.com/convert/color/rgb-to-hsl.html
	 */
	public static ColorHSL toHSL(Color color) {
		float r = (float) color.getRed() / 255;
		float g = (float) color.getGreen() / 255;
		float b = (float) color.getBlue() / 255;
		float cmax = Math.max(r, Math.max(g, b));
		float cmin = Math.min(r, Math.min(g, b));
		float delta = cmax - cmin;
		float hue;
		if(delta == 0) {
			hue = 0;
		} else if(cmax == r) {
			hue = ((60 * (g - b) / delta) + 360) % 360;
		} else if(cmax == g) {
			hue = (60 * (b - r) / delta) + 120;
		} else {
			hue = (60 * (r - g) / delta) + 240;
		}
		double lightness = (cmax + cmin) / 2;
		double saturation = delta / (1 - Math.abs(2 * lightness - 1));
		return new ColorHSL((int) hue, (int) (saturation * 100.0), (int) (lightness * 100.0));
	}

	/**
	 * Computes interpolated colors from 'start' (included) to 'end' (included). Hue, Saturation and Lightness are interpolated.
	 */
	public static List<ColorHSL> palette(ColorHSL start, ColorHSL end, int size) {
		assert size >= 2;
		final List<ColorHSL> result = new ArrayList<>();

		int hueEnd = end.hue >= start.hue ? end.hue : end.hue + 360;
		float hueStep = (float) (hueEnd - start.hue) / (size - 1);

		for(int i = 0; i < size; i++) {
			final float hue = (start.hue + (i * hueStep)) % 360;
			final float saturation = (float) start.saturation + (float) (i * (end.saturation - start.saturation)) / (size - 1);
			final float lightness = (float) start.lightness + (float) (i * (end.lightness - start.lightness)) / (size - 1);
			result.add(new ColorHSL((int) hue, (int) saturation, (int) lightness));
		}

		return result;
	}

	public static String toCSSHex(@NotNull TUIColors.ColorHSL color) {
		return toCSSHex(color.toRGB());
	}

	public static String toCSSHex(@NotNull Color color) {
		String red = Integer.toHexString(color.getRed());
		String green = Integer.toHexString(color.getGreen());
		String blue = Integer.toHexString(color.getBlue());
		return String.format("#%1$2s%2$2s%3$2s", red, green, blue).replace(' ', '0');
	}

	public static String toCSSRGBAsSelenium(Color color) {
		return String.format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue());
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

	public static Color computeContrastColor(ColorHSL color) {
		return computeContrastColor(color.toRGB());
	}
}
