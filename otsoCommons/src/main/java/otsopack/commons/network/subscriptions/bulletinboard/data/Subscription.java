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
package otsopack.commons.network.subscriptions.bulletinboard.data;

import java.util.UUID;

import otsopack.commons.data.NotificableTemplate;
import otsopack.commons.network.communication.event.listener.INotificationListener;

public class Subscription extends AbstractNotificableElement {
	final INotificationListener listener;
	
	private Subscription(String id, long expiration, NotificableTemplate tpl, INotificationListener listener) {
		super(id, expiration, tpl);
		this.listener = listener;
	}

	public INotificationListener getListener() {
		return this.listener;
	}
	
	public static Subscription createSubcription(String identifier, long lifetime, NotificableTemplate template, INotificationListener listener) {
		return new Subscription(identifier, lifetime, template, listener);
	}
	
	public static Subscription createSubcription(long lifetime, NotificableTemplate template, INotificationListener listener) {
		return createSubcription(UUID.randomUUID().toString(), lifetime, template, listener);
	}

	/**
	 * @param tpl
	 * 	The template being notified to the space.
	 * @return
	 * 	The node subscribed to "this" template should be notified when "tpl" is notified to the space?
	 * 	Returns true if the subscription template is subsumed by "tpl"
	 */
	public boolean isNotificable(NotificableTemplate tpl) {
		return this.tpl.match(tpl);
	}
}