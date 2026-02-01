/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test.components;

import org.jetbrains.annotations.NotNull;
import tui.json.JsonMap;
import tui.test.ComponentNoReachableException;
import tui.test.TClient;
import tui.test.TestExecutionException;
import tui.ui.components.TablePicker;
import tui.utils.TUIUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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

		loadRows(map, baseAttributes.columns(), result, client);

		final String refreshListenersTUIDs = map.getAttribute(TablePicker.ATTRIBUTE_REFRESH_LISTENERS);
		result.m_refreshListeners.addAll(TUIUtils.parseTUIDsSeparatedByComa(refreshListenersTUIDs));

		return result;
	}

	public void clickOnRow(int index) {
		final Map<String, TComponent> row = getRow(index);
		final Map<String, String> parameters = convertToParameters(row);

		for(Long listenerTUID : m_refreshListeners) {
			final TComponent tComponent = m_client.find(listenerTUID);
			if(tComponent == null) {
				throw new TestExecutionException("Component #%d not found", listenerTUID);
			} else if(!tComponent.isReachable()) {
				throw new ComponentNoReachableException(listenerTUID);
			}
			if(tComponent instanceof TRefreshableComponent tRefreshableComponent) {
				tRefreshableComponent.refresh(parameters);
			} else {
				throw new BadComponentException("Component #%d of type %s could not be refreshed",
						listenerTUID, tComponent.getClass().getSimpleName());
			}
		}
	}

	/**
	 * Each {@link TParagraph.TText} components are converted into String parameter. Any component that is not {@link TParagraph.TText} is ignored
	 */
	private static @NotNull Map<String, String> convertToParameters(Map<String, TComponent> row) {
		final Map<String, String> parameters = new TreeMap<>();
		row.forEach((key, value) -> {
			if(value instanceof TParagraph.TText tText) {
				parameters.put(key, tText.getText());
			}
		});
		return parameters;
	}
}
