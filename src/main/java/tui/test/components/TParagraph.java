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

import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.Paragraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TParagraph extends TRefreshableComponent {

	public static class TText extends TComponent {

		String m_content = null;

		protected TText(long tuid, TClient client) {
			super(tuid, client);
		}

		@Override
		public TComponent find(long tuid) {
			return null;
		}

		@Override
		protected Collection<TComponent> getChildrenComponents() {
			return List.of();
		}

		public static TText parse(JsonMap map, TClient client) {
			final TText result = new TText(-1L, client); // The Text class in Paragraph class does not provide TUID
			result.m_content = map.getAttribute(Paragraph.Text.JSON_ATTRIBUTE_CONTENT);
			return result;
		}
	}

	private String m_source = null;
	private final List<TComponent> m_content = new ArrayList<>();

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TParagraph(long tuid, TClient client) {
		super(tuid, client);
	}

	@Override
	public void update(JsonMap map) {
		final TParagraph paragraph = parse(map, null);
		m_source = paragraph.m_source;
		m_content.clear();
		m_content.addAll(paragraph.m_content);
	}

	public String getText() {
		final StringBuilder result = new StringBuilder();
		m_content.stream()
				.filter((component) -> component instanceof TText)
				.forEach((component) -> result.append(((TText) component).m_content));
		return result.toString();
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	@Override
	protected Collection<TComponent> getChildrenComponents() {
		return List.of();
	}

	public static TParagraph parse(JsonMap map, TClient client) {
		final long tuid = JsonConstants.readTUID(map);
		final TParagraph result = new TParagraph(tuid, client);
		result.readSource(map);

		final JsonArray content = map.getArray(Paragraph.ATTRIBUTE_CONTENT);
		for(int i = 0; i < content.size(); i++) {
			final JsonMap entry = content.getMap(i);
			final TComponent component = TComponentFactory.parse(entry, client);
			result.m_content.add(component);
		}

		return result;
	}
}
