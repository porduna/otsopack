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
package otsopack.full.java.network.communication.comet;

import java.util.HashMap;
import java.util.Map;

import otsopack.full.java.network.communication.comet.resources.EventResource;
import otsopack.full.java.network.communication.comet.resources.SessionResource;
import otsopack.full.java.network.communication.comet.resources.SessionsResource;
import otsopack.restlet.commons.AbstractOtsopackApplication;


public class OtsoCometApplication extends AbstractOtsopackApplication<ICometController> {

	private static final Map<String, Class<?>> PATHS = new HashMap<String, Class<?>>();
		
	static {
		addPaths(SessionResource.getRoots());
		addPaths(SessionsResource.getRoots());
		addPaths(EventResource.getRoots());
	}
	
	private static void addPaths(Map<String, Class<?>> roots) {
		for(String uri : roots.keySet())
			PATHS.put(uri, roots.get(uri));
	}
	
	public OtsoCometApplication() {
		super(PATHS);
	}
	
	public OtsoCometApplication(ICometController controller) {
		this();
		setController(controller);
	}

}
