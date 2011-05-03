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
 * 			Pablo Orduña <pablo.orduna@deusto.es>
 */

package otsopack.full.java.network.communication;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.restlet.Component;
import org.restlet.data.Protocol;

import otsopack.authn.IAuthenticatedUserHandler;
import otsopack.authn.OtsoAuthnApplication;
import otsopack.commons.IController;
import otsopack.full.java.network.communication.session.UserSession;

public class RestServer {
	public static final int DEFAULT_PORT = 8182;
	
	private final int port;
	private final Component component;
	private final OtsopackApplication application;
	private final OtsoAuthnApplication authnApp;
	
	public RestServer(int port, IController controller) {
		this.port = port;
		
	    this.component = new Component();
	    this.component.getServers().add(Protocol.HTTP, this.port);
	    
	    this.application = new OtsopackApplication();
	    this.application.setController(controller);
	    
	    this.authnApp = new OtsoAuthnApplication(
	    	new IAuthenticatedUserHandler() {
	    	  public String onAuthenticatedUser(String userIdentifier, String redirectURI){
	    		  final Calendar tomorrow = new GregorianCalendar();
	    		  tomorrow.setTimeInMillis( tomorrow.getTimeInMillis()+(24*60*60*1000));
	    		  
	    		  // new session created each time?
	    		  final UserSession session = new UserSession(userIdentifier);
	    		  final String sessionID = RestServer.this.application.getSessionManager().putSession(session);
	    		  return redirectURI + "?sessionID=" + sessionID;
	    	  }
	    	});
	    
	    this.component.getDefaultHost().attach(this.application);
	    this.component.getDefaultHost().attach(this.authnApp);
	}
	
	public RestServer(IController controller){
		this(DEFAULT_PORT, controller);
	}
	
	public RestServer(int port){
		this(port, null);
	}
	
	public RestServer(){
		this(DEFAULT_PORT, null);
	}
	
	public OtsopackApplication getApplication(){
		return this.application;
	}
	
	public void startup() throws Exception {
		this.component.start();
	}
	
	public void shutdown() throws Exception {
		this.component.stop();
	}
}
