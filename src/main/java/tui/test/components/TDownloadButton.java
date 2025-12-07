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

import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.DownloadButton;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDownloadButton extends TComponent {

	private final String m_label;
	private final String m_target;
	private final String m_defaultFileName;
	private final Map<String, String> m_parameters = new HashMap<>();

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TDownloadButton(Long tuid, String label, String target, String defaultFileName, TClient client) {
		super(tuid, client);
		m_label = label;
		m_target = target;
		m_defaultFileName = defaultFileName;
	}

	public String getLabel() {
		return m_label;
	}

	public String getTarget() {
		return m_target;
	}

	public String getDefaultFileName() {
		return m_defaultFileName;
	}

	public String getParameter(String name) {
		return m_parameters.get(name);
	}

	public File downloadIntoDir(File outputDir) {
		return m_client.download(m_target, new HashMap<>(m_parameters), m_defaultFileName, outputDir);
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	@Override
	public Collection<TComponent> getChildrenComponents() {
		return List.of();
	}

	public static TDownloadButton parse(JsonMap map, TClient client) {
		final long tuid = JsonConstants.readTUID(map);
		final String label = map.getAttribute(DownloadButton.JSON_ATTRIBUTE_LABEL);
		final String target = map.getAttribute(DownloadButton.JSON_ATTRIBUTE_TARGET);
		final String defaultFileName = map.getAttribute(DownloadButton.JSON_ATTRIBUTE_DEFAULT_FILE_NAME);
		final TDownloadButton result = new TDownloadButton(tuid, label, target, defaultFileName, client);
		final JsonMap parametersMap = map.getMap(DownloadButton.JSON_MAP_PARAMETERS);
		parametersMap.getAttributes().forEach((key, value) -> result.m_parameters.put(key, value.toString()));
		return result;
	}

}
