/* Copyright (c) 2026, Mathieu Bordas
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

import org.jetbrains.annotations.NotNull;
import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.NavButton;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TNavButton extends TComponent {

	private final String m_label;
	private final String m_target;
	private final Map<String, String> m_parameters = new HashMap<>();

	protected TNavButton(TClient client, String label, String target, Map<String, String> parameters) {
		super(null, client);
		m_label = label;
		m_target = target;
		m_parameters.putAll(parameters);
	}

	public String getLabel() {
		return m_label;
	}

	public String getTarget() {
		return m_target;
	}

	public Map<String, String> getParameters() {
		return new TreeMap<>(m_parameters);
	}

	@Override
	public @NotNull Collection<TComponent> getAllChildrenComponents() {
		return List.of();
	}

	@Override
	public @NotNull Collection<TComponent> getReachableChildrenComponents() {
		return List.of();
	}

	public static TNavButton parse(JsonMap json, TClient tClient) {
		final String label = json.getAttribute(NavButton.JSON_ATTRIBUTE_LABEL);
		final String target = json.getAttribute(NavButton.JSON_ATTRIBUTE_TARGET);
		final JsonMap parametersMap = json.getMap(NavButton.JSON_ATTRIBUTE_PARAMETERS);
		final Map<String, String> parameters = new HashMap<>();
		parametersMap.getAttributes().forEach((key, value) -> parameters.put(key, value.toString()));

		final TNavButton result = new TNavButton(tClient, label, target, parameters);
		result.readCustomTag(json);

		return result;
	}
}
