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
package otsopack.commons.network.subscriptions.bulletinboard.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import otsopack.commons.data.NotificableTemplate;
import otsopack.commons.data.WildcardTemplate;
import otsopack.commons.exceptions.SubscriptionException;
import otsopack.commons.network.coordination.Node;
import otsopack.commons.network.subscriptions.bulletinboard.LocalListenerTester;
import otsopack.commons.network.subscriptions.bulletinboard.RemoteBulletinBoard;
import otsopack.commons.network.subscriptions.bulletinboard.data.RemoteNotificationListener;
import otsopack.commons.network.subscriptions.bulletinboard.data.Subscription;
import otsopack.commons.network.subscriptions.bulletinboard.http.server.provider.OtsopackHttpBulletinBoardProviderApplication;

public class HttpBulletinBoardClientTest {
	private int PORT = 18086;
	private RemoteBulletinBoard client;
	private BulletinBoardManager manager;

	@Before
	public void setUp() throws Exception {
		this.manager = new BulletinBoardManager(this.PORT);
		this.manager.start();
		
		this.client = this.manager.createClient();
	}

	@After
	public void tearDown() throws Exception {
		this.manager.stop();
	}

	@Test
	public void testSubscribe() throws URISyntaxException, SubscriptionException {
		final Subscription sentSub = Subscription.createSubcription(
										System.currentTimeMillis()+60000,
										WildcardTemplate.createWithNull(null, null),
										// kids, don't do this at home!
										new RemoteNotificationListener(new URI("http://localhost:9999")) );
		final String uuid = this.client.subscribe(sentSub);
		final Subscription createdSubs = Subscription.createSubcription(uuid, 0, null, null);
		
		Collection<Subscription> subscriptions = this.manager.getSubscriptions();
		assertEquals(1, subscriptions.size());
		assertThat(subscriptions, hasItem(createdSubs));
	}
	
	
	@Test
	public void testUpdateSubscribe() throws URISyntaxException, SubscriptionException {
		final long timestamp1 = System.currentTimeMillis()+60000;
		final long timestamp2 = System.currentTimeMillis()+360000;
		final Subscription sentSub = Subscription.createSubcription(
				timestamp1,
				WildcardTemplate.createWithNull(null, null),
				new RemoteNotificationListener(new URI("http://localhost:9999")) );
		final String uuid = this.client.subscribe(sentSub);
		
		Collection<Subscription> subscriptions = this.manager.getSubscriptions();
		assertEquals(1, subscriptions.size());
		for(Subscription subscription: subscriptions) {
			if( subscription.getID().equals(uuid) ) {
				assertEquals(timestamp1, subscription.getExpiration());
				break;
			}
		}

		this.client.updateSubscription(uuid, timestamp2);
		
		subscriptions = this.manager.getSubscriptions();
		assertEquals(1, subscriptions.size());
		for(Subscription subscription: subscriptions) {
			if( subscription.getID().equals(uuid) ) {
				assertEquals(timestamp2, subscription.getExpiration());
				break;
			}
		}
	}
	
	@Test
	public void testUnsubscribe() throws URISyntaxException, SubscriptionException {
		final Subscription sentSub = Subscription.createSubcription(
											System.currentTimeMillis()+60000,
											WildcardTemplate.createWithNull(null, null),
											new RemoteNotificationListener(new URI("http://localhost:9999")) );

		final String uuid = this.client.subscribe(sentSub);
		final Subscription createdSubscription = Subscription.createSubcription(uuid,0, null, null);
		
		Collection<Subscription> subscriptions = this.manager.getSubscriptions();
		assertEquals(1, subscriptions.size());
		assertThat(subscriptions, hasItem(createdSubscription));

		this.client.unsubscribe(uuid);
		
		subscriptions = this.manager.getSubscriptions();
		assertEquals(0, subscriptions.size());
	}
	
	@Test
	public void testNotifyUsingOneBulletinBoard() throws Exception {
		assertTrue( notify(	WildcardTemplate.createWithURI("http://s", "http://p", "http://o"),
						WildcardTemplate.createWithURI("http://s", "http://p", "http://o")) );
		assertTrue( notify( WildcardTemplate.createWithNull("http://s", "http://p"),
					 	WildcardTemplate.createWithURI("http://s", "http://p", "http://o")) );
		assertTrue( notify( WildcardTemplate.createWithNull("http://s", "http://p"),
				 WildcardTemplate.createWithLiteral("http://s", "http://p", new Integer(21))) );
		assertFalse( notify(	WildcardTemplate.createWithURI("http://s", "http://p", "http://o"),
						WildcardTemplate.createWithNull("http://s", "http://p")) );
	}
	
	private boolean notify(NotificableTemplate subscribed, NotificableTemplate notified) throws Exception {
		final int EXPIRATIONTIME = 1000;
		final long currentTime = System.currentTimeMillis();
		
		final LocalListenerTester list = new LocalListenerTester();
		final Subscription sub = Subscription.createSubcription("uuid1", currentTime+EXPIRATIONTIME, subscribed, list);
		this.client.subscribe(sub);
		
		final RemoteBulletinBoard client2 = this.manager.createClient();
		client2.notify(notified);
		
		if (!list.isNotified()) { // it needs time...
			synchronized (list.getLock()) {
				list.getLock().wait(EXPIRATIONTIME);
			}
		}
		return list.isNotified();
	}
	
	@Test
	public void testNotifyUsingTwoBulletinBoards() throws Exception {
		assertTrue( notifyUsingTwo(	WildcardTemplate.createWithURI("http://s", "http://p", "http://o"),
						WildcardTemplate.createWithURI("http://s", "http://p", "http://o")) );
		assertTrue( notifyUsingTwo( WildcardTemplate.createWithNull("http://s", "http://p"),
					 	WildcardTemplate.createWithURI("http://s", "http://p", "http://o")) );
		assertTrue( notifyUsingTwo( WildcardTemplate.createWithNull("http://s", "http://p"),
				 		WildcardTemplate.createWithLiteral("http://s", "http://p", new Integer(21))) );
		assertFalse( notifyUsingTwo(WildcardTemplate.createWithURI("http://s", "http://p", "http://o"),
						WildcardTemplate.createWithNull("http://s", "http://p")) );
	}
	
	private boolean notifyUsingTwo(NotificableTemplate subscribed, NotificableTemplate notified) throws Exception {
		final int EXPIRATIONTIME = 1000;
		final long currentTime = System.currentTimeMillis();
		
		final BulletinBoardManager manager2 = new BulletinBoardManager(this.PORT+1, this.PORT+1000);
		manager2.start();
		
		// bulletinBoard0 knows bulletinBoard1
		manager.otherBulletinBoards.add(new Node("http://localhost:"+(this.PORT)+OtsopackHttpBulletinBoardProviderApplication.BULLETIN_ROOT_PATH,
													"bboard0", true, true, false));
		manager.otherBulletinBoards.add(new Node("http://localhost:"+(this.PORT+1)+OtsopackHttpBulletinBoardProviderApplication.BULLETIN_ROOT_PATH,
													"bboard1", true, true, false));
		
		manager2.otherBulletinBoards.add(new Node("http://localhost:"+this.PORT+OtsopackHttpBulletinBoardProviderApplication.BULLETIN_ROOT_PATH,
													"bboard0", true, true, false));
		manager2.otherBulletinBoards.add(new Node("http://localhost:"+(this.PORT+1)+OtsopackHttpBulletinBoardProviderApplication.BULLETIN_ROOT_PATH,
													"bboard1", true, true, false));
		
		final LocalListenerTester list = new LocalListenerTester();
		final Subscription sub = Subscription.createSubcription("uuid1", currentTime+EXPIRATIONTIME, subscribed, list);
		this.client.subscribe(sub);
		
		final RemoteBulletinBoard client2 = manager2.createClient();
		client2.notify(notified);
		
		if (!list.isNotified()) { // it needs time...
			synchronized (list.getLock()) {
				list.getLock().wait(EXPIRATIONTIME);
			}
		}
		
		manager2.stop();
		return list.isNotified();
	}
}