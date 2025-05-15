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

import tui.json.JsonArray;
import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.List;
import tui.ui.components.Paragraph;

import java.util.ArrayList;
import java.util.Collection;

public class TList extends TComponent {

	private final boolean m_isOrdered;
	private final java.util.List<TComponent> m_content = new ArrayList<>();

	protected TList(TClient client, boolean isOrdered) {
		super(null, client);
		m_isOrdered = isOrdered;
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	@Override
	public Collection<TComponent> getChildrenComponents() {
		return m_content;
	}

	public static TList parse(JsonMap map, TClient client) {
		final boolean isOrdered = Boolean.parseBoolean(map.getAttribute(List.JSON_ATTRIBUTE_IS_ORDERED));
		final TList result = new TList(client, isOrdered);

		final JsonArray content = map.getArray(Paragraph.ATTRIBUTE_CONTENT);
		for(int i = 0; i < content.size(); i++) {
			final JsonMap entry = content.getMap(i);
			final TComponent component = TComponentFactory.parse(entry, client);
			result.m_content.add(component);
		}

		return result;
	}
}
