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

import org.jetbrains.annotations.Nullable;
import tui.ui.components.svg.CoordinateTransformation;
import tui.ui.components.svg.SVG;
import tui.ui.components.svg.SVGPath;
import tui.ui.components.svg.SVGPoint;
import tui.ui.components.svg.SVGText;
import tui.ui.components.svg.defs.SVGMarker;

import java.awt.*;
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
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;

public class Axis {

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

	public static Map<Double, String> computeYLabels(long height_px, CoordinateTransformation.Range yValuesRange,
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

	/**
	 * Draws Y-axis and returns the y coordinates of the labelled ticks, relative to <code>axisStart</code>.
	 *
	 * @return The y coordinates of each axis label, expressed as pixels relative to <code>axisStart</code>.
	 */
	public static Set<Integer> drawYAxisWithArrow(SVG svg, CoordinateTransformation.Range yRange, SVGPoint axisStart,
			long height_px, Color color, BiFunction<Double, GridFactor, String> formatter) {
		final SVGMarker endMarker = svg.addMarker(UIGraph.buildArrow(color));

		final SVGPath yAxis = new SVGPath(axisStart.x(), axisStart.y()).lineRelative(0, -height_px);
		yAxis.withStrokeColor(color);
		yAxis.withMarkerAtEnd(endMarker);
		svg.add(yAxis);

		final Map<Double, String> yLabels = computeYLabels(height_px, yRange, formatter);

		final CoordinateTransformation.AffineTransformation yTransform_px = CoordinateTransformation.computeAffineTransformation(
				yRange.min(),
				yRange.max(), 0, height_px);

		final Set<Integer> result = new TreeSet<>();
		for(Map.Entry<Double, String> entry : yLabels.entrySet()) {
			int y_px = (int) yTransform_px.transform(entry.getKey());
			result.add(-y_px);
			svg.add(new SVGPath(axisStart.x() - 4, axisStart.y() - y_px).lineRelative(8, 0).withStrokeColor(color));
			svg.add(new SVGText(axisStart.x() - 10, axisStart.y() - y_px, entry.getValue(), SVGText.Anchor.END)
					.withStrokeColor(color).withFillColor(color));
		}

		return result;
	}

	public static void drawYBooleanAxis(SVG svg, SVGPoint axisStart, int height_px, Color color,
			Function<Boolean, String> formatter) {
		// Vertical line
		svg.add(new SVGPath(axisStart.x(), axisStart.y()).lineRelative(0, -height_px).withStrokeColor(color));

		// bottom tick
		svg.add(new SVGPath(axisStart.x() - 4, axisStart.y()).lineRelative(8, 0).withStrokeColor(color));
		svg.add(new SVGText(axisStart.x() - 10, axisStart.y(), formatter.apply(false), SVGText.Anchor.END)
				.withStrokeColor(color).withFillColor(color));

		// top tick
		svg.add(new SVGPath(axisStart.x() - 4, axisStart.y() - height_px).lineRelative(8, 0).withStrokeColor(color));
		svg.add(new SVGText(axisStart.x() - 10, axisStart.y() - height_px, formatter.apply(true), SVGText.Anchor.END)
				.withStrokeColor(color).withFillColor(color));
	}

	public static int computeLeftMargin_px(Collection<String> yLabels) {
		final Optional<Integer> maxLabelLength = yLabels.stream()
				.map(String::length)
				.max(Integer::compareTo);
		return maxLabelLength.orElse(0) * 10;
	}

	static GridFactor computeGridFactor(long height_px, CoordinateTransformation.Range yValuesRange, double minLabelHeight_px) {
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
		CoordinateTransformation.Range yRange = null;
		for(DataSerie serie : graph.getSeries()) {
			if(yRange == null) {
				yRange = serie.getYRange();
			} else {
				yRange = CoordinateTransformation.getUnion(yRange, serie.getYRange());
			}
		}

		final Map<Double, String> yLabels = computeYLabels(height_px, yRange, formatter);
		yLabels.forEach(graph::addYLabel);
		yLabels.forEach((y, label) -> graph.addYStripe(y));
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

	public static long getDuration_ms(LocalDateTime from, LocalDateTime to) {
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
	private static long estimateDuration_ms(Integer preferredStep, ChronoUnit stepUnit) {
		LocalDateTime t1 = LocalDateTime.now();
		LocalDateTime t2 = t1.plus(preferredStep, stepUnit);
		return getDuration_ms(t1, t2);
	}

	private static TimeRange computeRange(Collection<LocalDateTime> times) {
		return new TimeRange(times.stream().min(LocalDateTime::compareTo).get(),
				times.stream().max(LocalDateTime::compareTo).get());
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
		MS("HH:mm''ss", SECONDS),
		HM("HH:mm", MINUTES),
		DH("dd/MM HH:00", HOURS),
		MD("dd/MM", DAYS),
		YM("MM/yyyy", MONTHS),
		Y("dd/MM/yyyy", YEARS);

		final String pattern;
		final ChronoUnit precision;

		LabelFormat(String format, ChronoUnit precision) {
			this.pattern = format;
			this.precision = precision;
		}

		long width_px() {
			return this.pattern.length() * 9L;
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

	private static LabellingPlan getLabellingPlan(LocalDateTime time, ChronoUnit formatPrecision) {
		LocalDateTime gridStart = truncate(time, formatPrecision);
		return switch(formatPrecision) {
			case MILLIS -> new LabellingPlan(gridStart, MILLIS, List.of(1, 2, 5, 10, 50, 100));
			case SECONDS -> new LabellingPlan(gridStart, SECONDS, List.of(1, 2, 5, 10, 15, 20, 30));
			case MINUTES -> new LabellingPlan(gridStart, MINUTES, List.of(1, 2, 5, 10, 15, 20, 30));
			case HOURS -> new LabellingPlan(gridStart, HOURS, List.of(1, 2, 4, 5, 6, 8, 12));
			case DAYS, WEEKS -> new LabellingPlan(gridStart, DAYS, List.of(1, 2, 7));
			case MONTHS, YEARS -> new LabellingPlan(gridStart, MONTHS, List.of(1, 2, 3, 4, 6));
			case DECADES -> new LabellingPlan(gridStart, DECADES, List.of(1, 2, 5, 10));
			case CENTURIES -> new LabellingPlan(gridStart, CENTURIES, List.of(1, 2, 5, 10));
			default -> new LabellingPlan(gridStart, formatPrecision, List.of(1));
		};
	}

	record TimeFormatter(LabelFormat format, LabellingPlan plan, int step) {
	}

	/**
	 * @param width_px The length of the x-axis in pixels.
	 * @return Each entry of the Map gives the abscissa relative to the start of x-axis (in pixels), and the label.
	 */
	public static Map<Integer, String> computeXTimeLabels(long width_px, TimeRange timeRange) {
		final long range_ms = timeRange.size_ms();
		final long labelMargin_px = 10;

		TimeFormatter optimalFormat = null;
		for(LabelFormat labelFormat : LabelFormat.values()) {
			final LabellingPlan labellingPlan = getLabellingPlan(timeRange.min, labelFormat.precision);
			for(Integer step : labellingPlan.preferredSteps) {
				final long stepDuration_ms = estimateDuration_ms(step, labellingPlan.stepUnit);
				double nbSteps = (double) range_ms / stepDuration_ms;
				final long stepsLabelsWidth_px = (long) (nbSteps * (labelFormat.width_px() + labelMargin_px));
				if(optimalFormat == null && stepsLabelsWidth_px < width_px) {
					optimalFormat = new TimeFormatter(labelFormat, labellingPlan, step);
				}
			}
		}
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(optimalFormat.format().pattern);

		// Computing the labels on x-axis
		final Map<Integer, String> result = new TreeMap<>();
		LocalDateTime time = optimalFormat.plan().gridStart;
		while(time.isBefore(timeRange.max)) {
			if(time.equals(timeRange.min) || time.isAfter(timeRange.min)) {
				int relativeAbscissa_px = (int) ((double) getDuration_ms(timeRange.min, time) * width_px / range_ms);
				String label = time.format(formatter);
				result.put(relativeAbscissa_px, label);
			}
			time = time.plus(optimalFormat.step(), optimalFormat.plan().stepUnit);
		}

		return result;
	}

	public static void addXLabelsAuto(UIGraph graph, int width_px, TimeRange timeRange, CoordinateTransformation.Range xRange) {
		// labels are localised with pixels (relative to x-axis start)
		final Map<Integer, String> labelsOnAxis_px = computeXTimeLabels(width_px, timeRange);

		// Converting relative position in pixels into absolute value
		for(Map.Entry<Integer, String> entry : labelsOnAxis_px.entrySet()) {
			int x_px = entry.getKey();
			double x = xRange.min() + ((double) x_px / width_px) * (xRange.max() - xRange.min());
			graph.addXLabel(x, entry.getValue());
			graph.addXStripe(x);
		}
	}

	public static void drawXAxis(SVG svg, TimeRange timeRange,
			SVGPoint start, int length_px,
			int verticalSpaceForText_px, Color color) {
		drawXAxis(svg, timeRange, start, length_px, verticalSpaceForText_px, color, null);
	}

	/**
	 * @param svg                     Where the x-axis is drawn.
	 * @param timeRange               The time range to be labelled on the whole x-axis.
	 * @param start                   The starting point of the x-axis (left) localised with pixel coordinates in the SVG referential.
	 * @param length_px               The length of the x-axis in the SVG (pixels).
	 * @param verticalSpaceForText_px The space available under the x-axis for the labels.
	 */
	public static void drawXAxisWithArrow(SVG svg, TimeRange timeRange,
			SVGPoint start, long length_px,
			long verticalSpaceForText_px, Color color) {
		final SVGMarker endMarker = svg.addMarker(UIGraph.buildArrow(color));
		drawXAxis(svg, timeRange, start, length_px, verticalSpaceForText_px, color, endMarker);
	}

	private static void drawXAxis(SVG svg, TimeRange timeRange,
			SVGPoint start, long length_px,
			long verticalSpaceForText_px, Color color, @Nullable SVGMarker endMarker) {

		final Map<Integer, String> xAxisLabels = computeXTimeLabels(length_px, timeRange);

		final SVGPath xAxis = new SVGPath(start.x(), start.y()).lineRelative(length_px, 0);
		xAxis.withStrokeColor(color);
		if(endMarker != null) {
			xAxis.withMarkerAtEnd(endMarker);
		}
		svg.add(xAxis);

		for(Map.Entry<Integer, String> entry : xAxisLabels.entrySet()) {
			final long x_px = start.x() + entry.getKey();
			svg.add(new SVGPath(x_px, start.y() - 4).lineRelative(0, 8).withStrokeColor(color));
			svg.add(new SVGText(x_px, start.y() + 4L * verticalSpaceForText_px / 5, entry.getValue(), SVGText.Anchor.MIDDLE)
					.withFontSize_em(1.0f)
					.withStrokeColor(null)
					.withFillColor(color));
		}
	}
}
