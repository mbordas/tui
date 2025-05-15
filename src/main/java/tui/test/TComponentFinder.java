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

import tui.test.components.TComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TComponentFinder<C extends TComponent> {

	private final Class<C> m_type;
	private final Supplier<Collection<TComponent>> m_componentSupplier;
	private Predicate<TComponent> m_parentOfClass = component -> true;
	private Predicate<C> m_thisCustomCondition = component -> true;

	private TComponentFinder(Supplier<Collection<TComponent>> componentSupplier, Class<C> type) {
		m_componentSupplier = componentSupplier;
		m_type = type;
	}

	public List<C> findAll() {
		final List<C> result = new ArrayList<>();
		m_componentSupplier.get().forEach((component) -> {
			if(m_parentOfClass.test(component)) {
				component.getChildrenComponents().stream()
						.filter((c) -> m_type.isAssignableFrom(c.getClass()))
						.forEach((c) -> {
							C typedComponent = (C) c;
							if(m_thisCustomCondition.test(typedComponent)) {
								result.add(typedComponent);
							}
						});
			}
		});
		return result;
	}

	public TComponentFinder<C> thatMatches(Predicate<C> condition) {
		m_thisCustomCondition = condition;
		return this;
	}

	public TComponentFinder<C> withParentOfClass(Class<? extends TComponent> parentOfClass) {
		m_parentOfClass = parentOfClass::isInstance;
		return this;
	}

	public static <T extends TComponent> TComponentFinder<T> ofClass(Class<T> type, TClient client) {
		return new TComponentFinder<>(client::getReachableSubComponents, type);
	}

	public static <T extends TComponent> TComponentFinder<T> ofClass(Class<T> type, TComponent component) {
		return new TComponentFinder<>(component::getReachableSubComponents, type);
	}
}
