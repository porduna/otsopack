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

package otsopack.full.java.network.communication.resources.graphs;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface IGraphsResource {
	@Get("html")
	public abstract String toHtml();

	@Get("json")
	public abstract String toJson();
	
	@Post("json")
	public abstract String writeGraphJSON(String json);
	
	@Post("nt")
	public abstract String writeGraphNTriples(String ntriples);
}