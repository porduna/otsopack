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
package otsopack.commons.network.subscriptions.bulletinboard.http.server.provider.resources;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface ISubscriptionsResource {
	/*@Get("html")	
	Representation toHtml();*/
	
	@Get("json")
	Representation viewSubscriptions(Representation rep);
	
	@Post("json")
	Representation createSubscription(Representation rep);
}