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

package tui.http;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tui.html.CSSBuilder;
import tui.html.HTMLConstants;
import tui.json.JsonObject;
import tui.ui.Page;
import tui.ui.TUI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TUIBackend {

	private static final Logger LOG = LoggerFactory.getLogger(TUIBackend.class);

	public static final String PATH_TO_CSS = "/css/tui.css";
	public static final String PATH_TO_SCRIPT = "/js/tui.js";
	public static final String SCRIPT_ONLOAD_FUNCTION_CALL = "onload()";

	private final TUI m_ui;
	private Server m_server;

	private final Map<String, TUIWebService> m_webServices = new HashMap<>();

	public TUIBackend(TUI ui) {
		m_ui = ui;
	}

	public void start() throws Exception {
		int port = m_ui.getHTTPPort();
		m_server = new Server(port);
		m_server.setHandler(new AbstractHandler() {
			@Override
			public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response)
					throws IOException {
				final String uri = request.getRequestURI();
				LOG.info("URI: {}", uri);

				if(m_webServices.containsKey(uri)) {
					final TUIWebService webService = m_webServices.get(uri);
					final JsonObject node = webService.handle(uri, request, response);
					final String json = node.toJson();
					response.setContentType(HTMLConstants.JSON_CONTENT_TYPE);
					response.getWriter().write(json);
					response.setStatus(200);
					request.setHandled(true);
				} else if(PATH_TO_SCRIPT.equals(uri)) {
					respondWithResource(request, response, "js/tui.js", HTMLConstants.JAVASCRIPT_CONTENT_TYPE);
				} else if(PATH_TO_CSS.equals(uri)) {
					response.setContentType(HTMLConstants.CSS_CONTENT_TYPE);
					response.getWriter().write(CSSBuilder.toCSS(m_ui.getStyle()));
					response.setStatus(200);
					request.setHandled(true);
				} else {
					final Page defaultPage = m_ui.getDefaultPage();
					final String html = defaultPage.toHTMLNode(PATH_TO_CSS, PATH_TO_SCRIPT, SCRIPT_ONLOAD_FUNCTION_CALL).toHTML();

					response.setContentType(HTMLConstants.HTML_CONTENT_TYPE);
					response.getWriter().write(html);
					response.setStatus(200);
					request.setHandled(true);
				}
			}

			private void respondWithResource(Request request, HttpServletResponse response, String resourcePath, String contentType) {
				response.setContentType(contentType);
				try {
					final String content;
					try(InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
						assert is != null;
						content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
					}
					response.getWriter().write(content);
					response.setStatus(200);
					request.setHandled(true);
				} catch(IOException e) {
					e.printStackTrace();
					response.setStatus(500);
					request.setHandled(true);
				}
			}
		});
		LOG.info("Starting WebServer @port " + port);
		m_server.start();
		LOG.info("Web server listening on :{}", port);
	}

	public void stop() throws Exception {
		LOG.info("Stopping web server...");
		if(m_server != null) {
			try {
				m_server.stop();
			} catch(Exception t) {
				t.printStackTrace();
				throw t;
			}
		}
		LOG.info("Web server stopped");
	}

	public void registerWebService(String path, TUIWebService service) {
		m_webServices.put(path, service);
	}

}
