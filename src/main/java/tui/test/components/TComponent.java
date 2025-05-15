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

import tui.test.TClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Any component of a page on client-side is a {@link TComponent}. It gives convenient methods for test.
 */
public abstract class TComponent {

	private final Long m_tuid;
	protected final TClient m_client;
	protected boolean m_isVisible = true;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TComponent(Long tuid, TClient client) {
		m_tuid = tuid;
		m_client = client;
	}

	public Long getTUID() {
		return m_tuid;
	}

	public abstract TComponent find(long tuid);

	public abstract Collection<TComponent> getChildrenComponents();

	public Collection<TComponent> getReachableSubComponents() {
		final Collection<TComponent> result = new ArrayList<>();
		if(m_isVisible) {
			for(TComponent childComponent : getChildrenComponents()) {
				if(childComponent.m_isVisible) {
					result.add(childComponent);
					result.addAll(childComponent.getReachableSubComponents());
				}
			}
		}
		return result;
	}

	public Optional<TComponent> findReachableSubComponent(Predicate<TComponent> condition) {
		if(m_isVisible) {
			for(TComponent childComponent : getChildrenComponents()) {
				if(childComponent.m_isVisible) {
					if(condition.test(childComponent)) {
						return Optional.of(childComponent);
					} else {
						final Optional<TComponent> anyFoundComponent = childComponent.findReachableSubComponent(condition);
						if(anyFoundComponent.isPresent()) {
							return anyFoundComponent;
						}
					}
				}
			}
		}
		return Optional.empty();
	}

	public boolean isReachable() {
		return m_client.getReachableSubComponents().contains(this);
	}

	@Override
	public String toString() {
		return toString(null);
	}

	protected String toString(String title) {
		final StringBuilder result = new StringBuilder(getClass().getSimpleName());
		if(m_tuid != null) {
			result.append(" #").append(m_tuid);
		}
		if(title != null) {
			result.append(" '").append(title).append("'");
		}
		return result.toString();
	}

	public String branchString() {
		final StringBuilder result = new StringBuilder(toString());
		result.append("\n");
		for(TComponent child : getChildrenComponents()) {
			Arrays.stream(child.branchString().split("\n")).forEach((line) -> result.append("  ").append(line).append("\n"));
		}
		return result.toString();
	}

	protected static TComponent find(long tuid, Collection<? extends TComponent> reachableChildren) {
		for(TComponent child : reachableChildren) {
			if(child.getTUID() == tuid) {
				return child;
			}
			final TComponent foundComponent = child.find(tuid);
			if(foundComponent != null) {
				return foundComponent;
			}
		}
		return null;
	}

	protected static <T extends TComponent> List<T> getContent(final Class<T> clazz, Collection<? extends TComponent> reachableChildren) {
		return reachableChildren.stream()
				.filter((component) -> component.getClass() == clazz)
				.map((component) -> (T) component)
				.toList();
	}
}
