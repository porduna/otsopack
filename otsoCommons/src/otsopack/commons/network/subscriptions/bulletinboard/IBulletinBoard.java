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
package otsopack.commons.network.subscriptions.bulletinboard;

import otsopack.commons.data.NotificableTemplate;
import otsopack.commons.exceptions.SubscriptionException;
import otsopack.commons.network.communication.event.listener.INotificationListener;

public interface IBulletinBoard extends ISubscriptionsChecker {	
	void start() throws SubscriptionException;
	void stop() throws SubscriptionException;
	
	String subscribe(NotificableTemplate template, INotificationListener listener, long subscriptionLifetime) throws SubscriptionException;
	void unsubscribe(String subscriptionId) throws SubscriptionException ;
	
	/**
	 * Notification generated by the TSC user.
	 * @param adv
	 * @throws SubscriptionException
	 */
	void notify(NotificableTemplate adv) throws SubscriptionException ;
	
	/**
	 * Used when a bulletin board notifies this client (subscriber).
	 * 
	 * The main difference with notify is that the notification must not be propagated.
	 */
	void receiveCallback(NotificableTemplate adv);
}