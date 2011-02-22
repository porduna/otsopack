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
package otsopack.otsoDroid.network.communication.incoming;

import junit.framework.TestCase;
import otsopack.otsoDroid.network.communication.incoming.response.ModelResponse;
import otsopack.otsoDroid.network.communication.incoming.response.Response;
import otsopack.otsoDroid.network.communication.incoming.response.URIResponse;
import otsopack.otsoMobile.data.ISemanticFactory;
import otsopack.otsoMobile.data.impl.SemanticFactory;
import otsopack.otsoMobile.data.impl.microjena.MicrojenaFactory;
import otsopack.otsoMobile.exceptions.MalformedTemplateException;

public class IncomingListTest extends TestCase {
	
	public void setUp()	throws Exception {
		super.setUp();
		SemanticFactory.initialize(new MicrojenaFactory());
	}

	public void tearDown() {
	}

	public void testAdd() {
		ModelResponse[] responses = new ModelResponse[3];
		IncomingList inbox = new IncomingList();
		inbox.add( responses[0]=new ModelResponse(new Integer(1)) );
		inbox.add( responses[1]=new ModelResponse(new Integer(2)) );
		inbox.add( responses[2]=new ModelResponse(new Integer(3)) );
		assertEquals(inbox.modelResponses.size(),3);
		assertTrue(inbox.modelResponses.contains(responses[0]));
		assertTrue(inbox.modelResponses.contains(responses[1]));
		assertTrue(inbox.modelResponses.contains(responses[2]));
	}

	public void testGet() throws ArrayStoreException, MalformedTemplateException {
		final ISemanticFactory sf = new SemanticFactory();
		ModelResponse[] responses = new ModelResponse[5];
		URIResponse[] responsesUri = new URIResponse[2];
		IncomingList inbox = new IncomingList();
		inbox.add( responses[0]=new ModelResponse(new Integer(1)) );
		inbox.add( responses[1]=new ModelResponse(sf.createTemplate("?s ?p ?o .")) );
		inbox.add( responses[2]=new ModelResponse(new Integer(3)) );
		inbox.add( responses[4]=new ModelResponse(new Double(1.3)) );
		inbox.add( responses[3]=new ModelResponse(new Boolean(true)) );
		inbox.add( responsesUri[0]=new URIResponse(sf.createTemplate("?s ?p ?o ."),null) );
		inbox.add( responsesUri[1]=new URIResponse(sf.createTemplate("?s <http://p1> ?o ."),null) );
		assertEquals(inbox.modelResponses.size(),5);
		
		Response res = inbox.get(new Integer(2),false);
		assertNull(res);
		
		res = inbox.get(sf.createTemplate("?s ?p ?o ."),false);
		assertEquals(res,responses[1]);
		
		res = inbox.get(sf.createTemplate("?s ?p ?o ."),true);
		assertEquals(res,responsesUri[0]);
		
		res = inbox.get(new Boolean(true),false);
		assertEquals(res,responses[3]);
		
		res = inbox.get(new Double(1.3),false);
		assertEquals(res,responses[4]);
		
		res = inbox.get(sf.createTemplate("?s <http://p1> ?o ."),true);
		assertEquals(res,responsesUri[1]);
	}

	public void testRemove() {
		ModelResponse[] responses = new ModelResponse[3];
		IncomingList inbox = new IncomingList();
		inbox.add( responses[0]=new ModelResponse(new Integer(1)) );
		inbox.add( responses[1]=new ModelResponse(new Integer(2)) );
		inbox.add( responses[2]=new ModelResponse(new Integer(3)) );
		assertEquals(inbox.modelResponses.size(),3);
		
		inbox.remove(responses[1]);
		assertEquals(inbox.modelResponses.size(),2);
		assertTrue(inbox.modelResponses.contains(responses[0]));
		assertFalse(inbox.modelResponses.contains(responses[1]));
		assertTrue(inbox.modelResponses.contains(responses[2]));
		
		inbox.remove(responses[0]);
		assertEquals(inbox.modelResponses.size(),1);
		assertFalse(inbox.modelResponses.contains(responses[0]));
		assertFalse(inbox.modelResponses.contains(responses[1]));
		assertTrue(inbox.modelResponses.contains(responses[2]));
		
		inbox.remove(responses[2]);
		assertTrue(inbox.modelResponses.isEmpty());
		assertFalse(inbox.modelResponses.contains(responses[0]));
		assertFalse(inbox.modelResponses.contains(responses[1]));
		assertFalse(inbox.modelResponses.contains(responses[2]));
	}

	public void testClear() {
		ModelResponse[] responses = new ModelResponse[3];
		IncomingList inbox = new IncomingList();
		inbox.add( responses[0]=new ModelResponse(new Integer(1)) );
		inbox.add( responses[1]=new ModelResponse(new Integer(2)) );
		inbox.add( responses[2]=new ModelResponse(new Integer(3)) );
		assertEquals(inbox.modelResponses.size(),3);
		
		inbox.clear();
		assertEquals(inbox.modelResponses.size(),0);
		assertFalse(inbox.modelResponses.contains(responses[0]));
		assertFalse(inbox.modelResponses.contains(responses[1]));
		assertFalse(inbox.modelResponses.contains(responses[2]));
	}
}