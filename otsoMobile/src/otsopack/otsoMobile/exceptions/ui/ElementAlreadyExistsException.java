/*
 * Copyright (C) 2008-2011 University of Deusto
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

package otsopack.otsoMobile.exceptions.ui;

public class ElementAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ElementAlreadyExistsException() {
		this("The message is not one of the predefined by our TSC protocol.");
	}
	
	public ElementAlreadyExistsException(String message) {
		super(message);
	}
}
