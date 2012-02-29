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
package otsopack.commons.network.coordination.bulletinboard.data;

import otsopack.commons.network.communication.event.listener.EventNotification;
import otsopack.commons.network.communication.event.listener.INotificationListener;
import otsopack.commons.network.coordination.Node;

public class RemoteNotificationListener implements INotificationListener {
	final Node node;
	
	public RemoteNotificationListener(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return this.node;
	}

	@Override
	public void notifyEvent(EventNotification notification) {
		// TODO submit notification task
		
	}
}