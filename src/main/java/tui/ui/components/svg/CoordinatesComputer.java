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

package tui.ui.components.svg;

import tui.ui.components.svg.graph.Axis;

import java.util.Collection;
import java.util.Objects;

public class CoordinatesComputer {

	public record Range(double min, double max) {
	}

	public record Point_px(int x_px, int y_px) {
	}

	// f(x) = a.x + b
	public record AffineTransformation(double a, double b) {
		public double transform(double x) {
			return a * x + b;
		}
	}

	public record TimeToPixelTransformation(java.time.LocalDateTime referenceTime, int origin_px, double pixelPerMinute) {
		public int transform(java.time.LocalDateTime time) {
			final long duration_ms = Axis.getDuration_ms(referenceTime, time);
			double duration_px = pixelPerMinute * duration_ms / 60_000;
			return origin_px + (int) duration_px;
		}
	}

	final int m_width_px;
	final int m_height_px;
	final int m_padding_px; // Applies to top, right and bottom
	int m_paddingLeft_px = 0; // Padding left can be customized in order to adapt to y-axis labels
	final Range m_range_X;

	AffineTransformation m_x_transformation;
	final AffineTransformation m_y_transformation;

	@Deprecated
	public CoordinatesComputer(int width_px, int height_px, int padding_px, Range range_X, Range range_Y) {
		m_range_X = range_X;
		m_width_px = width_px;
		m_height_px = height_px;
		m_padding_px = padding_px;
		m_paddingLeft_px = padding_px;

		m_x_transformation = computeAffineTransformation(m_range_X.min, m_range_X.max, m_paddingLeft_px, m_width_px - m_padding_px);
		m_y_transformation = computeAffineTransformation(range_Y.min, range_Y.max, m_height_px - m_padding_px, m_padding_px);
	}

	public CoordinatesComputer(Point_px topLeft, int width_px, int height_px, Range range_X, Range range_Y) {
		m_range_X = range_X;
		m_width_px = width_px;
		m_height_px = height_px;
		m_padding_px = 0;
		m_paddingLeft_px = 0;

		m_x_transformation = computeAffineTransformation(m_range_X.min, m_range_X.max, topLeft.x_px(), topLeft.x_px() + m_width_px);
		m_y_transformation = computeAffineTransformation(range_Y.min, range_Y.max, topLeft.y_px() + m_height_px, topLeft.y_px());
	}

	public void setPaddingLeft_px(int paddingLeft_px) {
		m_paddingLeft_px = paddingLeft_px;
		m_x_transformation = computeAffineTransformation(m_range_X.min, m_range_X.max, m_paddingLeft_px, m_width_px - m_padding_px);
	}

	public int getX_px(double x) {
		return (int) m_x_transformation.transform(x);
	}

	public int getY_px(double y) {
		return (int) m_y_transformation.transform(y);
	}

	public static Range getRange(Collection<Double> values) {
		assert !values.isEmpty();
		Double min = null;
		Double max = null;
		for(double value : values.stream().filter(Objects::nonNull).toList()) {
			min = min == null ? value : Math.min(min, value);
			max = max == null ? value : Math.max(max, value);
		}
		return new Range(min, max);
	}

	public static Range getUnion(Range rangeA, Range rangeB) {
		double min = Math.min(rangeA.min(), rangeB.min());
		double max = Math.max(rangeA.max(), rangeB.max());
		return new Range(min, max);
	}

	public static AffineTransformation computeAffineTransformation(double srcMin, double srcMax, double dstMin, double dstMax) {
		double a = (dstMax - dstMin) / (srcMax - srcMin);
		double b = dstMin + srcMin * ((dstMin - dstMax) / (srcMax - srcMin));
		return new AffineTransformation(a, b);
	}

}
