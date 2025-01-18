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

import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.test.TClient;

import java.util.Collection;
import java.util.List;

public class TGrid extends TComponent {

	private TComponent[][] m_components;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TGrid(long tuid, TClient client) {
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

	public static TGrid parse(JsonMap jsonMap, TClient tClient) {
		final long tuid = JsonConstants.readTUID(jsonMap);

		final int rows = Integer.parseInt(jsonMap.getAttribute("rows"));
		final int columns = Integer.parseInt(jsonMap.getAttribute("columns"));

		final TGrid result = new TGrid(tuid, tClient);
		result.m_components = new TComponent[rows][columns];

		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				final String childName = String.format("%s_%s", row, column);
				final JsonMap childMap = jsonMap.getMap(childName);
				if(childMap != null) {
					final TComponent childComponent = TComponentFactory.parse(childMap, tClient);
					result.m_components[row][column] = childComponent;
				}
			}
		}

		return result;
	}
}
