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
 * Author: FILLME
 *
 */
package otsopack.full.java.network.coordination.bulletinboard;

import otsopack.commons.data.NotificableTemplate;

public abstract class AbstractNotificableElement implements Comparable<AbstractNotificableElement> {
	protected String id;
	protected long expiration;
	protected NotificableTemplate tpl;
	
	public AbstractNotificableElement(String id, long expiration, NotificableTemplate tpl) {
		this.id = id;
		this.expiration = expiration;
		this.tpl = tpl;
	}
	
	public String getID() {
		return this.id;
	}
	
	long getExpiration() {
		return this.expiration;
	}
	NotificableTemplate getTemplate() {
		return this.tpl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractNotificableElement other = (AbstractNotificableElement) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(AbstractNotificableElement o) {
		if( this.expiration<o.expiration ) return -1;
		if( this.expiration==o.expiration ) return 0;
		return 1;
	}
}
