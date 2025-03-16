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

import junit.framework.TestCase;
import tui.ui.components.Paragraph;
import tui.ui.components.layout.Panel;
import tui.utils.TestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

public class TimeSerieUtilsTest extends TestCase {

	static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

	static Collection<TimeSerieUtils.TimePoint> generatePoints(int width, ChronoUnit unit) {
		final long unitDuration_ms = TimeSerieUtils.getDuration_ms(unit);
		final long unitDurationMin_ms = TimeSerieUtils.getDuration_ms(ChronoUnit.MILLIS);
		final long unitDurationMax_ms = TimeSerieUtils.getDuration_ms(ChronoUnit.DECADES);

		if(unitDuration_ms < unitDurationMin_ms || unitDuration_ms > unitDurationMax_ms) {
			return null;
		}
		Collection<TimeSerieUtils.TimePoint> timePoints = new ArrayList<>();
		final LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = now.minus(width / 2, unit);
		for(int i = 0; i < 10; i++) {
			LocalDateTime x = from.plus((long) i * width, unit);
			timePoints.add(new TimeSerieUtils.TimePoint(x, Math.random() * 100.0, x.format(FORMATTER)));
		}
		return timePoints;
	}

	public static void main(String[] args) throws Exception {

		final Panel panel = new Panel(Panel.Align.VERTICAL);

		for(ChronoUnit unit : ChronoUnit.values()) {
			panel.append(new Paragraph.Text("Unit = " + unit.name()));

			Collection<TimeSerieUtils.TimePoint> timePoints = generatePoints((int) (20 * Math.random()), unit);
			if(timePoints == null) {
				continue;
			}

			final Collection<UIGraph.Point> points = TimeSerieUtils.toPoints(timePoints);
			final LineSerie serie = new LineSerie();
			serie.addAll(points);

			UIGraph graph = new UIGraph();
			graph.add(serie);
			TimeSerieUtils.addXLabels(graph, 800, timePoints);
			panel.append(graph.toSVG(800, 500));
		}

		TestUtils.quickShow(panel);
	}

}