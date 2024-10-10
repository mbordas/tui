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

package tui.ui.components.svg;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.HTMLNode;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.UI;
import tui.ui.components.Page;
import tui.ui.components.RefreshButton;
import tui.ui.components.form.Form;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class SVGTest extends TestWithBackend {

	private static final Logger LOG = LoggerFactory.getLogger(SVGTest.class);

	@Test
	public void oneRectangle() {
		final SVG svg = new SVG(20, 20);
		svg.add(new SVGRectangle(5, 10, 15, 20));

		HTMLNode.PRETTY_PRINT = true;
		assertEquals("""
				<svg width="20" height="20">
				  <rect x="5" y="10" width="15" height="20" rx="0" ry="0" style="stroke:#000000;stroke-width:1;stroke-opacity:1.00;fill:#000000;fill-opacity:1.00;"/>
				</svg>
				""", svg.toHTMLNode().toHTML());
	}

	@Test
	public void refreshError() {
		final SVG svg = new SVG(200, 100);
		svg.setSource("/svg");
		final RefreshButton refreshButton = new RefreshButton("Refresh SVG");
		refreshButton.connectListener(svg);

		final Page page = new Page("index", "/index");
		page.append(refreshButton);
		page.append(svg);
		startAndBrowse(page);

		final AtomicInteger calls = new AtomicInteger(0);
		m_backend.registerWebService(svg.getSource(), (uri, request, response) -> {
			calls.getAndIncrement();
			svg.add(new SVGRectangle(calls.get(), calls.get(), 10 + calls.get(), 10 + calls.get()));
			return svg.toJsonMap();
		});

		m_browser.clickRefreshButton(refreshButton.getLabel());
		wait_s(2.0);

		final List<WebElement> svgs = m_browser.getSVGs();
		assertEquals(1, svgs.size());
		assertEquals(1, svgs.get(0).findElements(By.tagName("rect")).size());

		m_backend.registerWebService(svg.getSource(), (uri, request, response) -> {
			throw new IOException("Custom exception message");
		});

		m_browser.clickRefreshButton(refreshButton.getLabel());
		wait_s(1.0);

		assertEquals("HTTP error, status = 500", m_browser.getErrorMessage(svgs.get(0)));
	}

	public static void main(String[] args) throws Exception {
		final UI ui = new UI();
		final Page page = new Page("Home", "/index");

		final Form form = new Form("New rectangle", "/addRectangle");
		form.createInputNumber("x", "x");
		form.createInputNumber("y", "y");
		form.createInputNumber("width", "width");
		form.createInputNumber("height", "height");

		final SVG svg = new SVG(200, 200);
		svg.setSource("/getSVG");
		form.registerRefreshListener(svg);
		page.append(form);

		RefreshButton refreshButton = new RefreshButton("Refresh graph");
		refreshButton.connectListener(svg);
		page.append(refreshButton);

		svg.add(new SVGPath(50, 50)
				.lineRelative(100, 10)
				.lineRelative(0, 80)
				.close()
				.withStrokeColor(Color.BLUE)
				.withFillColor(Color.BLUE)
				.withFillOpacity(0.5));
		page.append(svg);

		ui.add(page);
		ui.setHTTPBackend("localhost", 8080);

		final TUIBackend backend = new TUIBackend(ui);

		backend.registerWebService(form.getTarget(), (uri, request, response) -> {
			final RequestReader reader = new RequestReader(request);
			final int x = reader.getIntegerParameter("x");
			final int y = reader.getIntegerParameter("y");
			final int width = reader.getIntegerParameter("width");
			final int height = reader.getIntegerParameter("height");
			svg.add(new SVGRectangle(x, y, width, height).withFillColor(Color.ORANGE).withFillOpacity(0.5));
			return Form.getSuccessfulSubmissionResponse();
		});

		backend.registerWebService(svg.getSource(), (uri, request, response) -> svg.toJsonMap());

		backend.start();

		final Browser browser = new Browser(backend.getPort());
		browser.open("/index");
	}

}