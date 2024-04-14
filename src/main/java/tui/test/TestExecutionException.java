/*
 * Copyright (c) 2012-2024 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package tui.test;

public class TestExecutionException extends RuntimeException {
	public TestExecutionException(String format, Object... args) {
		super(String.format(format, args));
	}
}
