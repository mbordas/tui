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

import tui.ui.components.svg.CoordinatesComputer;

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
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

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

public class AxisLabeller {

	public record GridFactor(int powerOfTen, int step) {

		double multiply(double input) {
			return Math.pow(10.0, powerOfTen) * step * input;
		}

		double computeNextY(double inputY) {
			int intValueOfPower = (int) Math.floor(inputY / (Math.pow(10.0, powerOfTen) * step));
			double lowValue = Math.pow(10.0, powerOfTen) * intValueOfPower * step;
			if(inputY > lowValue) {
				return Math.pow(10.0, powerOfTen) * step * (intValueOfPower + 1);
			} else {
				return lowValue;
			}
		}

		GridFactor less() {
			return switch(step) {
				case 5 -> new GridFactor(powerOfTen, 2);
				case 2 -> new GridFactor(powerOfTen, 1);
				case 1 -> new GridFactor(powerOfTen - 1, 5);
				default -> null;
			};
		}

		GridFactor more() {
			return switch(step) {
				case 5 -> new GridFactor(powerOfTen + 1, 1);
				case 2 -> new GridFactor(powerOfTen, 5);
				case 1 -> new GridFactor(powerOfTen, 2);
				default -> null;
			};
		}
	}

	public static Map<Double, String> computeYLabels(int height_px, CoordinatesComputer.Range yValuesRange,
			BiFunction<Double, GridFactor, String> formatter) {
		double minLabelHeight_px = 30;
		final GridFactor gridFactor = computeGridFactor(height_px, yValuesRange, minLabelHeight_px);

		final Map<Double, String> result = new TreeMap<>();
		double y = gridFactor.computeNextY(yValuesRange.min());
		while(y <= yValuesRange.max()) {
			result.put(y, formatter.apply(y, gridFactor));
			y += gridFactor.multiply(1.0);
		}

		return result;
	}

	static GridFactor computeGridFactor(int height_px, CoordinatesComputer.Range yValuesRange, double minLabelHeight_px) {
		GridFactor result = new GridFactor(1, 1);
		if(yValuesRange != null) {
			double range_u = yValuesRange.max() - yValuesRange.min(); // value in raw (and unknown) unit
			double unitHeight_px = (height_px + minLabelHeight_px) / range_u;
			while(result.multiply(unitHeight_px) > minLabelHeight_px) {
				result = result.less();
			}
			while(result.multiply(unitHeight_px) <= minLabelHeight_px) {
				result = result.more();
			}
		}
		return result;
	}

	public static void addYLabelsAuto(UIGraph graph, int height_px, BiFunction<Double, GridFactor, String> formatter) {
		CoordinatesComputer.Range yRange = null;
		for(DataSerie serie : graph.getSeries()) {
			if(yRange == null) {
				yRange = serie.getYRange();
			} else {
				yRange = CoordinatesComputer.getUnion(yRange, serie.getYRange());
			}
		}

		final Map<Double, String> yLabels = computeYLabels(height_px, yRange, formatter);
		yLabels.forEach(graph::addYLabel);
	}

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
	private static long estimateDuration_ms(Integer preferredStep, AxisLabeller.LabellingPlan labellingPlan) {
		LocalDateTime t1 = LocalDateTime.now();
		LocalDateTime t2 = t1.plus(preferredStep, labellingPlan.stepUnit);
		return getDuration_ms(t1, t2);
	}

	private static AxisLabeller.TimeRange computeRange(Collection<LocalDateTime> times) {
		return new AxisLabeller.TimeRange(times.stream().min(LocalDateTime::compareTo).get(),
				times.stream().max(LocalDateTime::compareTo).get());
	}

	public static Collection<UIGraph.Point> toPoints(Collection<AxisLabeller.TimePoint> points) {
		if(points.isEmpty()) {
			return new ArrayList<>();
		}

		final Collection<UIGraph.Point> result = new ArrayList<>();
		final LocalDateTime startTime = computeRange(points.stream().map((point) -> point.x).toList()).min;
		final long start_ms = getMillis(startTime);
		for(AxisLabeller.TimePoint point : points) {
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

	private static AxisLabeller.LabellingPlan computeLabellingPlan(LocalDateTime time, ChronoUnit formatPrecision) {
		LocalDateTime gridStart = truncate(time, formatPrecision);
		return switch(formatPrecision) {
			case MILLIS -> new AxisLabeller.LabellingPlan(gridStart, MILLIS, List.of(1, 2, 5, 10, 50, 100));
			case SECONDS -> new AxisLabeller.LabellingPlan(gridStart, SECONDS, List.of(1, 2, 5, 10, 30));
			case MINUTES -> new AxisLabeller.LabellingPlan(gridStart, MINUTES, List.of(1, 2, 5, 10, 30));
			case HOURS, HALF_DAYS, DAYS -> new AxisLabeller.LabellingPlan(gridStart, DAYS, List.of(1, 7));
			case WEEKS -> new AxisLabeller.LabellingPlan(gridStart, WEEKS, List.of(1, 2));
			case MONTHS, YEARS -> new AxisLabeller.LabellingPlan(gridStart, MONTHS, List.of(1, 2, 3, 4, 6));
			case DECADES -> new AxisLabeller.LabellingPlan(gridStart, DECADES, List.of(1, 2, 5, 10));
			case CENTURIES -> new AxisLabeller.LabellingPlan(gridStart, CENTURIES, List.of(1, 2, 5, 10));
			default -> new AxisLabeller.LabellingPlan(gridStart, formatPrecision, List.of(1));
		};
	}

	public static void addXLabelsAuto(UIGraph graph, int width_px, Collection<AxisLabeller.TimePoint> timePoints) {
		final AxisLabeller.TimeRange timeRange = computeRange(timePoints.stream().map((point) -> point.x).toList());
		addXLabelsAuto(graph, width_px, timeRange);
	}

	public static void addXLabelsAuto(UIGraph graph, int width_px, AxisLabeller.TimeRange timeRange) {
		final long range_ms = timeRange.size_ms();

		// Computing minimum space between 2 labels (time marks)
		final double millisPerPixel = (double) range_ms / width_px;
		final long labelSpaceMin_ms = (long) (100 * millisPerPixel);

		// Choosing the best date format
		AxisLabeller.LabelFormat format = AxisLabeller.LabelFormat.MSL;
		for(AxisLabeller.LabelFormat _format : AxisLabeller.LabelFormat.values()) {
			if(labelSpaceMin_ms >= _format.precision.getDuration().get(ChronoUnit.SECONDS) * 1000L) {
				format = _format;
			} else {
				break;
			}
		}
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.pattern);

		// The labelling plan defines which instants should be marked with label on x-axis
		final AxisLabeller.LabellingPlan labellingPlan = computeLabellingPlan(timeRange.min, format.precision);
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
