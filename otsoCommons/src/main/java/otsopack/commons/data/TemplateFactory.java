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
package otsopack.commons.data;

public class TemplateFactory {

	public Template createWildcardTemplateWithLiteral(String subject, String predicate, Object object) {
		return new WildcardTemplate(subject, predicate, new TripleLiteralObject(object));
	}
	
	public Template createWildcardTemplateWithURI(String subject, String predicate, String object) {
		return new WildcardTemplate(subject, predicate, new TripleURIObject(object));
	}
}