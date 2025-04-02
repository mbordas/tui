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
import java.util.Collection;
import java.util.List;

public class LineSerie extends DataSerie {

	private final Collection<UIGraph.Point> m_points = new ArrayList<>();

	public void addPoint(double x, double y, String label) {
		m_points.add(new UIGraph.Point(x, y, label));
	}

	public void addAll(Collection<UIGraph.Point> points) {
		m_points.addAll(points);
	}

	@Override
	public CoordinateTransformation.Range getXRange() {
		final List<Double> xValues = new ArrayList<>(m_points.stream()
				.map(UIGraph.Point::x)
				.toList());
		return CoordinateTransformation.getRange(xValues);
	}

	@Override
	public CoordinateTransformation.Range getYRange() {
		final List<Double> yValues = new ArrayList<>(m_points.stream()
				.map(UIGraph.Point::y)
				.toList());
		return CoordinateTransformation.getRange(yValues);
	}

	@Override
	public void draw(SVG svg, CoordinateTransformation coordinateTransformation) {
		SVGPath path = null;
		for(UIGraph.Point point : m_points) {
			if(path == null) {
				path = new SVGPath(coordinateTransformation.getX_px(point.x()), coordinateTransformation.getY_px(point.y()));
				path.withStrokeColor(m_color);
				path.withFillOpacity(0.0);
				svg.add(path);
			} else {
				path.lineAbsolute(coordinateTransformation.getX_px(point.x()), coordinateTransformation.getY_px(point.y()));
			}
			final SVGCircle circle = new SVGCircle(coordinateTransformation.getX_px(point.x()), coordinateTransformation.getY_px(point.y()),
					3);
			circle.withTitle(point.label());
			circle.withStrokeColor(m_color);
			circle.withFillColor(m_color);
			svg.add(circle);
		}
	}

}
