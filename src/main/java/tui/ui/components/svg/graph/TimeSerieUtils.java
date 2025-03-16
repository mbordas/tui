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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

public class TimeSerieUtils {

	public record TimePoint(LocalDateTime x, Double y, String label) {
	}

	public record TimeRange(LocalDateTime min, LocalDateTime max) {
		long size_ms() {
			return getDuration_ms(min, max);
		}
	}

	private static long getMillis(LocalDateTime time) {
		ZonedDateTime zdt = ZonedDateTime.of(time, ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}

	private static long getDuration_ms(LocalDateTime from, LocalDateTime to) {
		return getMillis(to) - getMillis(from);
	}

	public static long getDuration_ms(ChronoUnit unit) {
		final Duration duration = unit.getDuration();
		return duration.getSeconds() * 1_000 + duration.getNano() / 1_000_000;
	}

	/**
	 * {@link Duration#of(long, TemporalUnit)} doesn't work with unit larger or equal to {@link ChronoUnit#DAYS} because it would be an
	 * 'estimation'. Indeed, the value computed in this method is not accurate, but it will do the job for what is expected in this class.
	 */
	private static long estimateDuration_ms(Integer preferredStep, LabellingPlan labellingPlan) {
		LocalDateTime t1 = LocalDateTime.now();
		LocalDateTime t2 = t1.plus(preferredStep, labellingPlan.stepUnit);
		return getDuration_ms(t1, t2);
	}

	private static TimeRange computeRange(Collection<LocalDateTime> times) {
		return new TimeRange(times.stream().min(LocalDateTime::compareTo).get(), times.stream().max(LocalDateTime::compareTo).get());
	}

	public static Collection<UIGraph.Point> toPoints(Collection<TimePoint> points) {
		if(points.isEmpty()) {
			return new ArrayList<>();
		}

		final Collection<UIGraph.Point> result = new ArrayList<>();
		final LocalDateTime startTime = computeRange(points.stream().map((point) -> point.x).toList()).min;
		final long start_ms = getMillis(startTime);
		for(TimePoint point : points) {
			long x = getMillis(point.x) - start_ms;
			result.add(new UIGraph.Point(x, point.y, point.label));
		}
		return result;
	}

	private enum LabelFormat {
		MSL("ss''''SSS", MILLIS),
		MS("HH:mm''ss", ChronoUnit.SECONDS),
		HM("HH:mm", ChronoUnit.MINUTES),
		DH("dd/MM HH:00", ChronoUnit.HOURS),
		MD("dd/MM", DAYS),
		YM("MM/yyyy", ChronoUnit.MONTHS),
		Y("dd/MM/yyyy", YEARS);

		final String pattern;
		final ChronoUnit precision;

		LabelFormat(String format, ChronoUnit precision) {
			this.pattern = format;
			this.precision = precision;
		}
	}

	private static LocalDateTime truncate(LocalDateTime time, ChronoUnit formatPrecision) {
		return switch(formatPrecision) {
			case MILLIS -> time.truncatedTo(SECONDS);
			case SECONDS -> time.truncatedTo(MINUTES);
			case MINUTES -> time.truncatedTo(HOURS);
			case HOURS, HALF_DAYS, DAYS -> time.truncatedTo(DAYS);
			case WEEKS -> LocalDateTime.of(time.getYear(), time.getMonth(), 1, 0, 0);
			case MONTHS, YEARS -> LocalDateTime.of(time.getYear(), 1, 1, 0, 0);
			default -> time;
		};
	}

	/**
	 * @param gridStart      The closest time in the past that correspond to the times we want to be labelled on the x-axis.
	 * @param stepUnit       Defines the duration between two consecutive time marks.
	 * @param preferredSteps Gives the best options for dividing the 'stepUnit'.
	 */
	private record LabellingPlan(LocalDateTime gridStart, ChronoUnit stepUnit, Collection<Integer> preferredSteps) {
	}

	private static LabellingPlan computeLabellingPlan(LocalDateTime time, ChronoUnit formatPrecision) {
		LocalDateTime gridStart = truncate(time, formatPrecision);
		return switch(formatPrecision) {
			case MILLIS -> new LabellingPlan(gridStart, MILLIS, List.of(1, 2, 5, 10, 50, 100));
			case SECONDS -> new LabellingPlan(gridStart, SECONDS, List.of(1, 2, 5, 10, 30));
			case MINUTES -> new LabellingPlan(gridStart, MINUTES, List.of(1, 2, 5, 10, 30));
			case HOURS, HALF_DAYS, DAYS -> new LabellingPlan(gridStart, DAYS, List.of(1, 7));
			case WEEKS -> new LabellingPlan(gridStart, WEEKS, List.of(1, 2));
			case MONTHS, YEARS -> new LabellingPlan(gridStart, MONTHS, List.of(1, 2, 3, 4, 6));
			case DECADES -> new LabellingPlan(gridStart, DECADES, List.of(1, 2, 5, 10));
			case CENTURIES -> new LabellingPlan(gridStart, CENTURIES, List.of(1, 2, 5, 10));
			default -> new LabellingPlan(gridStart, formatPrecision, List.of(1));
		};
	}

	public static void addXLabels(UIGraph graph, int width_px, Collection<TimePoint> timePoints) {
		final TimeRange timeRange = computeRange(timePoints.stream().map((point) -> point.x).toList());
		addXLabels(graph, width_px, timeRange);
	}

	public static void addXLabels(UIGraph graph, int width_px, TimeRange timeRange) {
		final long range_ms = timeRange.size_ms();

		// Computing minimum space between 2 labels (time marks)
		final double millisPerPixel = (double) range_ms / width_px;
		final long labelSpaceMin_ms = (long) (100 * millisPerPixel);

		// Choosing the best date format
		LabelFormat format = LabelFormat.MSL;
		for(LabelFormat _format : LabelFormat.values()) {
			if(labelSpaceMin_ms >= _format.precision.getDuration().get(ChronoUnit.SECONDS) * 1000L) {
				format = _format;
			} else {
				break;
			}
		}
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.pattern);

		// The labelling plan defines which instants should be marked with label on x-axis
		final LabellingPlan labellingPlan = computeLabellingPlan(timeRange.min, format.precision);
		Integer stepFactor = 1;
		for(Integer preferredStep : labellingPlan.preferredSteps) {
			final long duration_ms = estimateDuration_ms(preferredStep, labellingPlan);
			if(duration_ms >= labelSpaceMin_ms) {
				stepFactor = preferredStep;
				break;
			}
		}

		// Drawing the labels on x-axis
		LocalDateTime time = labellingPlan.gridStart;
		while(time.isBefore(timeRange.max)) {
			if(time.equals(timeRange.min) || time.isAfter(timeRange.min)) {
				double x = (double) getDuration_ms(timeRange.min, time);
				String label = time.format(formatter);
				graph.addXLabel(x, label);
			}
			time = time.plus(stepFactor, labellingPlan.stepUnit);
		}
	}

}
