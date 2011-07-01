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
package otsopack.full.java.network.coordination.bulletinboard;

import otsopack.full.java.network.coordination.IBulletinBoard;
import otsopack.full.java.network.coordination.IRegistry;
import otsopack.full.java.network.coordination.bulletinboard.data.Advertisement;
import otsopack.full.java.network.coordination.bulletinboard.data.Subscription;
import otsopack.full.java.network.coordination.bulletinboard.memory.BulletinBoard;

public class LocalBulletinBoard implements IBulletinBoard {
	final IRegistry registry;
	
	// server to receive subscriptions from clients or advertisements
	// from both clients and other bulletin boards
	
	// bulletin board for both local and remote subscriptions
	final BulletinBoard bulletinBoard;
	
	public LocalBulletinBoard(IRegistry registry) {
		this.registry = registry;
		this.bulletinBoard = new BulletinBoard();
	}

	@Override
	public String subscribe(Subscription subscription) {
		return this.bulletinBoard.subscribe(subscription);
	}
	
	@Override
	public void updateSubscription(String subscriptionId, long extratime) {
		this.bulletinBoard.updateSubscription(subscriptionId, extratime);
	}
	
	@Override
	public void unsuscribe(String subscriptionId) {
		this.bulletinBoard.unsuscribe(subscriptionId);
	}
	
	@Override
	public String advertise(Advertisement adv) {
		final String advId = this.bulletinBoard.advertise(adv);
		//this.registry.getBulletinBoards()
		// TODO propagate to other bulletin boards
		return advId;
	}
		
	@Override
	public void updateAdvertisement(String advId, long extratime) {
		this.bulletinBoard.updateAdvertisement(advId, extratime);
		// TODO propagate to other bulletin boards
	}

	@Override
	public void unadvertise(String advId) {
		this.bulletinBoard.unadvertise(advId);
		// TODO propagate to other bulletin boards
	}

	@Override
	public Advertisement[] getAdvertises() {
		return this.bulletinBoard.getAdvertises();
	}
}