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
package otsopack.commons.network.subscriptions.bulletinboard.http.server;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import otsopack.commons.network.coordination.IRegistry;
import otsopack.commons.network.subscriptions.bulletinboard.http.server.consumer.OtsopackHttpBulletinBoardConsumerApplication;
import otsopack.restlet.commons.OtsoRestletUtils;

public class BulletinBoardListenerRestServer {
	public static final int DEFAULT_PORT = 8185;
	
	private final int port;
	private final Component component;
	private final OtsopackHttpBulletinBoardConsumerApplication application;


	public BulletinBoardListenerRestServer(int port, IBulletinBoardController controller) {
		this.port = port;
		
	    this.component = new Component();
	    final Server server = new Server(Protocol.HTTP, this.port);
	    server.setContext(OtsoRestletUtils.createContext());
	    this.component.getServers().add(server);
	    
	    this.application = new OtsopackHttpBulletinBoardConsumerApplication();
	    this.application.setController(controller);
	    // TODO made this configurable!
	    this.component.getDefaultHost().attach(OtsopackHttpBulletinBoardConsumerApplication.BULLETIN_ROOT_PATH,
	    										this.application);
	}
	
	public BulletinBoardListenerRestServer(IBulletinBoardController controller){
		this(DEFAULT_PORT, controller);
	}
	
	public BulletinBoardListenerRestServer(int port, IRegistry registry){
		this(port, new BulletinBoardController(registry));
	}
	
	public OtsopackHttpBulletinBoardConsumerApplication getApplication(){
		return this.application;
	}
	
	public void startup() throws Exception {
		this.component.start();
	}
	
	public void shutdown() throws Exception {
		this.component.stop();
	}
}
