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
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGMarker;

import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class UIGraph {

	record Point(double x, double y, String label) {
	}

	private Color m_axisColor = Color.BLACK;

	private LineSerie m_serie = new LineSerie();

	private final Map<Double, String> m_xLabels = new TreeMap<>();
	private final Map<Double, String> m_yLabels = new TreeMap<>();
	private boolean m_drawArrowsOnAxis = false;

	public void addPoint(double x, double y) {
		m_serie.addPoint(x, y, null);
	}

	public void addPoint(double x, double y, String label) {
		m_serie.addPoint(x, y, label);
	}

	public void addXLabel(double x, String label) {
		m_xLabels.put(x, label);
	}

	public void addYLabel(double y, String label) {
		m_yLabels.put(y, label);
	}

	public UIGraph withSerieColor(Color color) {
		m_serie.setColor(color);
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
		final int padding_px = 30;
		final SVG result = new SVG(width_px, height_px);
		final SVGMarker arrowMarker = result.addMarker(buildArrow());

		final CoordinatesComputer.Range rangeX = computeXRange();
		final CoordinatesComputer.Range rangeY = computeYRange();
		final CoordinatesComputer coordinatesComputer = new CoordinatesComputer(width_px, height_px, padding_px, rangeX, rangeY);

		m_serie.draw(result, coordinatesComputer);

		drawXAxis(coordinatesComputer, rangeX, rangeY, arrowMarker, result, padding_px);
		drawYAxis(coordinatesComputer, rangeX, rangeY, arrowMarker, result, padding_px);

		return result;
	}

	private void drawYAxis(CoordinatesComputer coordinatesComputer, CoordinatesComputer.Range rangeX, CoordinatesComputer.Range rangeY,
			SVGMarker endMarker, SVG result, int padding_px) {
		final SVGPath yAxis = new SVGPath(coordinatesComputer.getX_px(rangeX.min()), coordinatesComputer.getY_px(rangeY.min()))
				.lineAbsolute(coordinatesComputer.getX_px(rangeX.min()), coordinatesComputer.getY_px(rangeY.max()));
		if(m_drawArrowsOnAxis) {
			yAxis.withMarkerAtEnd(endMarker);
		}
		yAxis.withStrokeColor(m_axisColor);
		result.add(yAxis);
		final int axisX_px = coordinatesComputer.getX_px(rangeX.min());
		for(Map.Entry<Double, String> entry : m_yLabels.entrySet()) {
			final int y_px = coordinatesComputer.getY_px(entry.getKey());
			result.add(new SVGPath(axisX_px - 4, y_px).lineRelative(8, 0).withStrokeColor(m_axisColor));
			result.add(new SVGText(axisX_px - padding_px / 2, y_px, entry.getValue(), SVGText.Anchor.MIDDLE)
					.withStrokeColor(m_axisColor)
					.withFillColor(m_axisColor));
		}
	}

	private void drawXAxis(CoordinatesComputer coordinatesComputer, CoordinatesComputer.Range rangeX, CoordinatesComputer.Range rangeY,
			SVGMarker endMarker, SVG result, int padding_px) {
		final SVGPath xAxis = new SVGPath(coordinatesComputer.getX_px(rangeX.min()), coordinatesComputer.getY_px(rangeY.min()))
				.lineAbsolute(coordinatesComputer.getX_px(rangeX.max()), coordinatesComputer.getY_px(rangeY.min()));
		if(m_drawArrowsOnAxis) {
			xAxis.withMarkerAtEnd(endMarker);
		}
		xAxis.withStrokeColor(m_axisColor);
		result.add(xAxis);
		final int axisY_px = coordinatesComputer.getY_px(rangeY.min());
		for(Map.Entry<Double, String> entry : m_xLabels.entrySet()) {
			final int x_px = coordinatesComputer.getX_px(entry.getKey());
			result.add(new SVGPath(x_px, axisY_px - 4).lineRelative(0, 8).withStrokeColor(m_axisColor));
			result.add(new SVGText(x_px, axisY_px + padding_px, entry.getValue(), SVGText.Anchor.MIDDLE)
					.withStrokeColor(m_axisColor)
					.withFillColor(m_axisColor));
		}
	}

	private CoordinatesComputer.Range computeXRange() {
		final CoordinatesComputer.Range xSerieRange = m_serie.getXRange();
		final CoordinatesComputer.Range xLabelRange = computeRange(m_xLabels.keySet());
		return CoordinatesComputer.getUnion(xSerieRange, xLabelRange);
	}

	private CoordinatesComputer.Range computeYRange() {
		final CoordinatesComputer.Range ySerieRange = m_serie.getYRange();
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
