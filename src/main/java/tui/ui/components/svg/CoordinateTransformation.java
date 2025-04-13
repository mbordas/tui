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

/**
 * This class transforms x,y coordinates from any 2D referential to another 2D referential. It is mainly used to convert coordinates from
 * a user space (using job-specific units) to  a pixel-based referential for drawing into an SVG.
 */
public class CoordinateTransformation {

	public record Range(double min, double max) {
	}

	// f(x) = a.x + b
	public record AffineTransformation(double a, double b) {
		public double transform(double x) {
			return a * x + b;
		}
	}

	public record TimeToPixelTransformation(java.time.LocalDateTime originTime, long origin_px, double pixelPerMinute) {
		public long transform(java.time.LocalDateTime time) {
			final long duration_ms = Axis.getDuration_ms(originTime, time);
			double duration_px = pixelPerMinute * duration_ms / 60_000;
			return origin_px + (long) duration_px;
		}
	}

	final AffineTransformation m_x_transformation;
	final AffineTransformation m_y_transformation;

	public CoordinateTransformation(SVGPoint topLeft, long width_px, long height_px, Range range_X, Range range_Y) {
		m_x_transformation = computeAffineTransformation(range_X.min, range_X.max, topLeft.x(), topLeft.x() + width_px);
		m_y_transformation = computeAffineTransformation(range_Y.min, range_Y.max, topLeft.y() + height_px, topLeft.y());
	}

	public long getX_px(double x) {
		return (long) m_x_transformation.transform(x);
	}

	public long getY_px(double y) {
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
