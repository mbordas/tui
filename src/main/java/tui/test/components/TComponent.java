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

import tui.test.TestClient;

import java.util.Collection;

/**
 * Any component of a page on client-side is a {@link TComponent}. It gives convenient methods for test.
 */
public abstract class TComponent {

	private final long m_tuid;
	protected final TestClient m_testClient;

	/**
	 * @param tuid       Unique identifier.
	 * @param testClient This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TComponent(long tuid, TestClient testClient) {
		m_tuid = tuid;
		m_testClient = testClient;
	}

	public long getTUID() {
		return m_tuid;
	}

	public abstract TComponent find(long tuid);

	public boolean isReachable() {
		return m_testClient.find(getTUID()) != null;
	}

	protected static TComponent find(long tuid, Collection<? extends TComponent> reachableChildren) {
		for(TComponent child : reachableChildren) {
			if(child.getTUID() == tuid) {
				return child;
			}
		}
		return null;
	}
}
