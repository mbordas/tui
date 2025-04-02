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

package tui.ui.components.svg.graph;

import org.jetbrains.annotations.NotNull;
import tui.ui.components.svg.CoordinateTransformation;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGCircle;
import tui.ui.components.svg.SVGPath;
import tui.ui.components.svg.SVGPoint;
import tui.ui.components.svg.SVGRectangle;
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGMarker;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UIGraph {

	public static long PADDING_px = 30;

	public record Point(double x, double y, String label) {
	}

	private Color m_backgroundColor = null;
	private Color m_axisColor = Color.BLACK;
	private Color m_stripesColor = Color.LIGHT_GRAY;

	private List<DataSerie> m_series = new ArrayList<>();

	private final Map<Double, String> m_xLabels = new TreeMap<>();
	private final List<Double> m_xStripes = new ArrayList<>();
	private final List<Double> m_xDivisions = new ArrayList<>();

	private final Map<Double, String> m_yLabels = new TreeMap<>();
	private final List<Double> m_yStripes = new ArrayList<>();
	private final List<Double> m_yDivisions = new ArrayList<>();

	private boolean m_drawArrowsOnAxis = false;

	public void add(DataSerie serie) {
		m_series.add(serie);
	}

	List<DataSerie> getSeries() {
		return m_series;
	}

	public void addXLabel(double x, String label) {
		m_xLabels.put(x, label);
	}

	public void addXDivision(double x) {
		m_xDivisions.add(x);
	}

	public void addXStripe(double x) {
		m_xStripes.add(x);
	}

	public void addYLabel(double y, String label) {
		m_yLabels.put(y, label);
	}

	public void addYDivision(double y) {
		m_yDivisions.add(y);
	}

	public void addYStripe(double y) {
		m_yStripes.add(y);
	}

	public UIGraph withBackgroundColor(Color color) {
		m_backgroundColor = color;
		return this;
	}

	public UIGraph withStripeColor(@NotNull Color color) {
		m_stripesColor = color;
		return this;
	}

	public UIGraph withAxisColor(Color color) {
		m_axisColor = color;
		return this;
	}

	public UIGraph withArrowOnAxis(boolean enabled) {
		m_drawArrowsOnAxis = enabled;
		return this;
	}

	public SVG toSVG(int width_px, int height_px) {
		return toSVG(width_px, height_px, false);
	}

	public SVG toDebugSVG(int width_px, int height_px) {
		return toSVG(width_px, height_px, true);
	}

	private SVG toSVG(int width_px, int height_px, boolean addDebugLines) {
		final SVG result = new SVG(width_px, height_px);

		final CoordinateTransformation.Range rangeX = computeXRange();
		final CoordinateTransformation.Range rangeY = computeYRange();

		final int leftMargin_px = Axis.computeLeftMargin_px(m_yLabels.values());
		final SVGPoint topLeft = new SVGPoint(leftMargin_px, PADDING_px);
		final long drawingWidth_px = width_px - leftMargin_px - PADDING_px;
		final long drawingHeight_px = height_px - 2 * PADDING_px;
		final CoordinateTransformation coordinateTransformation =
				new CoordinateTransformation(topLeft, drawingWidth_px, drawingHeight_px, rangeX, rangeY);

		if(m_backgroundColor != null) {
			result.add(new SVGRectangle(leftMargin_px + PADDING_px, PADDING_px,
					width_px - leftMargin_px - 2L * PADDING_px, height_px - 2L * PADDING_px)
					.withFillColor(m_backgroundColor)
					.withStrokeOpacity(0.0));
		}

		for(Double stripeX : m_xStripes) {
			final long x_px = coordinateTransformation.getX_px(stripeX);
			result.add(new SVGPath(x_px, PADDING_px)
					.lineRelative(0, height_px - 2L * PADDING_px)
					.withStrokeColor(m_stripesColor));
		}

		for(Double stripeY : m_yStripes) {
			final long y_px = coordinateTransformation.getY_px(stripeY);
			result.add(new SVGPath(leftMargin_px + PADDING_px, y_px)
					.lineRelative(width_px - leftMargin_px - 2L * PADDING_px, 0)
					.withStrokeColor(m_stripesColor));
		}

		for(DataSerie serie : m_series) {
			serie.draw(result, coordinateTransformation);
		}

		if(addDebugLines) {
			// Global borders
			result.add(new SVGRectangle(1, 1, width_px - 2, height_px - 2)
					.withStrokeColor(Color.MAGENTA)
					.withFillOpacity(0.0));
		}

		final SVGMarker arrowMarker = result.addMarker(buildArrow());
		drawXAxis(coordinateTransformation, rangeX, rangeY, arrowMarker, result, addDebugLines);
		drawYAxis(coordinateTransformation, leftMargin_px, rangeX, rangeY, arrowMarker, result);

		return result;
	}

	private void drawYAxis(CoordinateTransformation coordinateTransformation, int leftMargin_px, CoordinateTransformation.Range rangeX,
			CoordinateTransformation.Range rangeY, SVGMarker endMarker, SVG result) {
		final SVGPath yAxis = new SVGPath(coordinateTransformation.getX_px(rangeX.min()), coordinateTransformation.getY_px(rangeY.min()))
				.lineAbsolute(coordinateTransformation.getX_px(rangeX.min()), coordinateTransformation.getY_px(rangeY.max()));
		if(m_drawArrowsOnAxis) {
			yAxis.withMarkerAtEnd(endMarker);
		}
		yAxis.withStrokeColor(m_axisColor);
		result.add(yAxis);
		for(Map.Entry<Double, String> entry : m_yLabels.entrySet()) {
			final long y_px = coordinateTransformation.getY_px(entry.getKey());
			result.add(new SVGPath(leftMargin_px + PADDING_px - 4, y_px).lineRelative(8, 0).withStrokeColor(m_axisColor));
			result.add(new SVGText(leftMargin_px + PADDING_px - 10, y_px, entry.getValue(), SVGText.Anchor.END)
					.withStrokeColor(m_axisColor)
					.withFillColor(m_axisColor));
		}
		for(double y : m_yDivisions) {
			final long y_px = coordinateTransformation.getY_px(y);
			result.add(new SVGPath(leftMargin_px + PADDING_px - 2, y_px).lineRelative(4, 0).withStrokeColor(m_axisColor));
		}
	}

	private void drawXAxis(CoordinateTransformation coordinateTransformation, CoordinateTransformation.Range rangeX,
			CoordinateTransformation.Range rangeY,
			SVGMarker endMarker, SVG result, boolean addDebugLines) {

		final long leftX_px = coordinateTransformation.getX_px(rangeX.min());
		final long axisY_px = coordinateTransformation.getY_px(rangeY.min());

		final SVGPath xAxis = new SVGPath(leftX_px, axisY_px).lineAbsolute(coordinateTransformation.getX_px(rangeX.max()), axisY_px);
		if(m_drawArrowsOnAxis) {
			xAxis.withMarkerAtEnd(endMarker);
		}
		xAxis.withStrokeColor(m_axisColor);
		result.add(xAxis);
		for(Map.Entry<Double, String> entry : m_xLabels.entrySet()) {
			final long x_px = coordinateTransformation.getX_px(entry.getKey());
			result.add(new SVGPath(x_px, axisY_px - 4).lineRelative(0, 8).withStrokeColor(m_axisColor));
			result.add(new SVGText(x_px, axisY_px + 4 * PADDING_px / 5, entry.getValue(), SVGText.Anchor.MIDDLE)
					.withFontSize_em(1.0f)
					.withStrokeColor(m_axisColor)
					.withFillColor(m_axisColor));
		}
		for(double x : m_xDivisions) {
			final long x_px = coordinateTransformation.getX_px(x);
			result.add(new SVGPath(x_px, axisY_px - 2).lineRelative(0, 4).withStrokeColor(m_axisColor));
		}

		if(addDebugLines) {
			final long rightX_px = coordinateTransformation.getX_px(rangeX.max());
			result.add(new SVGRectangle(leftX_px, axisY_px, rightX_px - leftX_px, PADDING_px)
					.withStrokeColor(Color.MAGENTA)
					.withFillOpacity(0.0));
		}
	}

	private CoordinateTransformation.Range computeXRange() {
		CoordinateTransformation.Range xSerieRange = null;
		for(DataSerie serie : m_series) {
			final CoordinateTransformation.Range xRange = serie.getXRange();
			if(xSerieRange == null) {
				xSerieRange = xRange;
			} else {
				xSerieRange = CoordinateTransformation.getUnion(xSerieRange, xRange);
			}
		}
		if(xSerieRange == null) {
			xSerieRange = new CoordinateTransformation.Range(-1.0, +1.0);
		}

		final CoordinateTransformation.Range xLabelRange = computeRange(m_xLabels.keySet());
		return CoordinateTransformation.getUnion(xSerieRange, xLabelRange);
	}

	private CoordinateTransformation.Range computeYRange() {
		CoordinateTransformation.Range ySerieRange = null;
		for(DataSerie serie : m_series) {
			final CoordinateTransformation.Range yRange = serie.getYRange();
			if(ySerieRange == null) {
				ySerieRange = yRange;
			} else {
				ySerieRange = CoordinateTransformation.getUnion(ySerieRange, yRange);
			}
		}
		if(ySerieRange == null) {
			ySerieRange = new CoordinateTransformation.Range(-1.0, +1.0);
		}

		final CoordinateTransformation.Range yLabelRange = computeRange(m_yLabels.keySet());
		return CoordinateTransformation.getUnion(ySerieRange, yLabelRange);
	}

	private static CoordinateTransformation.@NotNull Range computeRange(Collection<Double> xValues) {
		if(xValues.isEmpty()) {
			return new CoordinateTransformation.Range(-1.0, 1.0);
		} else if(xValues.size() == 1) {
			final double uniqueValue = xValues.iterator().next();
			return new CoordinateTransformation.Range(uniqueValue - 1.0, uniqueValue + 1.0);
		} else {
			return CoordinateTransformation.getRange(xValues);
		}
	}

	private SVGMarker buildArrow() {
		return buildArrow(m_axisColor);
	}

	public static SVGMarker buildArrow(Color color) {
		final SVGMarker result = new SVGMarker("arrow", 10, 8)
				.withRef(0, 4);
		result.add(new SVGPath(0, 1)
				.lineAbsolute(9, 4)
				.lineAbsolute(0, 7)
				.lineAbsolute(0, 1)
				.withStrokeWidth(1)
				.withFillColor(color)
				.withStrokeColor(color));
		return result;
	}

	private SVGMarker buildPoint() {
		final SVGMarker result = new SVGMarker("point", 6, 6)
				.withRef(3, 3);
		result.add(new SVGCircle(3, 3, 2)
				.withStrokeWidth(1)
				.withFillColor(Color.BLACK));
		return result;
	}
}
