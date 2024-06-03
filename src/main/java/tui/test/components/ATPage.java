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

import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.Page;
import tui.ui.components.TabbedPage;

import java.util.Collection;

public abstract class ATPage extends TComponent {

	private final String m_title;

	/**
	 * Returns all the reachable {@link TComponent}s in the first level of {@code this}.
	 */
	public abstract Collection<TComponent> getReachableSubComponents();

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected ATPage(long tuid, String title, TClient client) {
		super(tuid, client);
		m_title = title;
	}

	public String getTitle() {
		return m_title;
	}

	public static ATPage parse(JsonMap map, TClient client) {
		final String type = map.getType();
		if(Page.JSON_TYPE.equals(type)) {
			return TPage.parse(map, client);
		} else if(TabbedPage.JSON_TYPE.equals(type)) {
			return TTabbedPage.parse(map, client);
		} else {
			throw new RuntimeException(String.format("Unsupported type for page: %s", type));
		}
	}

}
