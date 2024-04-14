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

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Map;

public class TestHTTPClient {

	public static final int DEFAULT_CONNECTION_TIMEOUT_ms = 19_000;
	public static final int DEFAULT_SOCKET_READ_TIMEOUT_ms = 39_000;
	public static final int DEFAULT_KEEP_ALIVE_ms = 5_000;

	private final CloseableHttpClient m_httpClient;
	private final ResponseHandler m_responseHandler = new ResponseHandler();
	private final String m_host;
	private final int m_port;

	public TestHTTPClient(String host, int port) {
		m_host = host;
		m_port = port;
		final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		final SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
		final CredentialsProvider credentialsProvider = new SystemDefaultCredentialsProvider();
		final RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT_ms)
				.setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT_ms)
				.setSocketTimeout(DEFAULT_SOCKET_READ_TIMEOUT_ms)
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		final HttpClientBuilder clientBuilder = HttpClientBuilder.create()
				.setConnectionManager(connectionManager)
				.setRoutePlanner(routePlanner)
				.setDefaultCredentialsProvider(credentialsProvider)
				.setDefaultRequestConfig(requestConfig)
				.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
					@Override
					public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
						final long defaultResult = super.getKeepAliveDuration(response, context);
						return defaultResult != -1L ? defaultResult : DEFAULT_KEEP_ALIVE_ms * 1000L;
					}
				})
				.disableAutomaticRetries();

		m_httpClient = clientBuilder.build();
	}

	public void setBasicAuthentication(CredentialsProvider credentialsProvider, String host, int port, String login, String password) {
		final AuthScope authScope = new AuthScope(host, port);
		final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
		credentialsProvider.setCredentials(authScope, credentials);
	}

	private static class ResponseHandler extends BasicResponseHandler {

		private int m_lastStatusCode;

		ResponseHandler() {
		}

		@Override
		public String handleResponse(HttpResponse response) throws IOException {
			ContentType contentType = ContentType.getOrDefault(response.getEntity());
			String result = null;
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				result = EntityUtils.toString(entity, contentType.getCharset());
			}
			m_lastStatusCode = response.getStatusLine().getStatusCode();
			return result;
		}

		public int getLastStatusCode() {
			return m_lastStatusCode;
		}
	}

	public String callBackend(String target, Map<String, Object> parameters) throws HttpException {
		final String uri = String.format("http://%s:%d/%s", m_host, m_port,
				target.startsWith("/") ? target.substring(1) : target);
		final HttpPost httpRequest = new HttpPost(uri);

		final String result;
		try {
			if(parameters != null) {
				final ArrayList<NameValuePair> postParameters = new ArrayList<>();
				for(Map.Entry<String, Object> entry : parameters.entrySet()) {
					postParameters.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
				}
				httpRequest.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			}
			result = m_httpClient.execute(httpRequest, m_responseHandler);
		} catch(IOException e) {
			throw new HttpException(e.getMessage(), e);
		} finally {
			httpRequest.releaseConnection();
		}
		if(m_responseHandler.getLastStatusCode() < 200 || m_responseHandler.getLastStatusCode() >= 300) {
			throw new HttpException(String.format("HTTP error %d", m_responseHandler.getLastStatusCode()));
		}
		return result;
	}
}
