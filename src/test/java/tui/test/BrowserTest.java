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

package tui.test;

import org.junit.Test;
import tui.html.HTMLConstants;
import tui.http.TUIBackend;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrowserTest {

	@Test
	public void getScriptErrors() throws Exception {
		final int port = 8080;
		try(final TUIBackend backend = new TUIBackend(port);
				final Browser browser = new Browser(port)) {

			final String path = "/index.html";
			final String html = """
					<!DOCTYPE html>
					<html>
					<head>
					  <meta charset="UTF-8">
					  <title>JS Error</title>
					  <script>
					  	setTimeout(()=>{
					  		console.error("console boom");
					  		throw new Error("error boom")
					  		},
					  		300); // Only errors thrown after the page is loaded will be caught.
					  </script>
					</head>
					<body>
					  <h1>JS error test</h1>
					</body>
					</html>
					""";

			backend.registerResourceService(path, html.getBytes(StandardCharsets.UTF_8), HTMLConstants.HTML_CONTENT_TYPE);
			backend.start();

			browser.open(path);
			Thread.sleep(500); // Waiting for page to be loaded and errors to be thrown.

			//
			final List<Browser.ScriptError> scriptErrors = browser.getScriptErrors();
			//

			assertEquals(2, scriptErrors.size());
			assertTrue(scriptErrors.stream().anyMatch((error) -> error.message().equals("console boom")));
			assertTrue(scriptErrors.stream().anyMatch((error) -> error.message().equals("Error: error boom")));
		}
	}

}