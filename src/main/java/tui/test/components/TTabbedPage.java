/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test.components;

import tui.json.JsonArray;
import tui.json.JsonConstants;
import tui.json.JsonMap;
import tui.json.JsonObject;
import tui.test.TClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTabbedPage extends ATPage {

	private final Map<String /* tab label */, TTabbedPanel /* panel */> m_panels = new LinkedHashMap<>();
	private String m_selectedPanelTitle = null;

	/**
	 * @param tuid   Unique identifier.
	 * @param client This client object will help acting on some component, and determining if they are reachable.
	 */
	protected TTabbedPage(long tuid, String title, TClient client) {
		super(tuid, title, client);
	}

	public String getTabTitle() {
		return m_selectedPanelTitle;
	}

	@Override
	public Collection<TComponent> getReachableSubComponents() {
		return List.of(m_panels.get(m_selectedPanelTitle));
	}

	@Override
	public TComponent find(long tuid) {
		return null;
	}

	public static TTabbedPage parse(JsonMap jsonMap, TClient client) {
		final String title = jsonMap.getAttribute("title");
		final long tuid = JsonConstants.readTUID(jsonMap);
		final TTabbedPage result = new TTabbedPage(tuid, title, client);

		final JsonArray panels = jsonMap.getArray("content");

		final Iterator<JsonObject> panelIterator = panels.iterator();
		while(panelIterator.hasNext()) {
			final JsonObject panelJson = panelIterator.next();

			final TComponent maybePanel = TComponentFactory.parse(panelJson, client);
			assert maybePanel instanceof TTabbedPanel;
			TTabbedPanel panel = (TTabbedPanel) maybePanel;
			if(result.m_panels.isEmpty()) {
				result.m_selectedPanelTitle = panel.getTitle();
			}
			result.m_panels.put(panel.getTitle(), panel);
		}
		return result;
	}

}
