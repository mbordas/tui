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
import tui.html.HTMLConstants;
import tui.json.JsonObject;
import tui.ui.Style;
import tui.ui.components.Page;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TUIBackend implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(TUIBackend.class);

	public static final String PATH_TO_CSS = "/css/tui.css";
	public static final String PATH_TO_SCRIPT = "/js/tui.js";

	private Server m_server;
	private Style m_style = new Style();
	private int m_httpPort;

	private final Map<String, TUIWebService> m_webServices = new HashMap<>();
	private final Map<String, TUIPageService> m_pageServices = new HashMap<>();
	private final Map<String, TUIFileService> m_fileServices = new HashMap<>();

	public TUIBackend() {
	}

	public TUIBackend(int port) {
		m_httpPort = port;
	}

	public int getPort() {
		return m_httpPort;
	}

	public String sourceToURI(String source) {
		return String.format("http://localhost:%d%s", getPort(), source);
	}

	public void start(int port) throws Exception {
		m_httpPort = port;
		start();
	}

	public void start() throws Exception {
		m_server = new Server(m_httpPort);
		m_server.setHandler(new AbstractHandler() {
			@Override
			public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response)
					throws IOException {
				final String uri = request.getRequestURI();
				LOG.info("URI: {}", uri);

				if(m_fileServices.containsKey(uri)) {
					try {
						final TUIFileService fileService = m_fileServices.get(uri);
						fileService.handle(uri, request, response);
						response.setStatus(200);
						request.setHandled(true);
					} catch(Throwable t) {
						LOG.error(t.getMessage(), t);
						response.setStatus(500);
						request.setHandled(true);
					}
				} else if(m_pageServices.containsKey(uri)) {
					final TUIPageService pageService = m_pageServices.get(uri);
					try {
						final String format = httpServletRequest.getParameter("format");
						final Page page = pageService.handle(uri, request);
						if(page == null) {
							throw new FileNotFoundException("No page found at: " + uri);
						}
						if("json".equals(format)) {
							final String json = page.toJsonMap().toJson();
							response.setContentType(HTMLConstants.JSON_CONTENT_TYPE);
							response.getWriter().write(json);
						} else {
							final String html = page.toHTMLNode(new Page.Resource(true, PATH_TO_CSS),
									new Page.Resource(true, PATH_TO_SCRIPT)).toHTML();
							response.setContentType(HTMLConstants.HTML_CONTENT_TYPE);
							response.getWriter().write(html);
						}

						response.setStatus(200);
						request.setHandled(true);
					} catch(Throwable t) {
						LOG.error(t.getMessage(), t);
						response.setStatus(500);
						request.setHandled(true);
					}
				} else if(m_webServices.containsKey(uri)) {
					final TUIWebService webService = m_webServices.get(uri);
					try {
						final JsonObject node = webService.handle(uri, request, response);
						final String json = node.toJson();
						response.setContentType(HTMLConstants.JSON_CONTENT_TYPE);
						response.getWriter().write(json);
						response.setStatus(200);
						request.setHandled(true);
					} catch(Throwable t) {
						LOG.error(t.getMessage(), t);
						response.setStatus(500);
						request.setHandled(true);
					}
				} else if(PATH_TO_SCRIPT.equals(uri)) {
					respondWithTextResource(request, response, "js/tui.js", HTMLConstants.JAVASCRIPT_CONTENT_TYPE);
				} else if(PATH_TO_CSS.equals(uri)) {
					response.setContentType(HTMLConstants.CSS_CONTENT_TYPE);
					response.getWriter().write(m_style.toCSS());
					response.setStatus(200);
					request.setHandled(true);
				} else if("/favicon.ico".equals(uri)) {
					respondWithBinaryResource(request, response, "favicon.ico", HTMLConstants.FAVICON_CONTENT_TYPE);
				} else {
					throw new FileNotFoundException("No page found at: " + uri);
				}
			}

			private void respondWithTextResource(Request request, HttpServletResponse response, String resourcePath, String contentType) {
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

			private void respondWithBinaryResource(Request request, HttpServletResponse response, String resourcePath, String contentType) {
				response.setContentType(contentType);
				try {
					final ServletOutputStream outputStream = response.getOutputStream();
					try(InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
						assert is != null;
						outputStream.write(is.readAllBytes());
					}
					response.setStatus(200);
					request.setHandled(true);
				} catch(IOException e) {
					e.printStackTrace();
					response.setStatus(500);
					request.setHandled(true);
				}
			}
		});
		LOG.info("Starting WebServer @port " + m_httpPort);
		m_server.start();
		LOG.info("Web server listening on :{}", m_httpPort);
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

	public void registerPageService(String path, TUIPageService service) {
		m_pageServices.put(path, service);
	}

	public void setStyle(Style style) {
		m_style = style;
	}

	public void registerResourceFileService(String path, String resourcePath, String contentType) throws IOException {
		m_fileServices.put(path, (uri, request, response) -> {
			try(InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);) {
				try(OutputStream out = response.getOutputStream()) {
					assert input != null;
					input.transferTo(out);
				}
			}
			response.setContentType(contentType);
		});
	}

	public void registerPage(Page page) {
		registerPageService(page.getSource(), (uri, request) -> page);
	}

	@Override
	public void close() throws Exception {
		stop();
	}
}
