/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test.components;

import tui.json.JsonMap;
import tui.test.TClient;
import tui.ui.components.TablePicker;
import tui.utils.TUIUtils;

import java.util.Collection;
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
}
