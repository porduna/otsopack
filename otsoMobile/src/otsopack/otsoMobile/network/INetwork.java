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

package otsopack.otsoMobile.network;

import otsopack.otsoMobile.ILayer;

/**
 * network layer interface
 * @author Aitor Gómez Goiri
 */
public interface INetwork extends ICommunication, ICoordination, ILayer {
	
	/**
	 * get communication object
	 * @return communication
	 */
	public ICommunication getCommunication();

	/**
	 * get coordination object
	 * @return coordination
	 */
	public ICoordination getCoordination();
}