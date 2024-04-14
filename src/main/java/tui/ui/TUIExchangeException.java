/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.ui;

public class TUIExchangeException extends RuntimeException {

	public TUIExchangeException(String format, Object... args) {
		super(String.format(format, args));
	}
}
