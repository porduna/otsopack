package otsopack.commons.network.communication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import otsopack.commons.Arguments;
import otsopack.commons.OtsoServerManager;
import otsopack.commons.data.Graph;
import otsopack.commons.data.SemanticFormat;
import otsopack.commons.data.WildcardTemplate;

public class OtsopackRestMulticastWithoutSecurityIntegrationTest extends
		AbstractOtsopackRestMulticastIntegrationTest {

	private final Arguments arguments = new Arguments().setOutputFormat(SemanticFormat.NTRIPLES);
	
	// READ
	
	@Test
	public void testReadURI() throws Exception {
		final Graph graphA = this.ruc.read(OtsoServerManager.SPACE, this.nodeA.getGraphUris().get(0), this.arguments);
		assertGraphEquals(OtsoServerManager.AITOR_GRAPH, graphA);
		
		final Graph graphB = this.ruc.read(OtsoServerManager.SPACE, this.nodeB.getGraphUris().get(0), this.arguments);
		assertGraphEquals(OtsoServerManager.PABLO_GRAPH, graphB);
		
		final Graph graphC = this.ruc.read(OtsoServerManager.SPACE, this.nodeC.getGraphUris().get(0), this.arguments);
		assertGraphEquals(OtsoServerManager.YODA_GRAPH, graphC);
	}
	
	@Test
	public void testReadURINotFound() throws Exception {
		final Graph graphD = this.ruc.read(OtsoServerManager.SPACE, "http://not.existing.uri/", this.arguments);
		assertNull(graphD);
	}
	
	@Test
	public void testReadTemplate() throws Exception {
		final Graph graphA = this.ruc.read(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.AITOR_DEPICTION), this.arguments);
		assertGraphEquals(OtsoServerManager.AITOR_GRAPH, graphA);
		
		final Graph graphB = this.ruc.read(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.PABLO_DEPICTION), this.arguments);
		assertGraphEquals(OtsoServerManager.PABLO_GRAPH, graphB);
		
		final Graph graphC = this.ruc.read(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.YODA_DEPICTION), this.arguments);
		assertGraphEquals(OtsoServerManager.YODA_GRAPH, graphC);
	}
	
	@Test
	public void testReadTemplateNotFound() throws Exception {
		final Graph graphD = this.ruc.read(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, "http://other.uri/"), this.arguments);
		assertNull(graphD);
	}
	
	// TAKE
	
	@Test
	public void testTakeURI() throws Exception {
		Graph graphA = this.ruc.take(OtsoServerManager.SPACE, this.nodeA.getGraphUris().get(0), this.arguments);
		assertGraphEquals(OtsoServerManager.AITOR_GRAPH, graphA);
		// Assert deleted
		graphA = this.ruc.take(OtsoServerManager.SPACE, this.nodeA.getGraphUris().get(0), this.arguments);
		assertNull(graphA);
		
		Graph graphB = this.ruc.take(OtsoServerManager.SPACE, this.nodeB.getGraphUris().get(0), this.arguments);
		assertGraphEquals(OtsoServerManager.PABLO_GRAPH, graphB);
		// Assert deleted
		graphB = this.ruc.take(OtsoServerManager.SPACE, this.nodeB.getGraphUris().get(0), this.arguments);
		assertNull(graphB);
		
		
		Graph graphC = this.ruc.take(OtsoServerManager.SPACE, this.nodeC.getGraphUris().get(0), this.arguments);
		assertGraphEquals(OtsoServerManager.YODA_GRAPH, graphC);
		// Assert deleted
		graphC = this.ruc.take(OtsoServerManager.SPACE, this.nodeC.getGraphUris().get(0), this.arguments);
		assertNull(graphC);
	}
	
	@Test
	public void testTakeURINotFound() throws Exception {
		final Graph graphD = this.ruc.take(OtsoServerManager.SPACE, "http://not.existing.uri/", this.arguments);
		assertNull(graphD);
	}
	
	
	@Test
	public void testTakeTemplate() throws Exception {
		Graph graphA = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.AITOR_DEPICTION), this.arguments);
		assertGraphEquals(OtsoServerManager.AITOR_GRAPH, graphA);
		// Assert deleted
		graphA = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.AITOR_DEPICTION), this.arguments);
		assertNull(graphA);
		
		Graph graphB = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.PABLO_DEPICTION), this.arguments);
		assertGraphEquals(OtsoServerManager.PABLO_GRAPH, graphB);
		// Assert deleted
		graphB = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.PABLO_DEPICTION), this.arguments);
		assertNull(graphB);
		
		Graph graphC = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.YODA_DEPICTION), this.arguments);
		assertGraphEquals(OtsoServerManager.YODA_GRAPH, graphC);
		// Assert deleted
		graphC = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, OtsoServerManager.YODA_DEPICTION), this.arguments);
		assertNull(graphC);
	}
	
	@Test
	public void testTakeTemplateNotFound() throws Exception {
		final Graph graphD = this.ruc.take(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, null, "http://other.uri/"), this.arguments);
		assertNull(graphD);
	}
	
	@Test
	public void testQueryAll() throws Exception {
		final Graph [] returnedGraphs = this.ruc.query(OtsoServerManager.SPACE, WildcardTemplate.createWithNull(null, OtsoServerManager.DEPICTION), this.arguments);
		// All nodes returned something
		assertNotNull(returnedGraphs);
		assertEquals(3, returnedGraphs.length);
		
		for(Graph graph : returnedGraphs)
			assertTrue(
					"No known depiction found in returned graph: " + graph,
					graph.getData().indexOf(OtsoServerManager.AITOR_DEPICTION) >= 0
					|| graph.getData().indexOf(OtsoServerManager.PABLO_DEPICTION) >= 0
					|| graph.getData().indexOf(OtsoServerManager.YODA_DEPICTION) >= 0
				);
	}
	
	@Test
	public void testQueryOne() throws Exception {
		final Graph [] returnedGraphs = this.ruc.query(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, OtsoServerManager.DEPICTION, OtsoServerManager.PABLO_DEPICTION), this.arguments);
		// A single node returned something
		assertNotNull(returnedGraphs);
		assertEquals(1, returnedGraphs.length);
		assertTrue(returnedGraphs[0].getData().indexOf(OtsoServerManager.PABLO_DEPICTION) >= 0);
	}
	
	@Test
	public void testQueryNone() throws Exception {
		final Graph [] returnedGraphs = this.ruc.query(OtsoServerManager.SPACE, WildcardTemplate.createWithURI(null, OtsoServerManager.DEPICTION, "http://this.does.not.exist"), this.arguments);
		// No exception, no null, just an empty array
		assertNull(returnedGraphs);
	}
}
