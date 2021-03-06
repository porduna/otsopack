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
package otsopack.commons.authz.entities;

/**
 * This class represents an authorisation entity to a certain graph.
 * 
 * More specifically: Certification entities.
 */
public class CertificationEntity implements IEntity {
	
	private static final long serialVersionUID = 8148587788772098127L;
	
	public static final String code = "certificate";
	
	public String serialize(){
		return code + ":"; // + TODO
	}
	
	public static CertificationEntity create(String serialized){
		throw new RuntimeException(CertificationEntity.class.getName() + " not implemented");
	}
	
	public boolean check(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see otsopack.commons.authz.entities.IEntity#isAnonymous()
	 */
	public boolean isAnonymous() {
		return false;
	}
}
