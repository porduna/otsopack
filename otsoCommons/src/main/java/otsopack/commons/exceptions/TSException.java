/*
 * Copyright (C) 2008 onwards University of Deusto
 * 
 * All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.
 * 
 * This software consists of contributions made by many individuals, 
 * listed below:
 *
 * Author: Aitor Gómez Goiri <aitor.gomez@deusto.es>
 */

package otsopack.commons.exceptions;

public class TSException extends Exception {

	private static final long serialVersionUID = 1L;

	public TSException(String message) {
		super(message);
	}
	
	public TSException(String message, Exception e) {
		super(message, e);
	}
}