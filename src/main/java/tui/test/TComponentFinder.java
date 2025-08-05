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

package tui.test;

import org.jetbrains.annotations.NotNull;
import tui.test.components.TComponent;
import tui.test.components.TPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class TComponentFinder<C extends TComponent> {

	private final Class<C> m_type;
	private final TComponent m_root;
	private final Collection<TComponent> m_children;
	private Predicate<TComponent> m_parentOfClass = component -> true;
	private Predicate<TComponent> m_parentCondition = component -> true;
	private Predicate<C> m_thisCondition = component -> true;

	private TComponentFinder(Class<C> type, TComponent root, Collection<TComponent> children) {
		m_type = type;
		m_root = root;
		m_children = children;
	}

	public List<C> findAll() {
		return findAll(m_root, m_children);
	}

	private List<C> findAll(TComponent parent, Collection<TComponent> children) {
		final List<C> result = new ArrayList<>();
		children.forEach((c) -> {

			// Testing child 'c'
			if((parent == null || (m_parentOfClass.test(parent) && m_parentCondition.test(parent)))
					&& m_type.isAssignableFrom(c.getClass())) {
				C typedComponent = (C) c;
				if(m_thisCondition.test(typedComponent)) {
					result.add(typedComponent);
				}
			}

			// Recursive call on c's children
			result.addAll(findAll(c, c.getChildrenComponents()));
			//			c.getChildrenComponents().forEach((c3) -> result.addAll(findAll(c3, c3.getChildrenComponents())));
		});
		return result;
	}

	public @NotNull C getUnique() {
		final List<C> components = findAll();
		if(components.isEmpty()) {
			throw new TestExecutionException("No %s found in current page.", m_type.getSimpleName());
		} else if(components.size() > 1) {
			throw new TestExecutionException("Too many %s found: %d", m_type.getSimpleName(), components.size());
		} else {
			return components.get(0);
		}
	}

	public TComponentFinder<C> withCondition(Predicate<C> condition) {
		m_thisCondition = condition;
		return this;
	}

	public TComponentFinder<C> withConditionOnParent(Predicate<TComponent> condition) {
		m_parentCondition = condition;
		return this;
	}

	public TComponentFinder<C> withParentOfClass(Class<? extends TComponent> parentOfClass) {
		m_parentOfClass = parentOfClass::isInstance;
		return this;
	}

	public static <T extends TComponent> TComponentFinder<T> ofClass(Class<T> type, TPage page) {
		return new TComponentFinder<>(type, null, page.getChildrenComponents());
	}

	public static <T extends TComponent> TComponentFinder<T> ofClass(Class<T> type, TComponent component) {
		return new TComponentFinder<>(type, component, component.getChildrenComponents());
	}
}
