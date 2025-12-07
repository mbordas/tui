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

package tui.test.components;

import org.junit.Test;
import tui.html.HTMLConstants;
import tui.http.RequestReader;
import tui.http.TUIBackend;
import tui.test.TClient;
import tui.test.TestWithBackend;
import tui.ui.components.DownloadButton;
import tui.ui.components.Page;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class TDownloadButtonTest extends TestWithBackend {

	/**
	 * Here the backend generates a text file using the value of one parameter. This ensures that the parameter is
	 * well sent to the backend.
	 */
	@Test
	public void downloadWithParameters() {
		final Page page = new Page("Home", "/index");
		final String target = "/target";
		final DownloadButton downloadButton = page.append(new DownloadButton("Label", target, "default_file_name.css"));
		downloadButton.setParameter("param1", "value1");

		try(final TUIBackend backend = startBackend(page)) {

			backend.registerFileService(target, (uri, request, response) -> {
				final RequestReader reader = new RequestReader(request);
				final String param1 = reader.getStringParameter("param1");
				response.getWriter().write(
						"Value of param1 is:\n" + param1); // We build the content of the file with the value of the parameter
				response.setContentType(HTMLConstants.CSS_CONTENT_TYPE);
			});

			final TClient client = new TClient(backend.getPort());
			client.open(page.getSource());

			final TDownloadButton button = client.getDownloadButton("Label");

			assertEquals(target, button.getTarget());
			assertEquals("default_file_name.css", button.getDefaultFileName());
			assertEquals("value1", button.getParameter("param1"));

			final File outputDir = new File("target/test-classes/TDownloadButtonTest");
			outputDir.mkdirs();

			final File downloadedFile = button.downloadIntoDir(outputDir);
			assertEquals("default_file_name.css", downloadedFile.getName());
			assertEquals("Value of param1 is:\nvalue1", Files.readString(downloadedFile.toPath()));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}