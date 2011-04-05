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

package otsopack.full.java.network.communication.resources.graphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import otsopack.commons.data.TripleLiteralObject;
import otsopack.commons.data.TripleURIObject;
import otsopack.commons.data.WildcardTemplate;
import otsopack.full.java.network.communication.resources.prefixes.PrefixesStorage;

public class WildcardConverterTest {
	/*
	/* Trivial implementation, functionality already tested both in ITemplate
	 * implementations and in testAdaptFieldFormat()
	 **/
	/*@Test
	public void testCreateTemplateFromURL() {
		fail("Not yet implemented");
	}*/

	@Test
	public void testAdaptFieldFormat() throws Exception {
		final PrefixesStorage ps = new PrefixesStorage();
		
		ps.create("facebook","http://facebook.com/user/");
		ps.create("personal","http://personal.com/");
		
		assertEquals("<http://facebook.com/user/yoda>", WildcardConverter.adaptFieldFormat("http://facebook.com/user/yoda", 's', ps));
		assertEquals("<http://personal.com/website>", WildcardConverter.adaptFieldFormat("http://personal.com/website", 'p', ps));
		assertEquals("<http://yodaknowsit.com>", WildcardConverter.adaptFieldFormat("http://yodaknowsit.com", 'o', ps));
		assertEquals("?s", WildcardConverter.adaptFieldFormat("*", 's', ps));
		assertEquals("?p", WildcardConverter.adaptFieldFormat("*", 'p', ps));
		assertEquals("?o", WildcardConverter.adaptFieldFormat("*", 'o', ps));
		assertEquals("<http://facebook.com/user/yoda>", WildcardConverter.adaptFieldFormat("facebook:yoda", 's', ps));
		assertEquals("<http://personal.com/website>", WildcardConverter.adaptFieldFormat("personal:website", 'p', ps));
		assertEquals("<http://personal.com/>", WildcardConverter.adaptFieldFormat("personal:", 'o', ps));
		
		try {
			WildcardConverter.adaptFieldFormat("doesnotexist:website", 'p', ps);
			fail();
		} catch(Exception e) {
			//Since this prefix does not exist it must fail
		}
	}

	/*
	 * 	/{subjecturi}/{predicateuri}/* <br/>
	 * 	/{subjecturi}/{predicateuri}/{object-uri} <br />
	 *	/{subjecturi}/{predicateuri}/{object-literal-type}/{object-literal-value} <br />
	 * 	e.g.: /{subjecturi}/{predicateuri}/xsd:int/5<br />
	 */
	@Test
	public void testCreateURLFromTemplate() throws Exception {
		checkReturns(WildcardTemplate.createWithNull(null,null), "*/*/*");
		checkReturns(WildcardTemplate.createWithNull("http://s","http://p"),
				URLEncoder.encode("http://s","UTF-8")+"/"+URLEncoder.encode("http://p","UTF-8")+"/*");
		checkReturns(WildcardTemplate.createWithURI(null,null,"http://o"),
				"*/*/"+URLEncoder.encode("http://o","UTF-8"));
		checkReturns(WildcardTemplate.createWithLiteral(null,null,9), "*/*/xsd:int/9");
		checkReturns(WildcardTemplate.createWithLiteral(null,null,true), "*/*/xsd:boolean/true");

	}
	
	private void checkReturns(WildcardTemplate givingTpl, String expected) throws Exception {
		assertEquals( WildcardConverter.createURLFromTemplate(givingTpl), expected );
	}
}
