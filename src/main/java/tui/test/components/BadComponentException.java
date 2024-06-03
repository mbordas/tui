/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test.components;

public class BadComponentException extends RuntimeException {

	public BadComponentException(String format, Object... args) {
		super(String.format(format, args));
	}
}
