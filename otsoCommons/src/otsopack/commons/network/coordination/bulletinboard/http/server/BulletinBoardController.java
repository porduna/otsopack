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
package otsopack.commons.network.coordination.bulletinboard.http.server;

import otsopack.commons.network.coordination.IBulletinBoard;
import otsopack.commons.network.coordination.IRegistry;
import otsopack.commons.network.coordination.bulletinboard.LocalBulletinBoard;

public class BulletinBoardController implements IBulletinBoardController {
	
	// TODO rethink: maybe this class or LocalBulletinBoard are redundant
	final IBulletinBoard local;
	
	public BulletinBoardController(IBulletinBoard localBulletinBoard) {
		this.local = localBulletinBoard;
	}
	
	public BulletinBoardController(IRegistry registry) {
		this(new LocalBulletinBoard(registry));
	}
	
	@Override
	public IBulletinBoard getBulletinBoard() {
		return this.local;
	}
}