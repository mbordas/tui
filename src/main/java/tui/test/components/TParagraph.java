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

package tui.test.components;

import org.apache.http.HttpException;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonParser;
import tui.test.TClient;
import tui.ui.components.Paragraph;

import java.util.Map;

public class TParagraph extends TRefreshableComponent {

	private String m_source;
	private String m_text;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TParagraph(long tuid, TClient client, String source) {
		super(tuid, client);
		m_source = source;
	}

	@Override
	public void refresh(Map<String, Object> data) throws HttpException {
		final String response = m_client.callBackend(m_source, data);
		final JsonMap map = JsonParser.parseMap(response);
		m_text = map.getAttribute(Paragraph.ATTRIBUTE_TEXT);
	}

	public String getText() {
		return m_text;
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	public static TParagraph parse(JsonMap map, TClient client) {
		final long tuid = JsonConstants.readTUID(map);
		final String source = map.getAttribute(Paragraph.ATTRIBUTE_SOURCE);

		final TParagraph result = new TParagraph(tuid, client, source);
		result.m_text = map.getAttribute(Paragraph.ATTRIBUTE_TEXT);
		return result;
	}
}
