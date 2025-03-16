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
import tui.ui.components.svg.CoordinatesComputer;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGCircle;
import tui.ui.components.svg.SVGPath;
import tui.ui.components.svg.SVGRectangle;
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGMarker;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class UIGraph {

	public static int PADDING_px = 30;

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

		final CoordinatesComputer.Range rangeX = computeXRange();
		final CoordinatesComputer.Range rangeY = computeYRange();

		final int leftMargin_px = computeLeftMargin_px();
		final CoordinatesComputer coordinatesComputer =
				new CoordinatesComputer(width_px - leftMargin_px, height_px, PADDING_px, rangeX, rangeY);
		coordinatesComputer.setMarginLeft_px(leftMargin_px);

		if(m_backgroundColor != null) {
			result.add(new SVGRectangle(leftMargin_px + PADDING_px, PADDING_px,
					width_px - leftMargin_px - 2L * PADDING_px, height_px - 2L * PADDING_px)
					.withFillColor(m_backgroundColor)
					.withStrokeOpacity(0.0));
		}

		for(Double stripeX : m_xStripes) {
			final int x_px = coordinatesComputer.getX_px(stripeX);
			result.add(new SVGPath(x_px, PADDING_px)
					.lineRelative(0, height_px - 2L * PADDING_px)
					.withStrokeColor(m_stripesColor));
		}

		for(Double stripeY : m_yStripes) {
			final int y_px = coordinatesComputer.getY_px(stripeY);
			result.add(new SVGPath(leftMargin_px + PADDING_px, y_px)
					.lineRelative(width_px - leftMargin_px - 2L * PADDING_px, y_px)
					.withStrokeColor(m_stripesColor));
		}

		for(DataSerie serie : m_series) {
			serie.draw(result, coordinatesComputer);
		}

		if(addDebugLines) {
			// Global borders
			result.add(new SVGRectangle(1, 1, width_px - 2, height_px - 2)
					.withStrokeColor(Color.MAGENTA)
					.withFillOpacity(0.0));
		}

		final SVGMarker arrowMarker = result.addMarker(buildArrow());
		drawXAxis(coordinatesComputer, rangeX, rangeY, arrowMarker, result, addDebugLines);
		drawYAxis(coordinatesComputer, leftMargin_px, rangeX, rangeY, arrowMarker, result);

		return result;
	}

	private int computeLeftMargin_px() {
		final Optional<Integer> maxLabelLength = m_yLabels.values().stream()
				.map(String::length)
				.max(Integer::compareTo);
		return maxLabelLength.orElse(0) * 10;
	}

	private void drawYAxis(CoordinatesComputer coordinatesComputer, int leftMargin_px, CoordinatesComputer.Range rangeX,
			CoordinatesComputer.Range rangeY, SVGMarker endMarker, SVG result) {
		final SVGPath yAxis = new SVGPath(coordinatesComputer.getX_px(rangeX.min()), coordinatesComputer.getY_px(rangeY.min()))
				.lineAbsolute(coordinatesComputer.getX_px(rangeX.min()), coordinatesComputer.getY_px(rangeY.max()));
		if(m_drawArrowsOnAxis) {
			yAxis.withMarkerAtEnd(endMarker);
		}
		yAxis.withStrokeColor(m_axisColor);
		result.add(yAxis);
		for(Map.Entry<Double, String> entry : m_yLabels.entrySet()) {
			final int y_px = coordinatesComputer.getY_px(entry.getKey());
			result.add(new SVGPath(leftMargin_px + PADDING_px - 4, y_px).lineRelative(8, 0).withStrokeColor(m_axisColor));
			result.add(new SVGText(leftMargin_px + PADDING_px - 10, y_px, entry.getValue(), SVGText.Anchor.END)
					.withStrokeColor(m_axisColor)
					.withFillColor(m_axisColor));
		}
		for(double y : m_yDivisions) {
			final int y_px = coordinatesComputer.getY_px(y);
			result.add(new SVGPath(leftMargin_px + PADDING_px - 2, y_px).lineRelative(4, 0).withStrokeColor(m_axisColor));
		}
	}

	private void drawXAxis(CoordinatesComputer coordinatesComputer, CoordinatesComputer.Range rangeX, CoordinatesComputer.Range rangeY,
			SVGMarker endMarker, SVG result, boolean addDebugLines) {

		final int leftX_px = coordinatesComputer.getX_px(rangeX.min());
		final int axisY_px = coordinatesComputer.getY_px(rangeY.min());

		final SVGPath xAxis = new SVGPath(leftX_px, axisY_px).lineAbsolute(coordinatesComputer.getX_px(rangeX.max()), axisY_px);
		if(m_drawArrowsOnAxis) {
			xAxis.withMarkerAtEnd(endMarker);
		}
		xAxis.withStrokeColor(m_axisColor);
		result.add(xAxis);
		for(Map.Entry<Double, String> entry : m_xLabels.entrySet()) {
			final int x_px = coordinatesComputer.getX_px(entry.getKey());
			result.add(new SVGPath(x_px, axisY_px - 4).lineRelative(0, 8).withStrokeColor(m_axisColor));
			result.add(new SVGText(x_px, axisY_px + 4 * PADDING_px / 5, entry.getValue(), SVGText.Anchor.MIDDLE)
					.withFontSize_em(1.0f)
					.withStrokeColor(m_axisColor)
					.withFillColor(m_axisColor));
		}
		for(double x : m_xDivisions) {
			final int x_px = coordinatesComputer.getX_px(x);
			result.add(new SVGPath(x_px, axisY_px - 2).lineRelative(0, 4).withStrokeColor(m_axisColor));
		}

		if(addDebugLines) {
			final int rightX_px = coordinatesComputer.getX_px(rangeX.max());
			result.add(new SVGRectangle(leftX_px, axisY_px, rightX_px - leftX_px, PADDING_px)
					.withStrokeColor(Color.MAGENTA)
					.withFillOpacity(0.0));
		}
	}

	private CoordinatesComputer.Range computeXRange() {
		CoordinatesComputer.Range xSerieRange = null;
		for(DataSerie serie : m_series) {
			final CoordinatesComputer.Range xRange = serie.getXRange();
			if(xSerieRange == null) {
				xSerieRange = xRange;
			} else {
				xSerieRange = CoordinatesComputer.getUnion(xSerieRange, xRange);
			}
		}
		if(xSerieRange == null) {
			xSerieRange = new CoordinatesComputer.Range(-1.0, +1.0);
		}

		final CoordinatesComputer.Range xLabelRange = computeRange(m_xLabels.keySet());
		return CoordinatesComputer.getUnion(xSerieRange, xLabelRange);
	}

	private CoordinatesComputer.Range computeYRange() {
		CoordinatesComputer.Range ySerieRange = null;
		for(DataSerie serie : m_series) {
			final CoordinatesComputer.Range yRange = serie.getYRange();
			if(ySerieRange == null) {
				ySerieRange = yRange;
			} else {
				ySerieRange = CoordinatesComputer.getUnion(ySerieRange, yRange);
			}
		}
		if(ySerieRange == null) {
			ySerieRange = new CoordinatesComputer.Range(-1.0, +1.0);
		}

		final CoordinatesComputer.Range yLabelRange = computeRange(m_yLabels.keySet());
		return CoordinatesComputer.getUnion(ySerieRange, yLabelRange);
	}

	private static CoordinatesComputer.@NotNull Range computeRange(Collection<Double> xValues) {
		if(xValues.isEmpty()) {
			return new CoordinatesComputer.Range(-1.0, 1.0);
		} else if(xValues.size() == 1) {
			final double uniqueValue = xValues.iterator().next();
			return new CoordinatesComputer.Range(uniqueValue - 1.0, uniqueValue + 1.0);
		} else {
			return CoordinatesComputer.getRange(xValues);
		}
	}

	private SVGMarker buildArrow() {
		final SVGMarker result = new SVGMarker("arrow", 10, 8)
				.withRef(0, 4);
		result.add(new SVGPath(0, 1)
				.lineAbsolute(9, 4)
				.lineAbsolute(0, 7)
				.lineAbsolute(0, 1)
				.withStrokeWidth(1)
				.withFillColor(m_axisColor)
				.withStrokeColor(m_axisColor));
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
