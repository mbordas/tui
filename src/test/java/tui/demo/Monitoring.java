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

package tui.demo;

import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.ui.components.Page;
import tui.ui.components.Paragraph;
import tui.ui.components.RefreshButton;
import tui.ui.components.layout.Grid;
import tui.ui.components.layout.Layouts;
import tui.ui.components.layout.Panel;
import tui.ui.components.layout.VerticalScroll;
import tui.ui.components.monitoring.MonitorField;
import tui.ui.components.monitoring.MonitorFieldGreenRed;
import tui.ui.components.monitoring.MonitorFieldSet;
import tui.utils.TestUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitoring {

	static Map<String, Double> m_citiesHealthScore_pc = new TreeMap<>();

	private static void updateRandomScores() {
		for(String city : TestUtils.CITIES) {
			m_citiesHealthScore_pc.put(city, Math.random() * 100.0);
		}
	}

	static TreeSet<MonitorFieldGreenRed> computeFields() {
		final TreeSet<MonitorFieldGreenRed> result = new TreeSet<>();
		for(Map.Entry<String, Double> entry : m_citiesHealthScore_pc.entrySet()) {
			final String city = entry.getKey();
			final double score_pc = entry.getValue();
			final MonitorFieldGreenRed field = new MonitorFieldGreenRed(city, city);
			field.displayLabel(false);
			final String scoreStr = String.format("%.0f %%", score_pc);
			if(score_pc < 20.0) {
				field.set(MonitorFieldGreenRed.Value.RED, scoreStr);
			} else if(score_pc > 60.0) {
				field.set(MonitorFieldGreenRed.Value.GREEN, scoreStr);
			} else {
				field.set(MonitorFieldGreenRed.Value.NEUTRAL, scoreStr);
			}
			result.add(field);
		}

		return result;
	}

	static MonitorFieldGreenRed emptyField() {
		final MonitorFieldGreenRed result = new MonitorFieldGreenRed("", "");
		result.setText("-");
		result.displayLabel(false);
		return result;
	}

	private static RefreshButton viewButton(MonitorFieldGreenRed field, Paragraph paragraph) {
		RefreshButton result = new RefreshButton("view");
		result.setKey(field.getName());
		result.connectListener(paragraph);
		result.customStyle().setPadding(0, 5, 0, 5);
		return result;
	}

	public static void main(String[] args) throws Exception {
		final Page page = new Page("Monitoring", "/index");
		page.setReadingWidth(Layouts.Width.WIDE);

		final Grid mainGrid = new Grid(1, 2);
		mainGrid.setFirstColumnWidth_px(500);
		page.append(mainGrid);

		updateRandomScores();

		final Grid leftGrid = new Grid(m_citiesHealthScore_pc.size(), 4);
		final Panel rightPanel = new Panel();
		final Paragraph rightParagraph = rightPanel.append(new Paragraph());
		rightParagraph.setSource("/monitoring/view");

		final MonitorFieldSet fieldSet = new MonitorFieldSet("");
		fieldSet.setSource("/monitoring/fields");
		fieldSet.setAutoRefreshPeriod_s(2);
		page.append(fieldSet);

		final AtomicInteger row = new AtomicInteger(0);
		computeFields().forEach((field) -> {
			leftGrid.set(row.get(), 0, new Paragraph(field.getName()));
			leftGrid.set(row.get(), 1, field);
			leftGrid.set(row.get(), 2, emptyField());
			leftGrid.set(row.get(), 3, viewButton(field, rightParagraph));
			row.getAndIncrement();
		});

		mainGrid.set(0, 0, new VerticalScroll(800, leftGrid));
		mainGrid.set(0, 1, rightPanel);

		final TUIBackend backend = new TUIBackend(8080);
		backend.registerPage(page);

		backend.registerWebService(fieldSet.getSource(), (uri, request, response) -> {
			updateRandomScores();
			return MonitorField.toJson(computeFields());
		});
		backend.registerWebService(rightParagraph.getSource(), (uri, request, response) -> {
			final RequestReader requestReader = new RequestReader(request);
			final String key = requestReader.getStringParameter(RefreshButton.PARAMETER_NAME);
			final Paragraph result = new Paragraph().appendNormal("here is the view detail for: " + key);
			return result.toJsonMap();
		});

		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}
