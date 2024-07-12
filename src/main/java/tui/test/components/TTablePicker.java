/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test.components;

import org.apache.http.HttpException;
import tui.json.JsonMap;
import tui.test.ComponentNoReachableException;
import tui.test.TClient;
import tui.test.TestExecutionException;
import tui.ui.components.TablePicker;
import tui.utils.TUIUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TTablePicker extends TTable {

	private final Set<Long> m_refreshListeners = new TreeSet<>();

	public TTablePicker(long tuid, String title, Collection<String> columns, String sourcePath, TClient tClient) {
		super(tuid, title, columns, sourcePath, tClient);
	}

	public static TTablePicker parse(JsonMap map, TClient client) {
		final BaseAttributes baseAttributes = parseBaseAttributes(map);

		final TTablePicker result = new TTablePicker(baseAttributes.tuid(), baseAttributes.title(), baseAttributes.columns(),
				baseAttributes.source(), client);

		loadRows(map, baseAttributes.columns(), result);

		final String refreshListenersTUIDs = map.getAttribute(TablePicker.ATTRIBUTE_REFRESH_LISTENERS);
		result.m_refreshListeners.addAll(TUIUtils.parseTUIDsSeparatedByComa(refreshListenersTUIDs));

		return result;
	}

	public void clickOnRow(int index) {
		final Map<String, Object> row = getRow(index);

		for(Long listenerTUID : m_refreshListeners) {
			final TComponent tComponent = m_client.find(listenerTUID);
			if(tComponent == null) {
				throw new TestExecutionException("Component #%d not found", listenerTUID);
			} else if(!tComponent.isReachable()) {
				throw new ComponentNoReachableException(listenerTUID);
			}
			if(tComponent instanceof TRefreshableComponent tRefreshableComponent) {
				try {
					tRefreshableComponent.refresh(row);
				} catch(HttpException e) {
					throw new TestExecutionException(e);
				}
			} else {
				throw new BadComponentException("Component #%d of type %s could not be refreshed",
						listenerTUID, tComponent.getClass().getSimpleName());
			}
		}
	}
}
