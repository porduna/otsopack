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
 * Author: Pablo Orduña <pablo.orduna@deusto.es>
 *
 */
package otsopack.full.java.network.communication.comet.event.requests;

import otsopack.commons.data.Graph;
import otsopack.commons.data.SemanticFormat;
import otsopack.commons.exceptions.TSException;
import otsopack.commons.network.ICommunication;

public abstract class ReadRequest extends GraphRequest {

	public ReadRequest() {
	}

	public ReadRequest(long timeout, SemanticFormat outputFormat) {
		super(timeout, outputFormat);
	}
	
	public abstract Graph read(String spaceURI, ICommunication comm) throws TSException;
}
