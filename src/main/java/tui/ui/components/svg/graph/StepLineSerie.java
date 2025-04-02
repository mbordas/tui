/* Copyright (c) 2025, Mathieu Bordas
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

import tui.ui.components.svg.CoordinateTransformation;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGCircle;
import tui.ui.components.svg.SVGPath;

import java.util.ArrayList;
import java.util.List;

public class StepLineSerie extends DataSerie {

	public record Point(double x, Double y, String label) {
	}

	private final List<Point> m_points = new ArrayList<>();

	public void addPoint(double x, Double y, String label) {
		m_points.add(new Point(x, y, label));
	}

	@Override
	public CoordinateTransformation.Range getXRange() {
		if(m_points.isEmpty()) {
			return null;
		} else {
			return CoordinateTransformation.getRange(m_points.stream().map(Point::x).toList());
		}
	}

	@Override
	public CoordinateTransformation.Range getYRange() {
		if(m_points.isEmpty()) {
			return null;
		} else {
			return CoordinateTransformation.getRange(m_points.stream().filter((point -> point.y != null)).map(Point::y).toList());
		}
	}

	@Override
	public void draw(SVG svg, CoordinateTransformation coordinateTransformation) {
		Point prevPoint = null;
		for(Point point : m_points) {
			if(prevPoint != null && prevPoint.y != null) {
				final long prevX_px = coordinateTransformation.getX_px(prevPoint.x());
				final long prevY_px = coordinateTransformation.getY_px(prevPoint.y());
				final long x_px = coordinateTransformation.getX_px(point.x());
				svg.add(new SVGPath(prevX_px, prevY_px).lineAbsolute(x_px, prevY_px).withStrokeColor(m_color));
			}

			if(point.y != null) {
				final SVGCircle circle = new SVGCircle(coordinateTransformation.getX_px(point.x()),
						coordinateTransformation.getY_px(point.y()), 3);
				circle.withTitle(point.label());
				circle.withStrokeColor(m_color);
				circle.withFillColor(m_color);
				svg.add(circle);

				if(prevPoint != null && prevPoint.y != null) {
					final long prevY_px = coordinateTransformation.getY_px(prevPoint.y());
					final long x_px = coordinateTransformation.getX_px(point.x());
					final long y_px = coordinateTransformation.getY_px(point.y());
					svg.add(new SVGPath(x_px, prevY_px).lineAbsolute(x_px, y_px).withStrokeColor(m_color));
				}
			}

			prevPoint = point;
		}
	}

}
