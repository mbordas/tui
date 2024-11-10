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

package tui.ui.components;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import tui.html.HTMLConstants;
import tui.test.Browser;
import tui.test.TestWithBackend;
import tui.ui.components.layout.Panel;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ImageTest extends TestWithBackend {

	/**
	 * Displays an image in a {@link Panel} then refreshes the panel so that the image changes its source, alternative text and style.
	 */
	@Test
	public void load() throws IOException {
		final Page page = new Page("Image refresh", "/index");

		final String pathImage1 = "/image/Rene_Descartes.jpg";
		final String pathImage2 = "/image/Isaac_Newton.jpg";

		final RefreshButton refreshButton = page.append(new RefreshButton("Update image"));

		final Panel panel = page.append(new Panel());
		panel.setSource("/image/panel");
		refreshButton.connectListener(panel);
		final Image image = panel.append(new Image(pathImage1, "Refreshable image"));// initial image on page
		image.customStyle().setHeight_px(200);

		startBackend(page);
		m_backend.registerResourceFileService(pathImage1,
				"images/390px-Frans_Hals_-_Portret_van_René_Descartes.jpg", HTMLConstants.JPG_CONTENT_TYPE);
		m_backend.registerResourceFileService(pathImage2,
				"images/GodfreyKneller-IsaacNewton-1689.jpg", HTMLConstants.JPG_CONTENT_TYPE);
		m_backend.registerWebService(panel.getSource(), (uri, request, response) -> {
			final Panel result = new Panel();
			final Image image2 = result.append(new Image(pathImage2, "Updated image"));
			image2.customStyle().setWidth_px(150);
			return result.toJsonMap();
		});

		final Browser browser = startBrowser();
		browser.open(page.getSource());

		// Checking initial image
		{
			final List<WebElement> images = browser.getImages();
			assertEquals(1, images.size());
			final WebElement imageElement = images.get(0);
			assertEquals(String.format("http://localhost:%d%s", m_backend.getPort(), pathImage1), imageElement.getAttribute("src"));
			assertEquals("Refreshable image", imageElement.getAttribute("alt"));
			assertEquals("height: 200px;", imageElement.getAttribute("style"));
		}

		browser.clickRefreshButton(refreshButton.getLabel());
		wait_s(0.05);

		// Checking the new image in the refreshed panel
		{
			final List<WebElement> images = browser.getImages();
			assertEquals(1, images.size());
			final WebElement imageElement = images.get(0);
			assertEquals(String.format("http://localhost:%d%s", m_backend.getPort(), pathImage2), imageElement.getAttribute("src"));
			assertEquals("Updated image", imageElement.getAttribute("alt"));
			assertEquals("width: 150px;", imageElement.getAttribute("style"));
		}
	}

}