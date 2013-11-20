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
 * Author: Pablo Orduña <pablo.orduna@deusto.es>
 *
 */
package otsopack.commons.network.coordination.discovery.http.server;

import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.ServerResource;

import otsopack.commons.network.coordination.discovery.http.server.resources.DiscoveryResource;
import otsopack.restlet.commons.AbstractOtsopackApplication;

public class OtsopackHttpDiscoveryApplication extends AbstractOtsopackApplication<IDiscoveryController> {

	private static final Map<String, Class<? extends ServerResource>> PATHS = new HashMap<String, Class<? extends ServerResource>>();
	
	static{
		addPaths(DiscoveryResource.getRoots());
	}
	
	private static void addPaths(Map<String, Class<? extends ServerResource>> roots){
		for(String uri : roots.keySet())
			PATHS.put(uri, roots.get(uri));
	}

	
	public OtsopackHttpDiscoveryApplication() {
		super(PATHS);
	}

}
