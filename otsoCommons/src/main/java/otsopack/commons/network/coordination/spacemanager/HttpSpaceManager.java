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
 * Author: Pablo Orduña <pablo.orduna@deusto.es>
 *
 */
package otsopack.commons.network.coordination.spacemanager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import otsopack.commons.network.communication.util.JSONDecoder;
import otsopack.commons.network.communication.util.JSONEncoder;
import otsopack.commons.network.coordination.ISpaceManager;
import otsopack.commons.network.coordination.Node;
import otsopack.commons.network.coordination.spacemanager.http.server.resources.NodesResource;
import otsopack.commons.network.coordination.spacemanager.http.server.resources.StatesResource;
import otsopack.restlet.commons.EnrichedClientResource;

public class HttpSpaceManager implements ISpaceManager {
	
	private final String uri;
	private final String [] references; 
	
	public HttpSpaceManager(String uri){
		this.uri = uri;
		this.references = new String[]{"[http]" + uri};
	}

	protected ClientResource createClientResource(String uriToGo){
		return new EnrichedClientResource(uriToGo);
	}
	
	@Override
	public Node[] getNodes() throws SpaceManagerException {
		final ClientResource client = createClientResource(this.uri + NodesResource.ROOT);
		String serializedSpaceManagers;
		try{
			final Representation repr;
			try{
				repr = client.get(MediaType.APPLICATION_JSON);
			}catch(ResourceException e){
				throw new SpaceManagerException("Could not get nodes from " + this.uri + ": " + e.getMessage(), e);
			}
			try {
				serializedSpaceManagers = IOUtils.toString(repr.getStream());
			} catch (IOException e) {
				throw new SpaceManagerException("Could not read stream from space manager: " + e.getMessage(), e);
			}
		}finally{
			client.release();
		}
		return JSONDecoder.decode(serializedSpaceManagers, Node[].class);
	}
	
	@Override
	public Set<Node> getBulletinBoards() throws SpaceManagerException {
		final Set<Node> bbs = new HashSet<Node>();
		final Node[] nodes = getNodes();
		for(Node node : nodes)
			if(node.isBulletinBoard())
				bbs.add(node);
		return bbs;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.uri == null) ? 0 : this.uri.hashCode());
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
		HttpSpaceManager other = (HttpSpaceManager) obj;
		if (this.uri == null) {
			if (other.uri != null)
				return false;
		} else if (!this.uri.equals(other.uri))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "HttpSpaceManagerClient [uri=" + this.uri + "]";
	}

	@Override
	public String [] getExternalReferences() {
		return this.references;
	}

	/* (non-Javadoc)
	 * @see otsopack.full.java.network.coordination.ISpaceManager#join(otsopack.full.java.network.coordination.Node)
	 */
	@Override
	public String join(Node node) throws SpaceManagerException {
		final ClientResource client = createClientResource(this.uri + StatesResource.ROOT);
		final String encodedNode = JSONEncoder.encode(node);
		final String serializedSecret;
		try{
			final Representation repr;
			try{
				repr = client.post(new StringRepresentation(encodedNode, MediaType.APPLICATION_JSON, null, null), MediaType.APPLICATION_JSON);
			}catch(ResourceException e){
				throw new SpaceManagerException("Could not join to space manager " + this.uri + ": " + e.getMessage(), e);
			}
			try {
				serializedSecret = repr.getText();
			} catch (IOException e) {
				throw new SpaceManagerException("Could not read stream from space manager: " + e.getMessage(), e);
			}
		}finally{
			client.release();
		}
		return JSONDecoder.decode(serializedSecret, String.class);
	}
	
	private static boolean isUp(NetworkInterface netIf) throws SocketException {
		try {
			// In Android <= 8, the method isUp is not available. Therefore, this code will compile
			// but will not work. We check if it's available or not before calling it
			NetworkInterface.class.getDeclaredMethod("isUp");
		} catch (SecurityException e) {
			// We can't know: we assume that it's up
			return true;
		} catch (NoSuchMethodException e) {
			// We can't know: we assume that it's up
			return true;
		}
		return netIf.isUp();
	}
	
	public static String getIpAddress() {
		try{
			final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netIf : Collections.list(nets)) {
	        	// If the network is active
	            if(isUp(netIf)){
	                Enumeration<InetAddress> addresses = netIf.getInetAddresses();
	                for(InetAddress addr : Collections.list(addresses)) 
	                	// If the IP address is IPv4 and it's not the local address, store it
	                    if(addr.getAddress().length == 4 && !addr.isLoopbackAddress())
	                    	return addr.getHostAddress();
	            }
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String generateBaseUrl(int port, String basePath) throws SpaceManagerException {
		final String myIpAddress = getIpAddress();
		if(myIpAddress == null)
			throw new SpaceManagerException("No ip address obtained: use join(Node)");
		return "http://" + myIpAddress + ":" + port + basePath;
	}
	
	public String selfJoin(int port, String uuid) throws SpaceManagerException {
		return selfJoin(port, "/", uuid, true, false, false);
	}
	
	public String selfJoin(int port, String uuid, boolean reachable, boolean mustPoll, boolean isBulletinBoard) throws SpaceManagerException {
		return selfJoin(port, "/", uuid, reachable, mustPoll, isBulletinBoard);
	}

	public String selfJoin(int port, String basePath, String uuid, boolean reachable, boolean mustPoll, boolean isBulletinBoard) throws SpaceManagerException {
		final Node node = new Node(generateBaseUrl(port, basePath), uuid, reachable, isBulletinBoard, mustPoll);
		return join(node);
	}

	/* (non-Javadoc)
	 * @see otsopack.full.java.network.coordination.ISpaceManager#poll(java.lang.String)
	 */
	@Override
	public void poll(String secret) throws SpaceManagerException {
		final ClientResource client = createClientResource(this.uri + StatesResource.ROOT + "/" + secret);
		try{
			try{
				client.put(new StringRepresentation("", MediaType.APPLICATION_JSON), MediaType.APPLICATION_JSON);
			}catch(ResourceException e){
				throw new SpaceManagerException("Could not poll space manager " + this.uri + ": " + e.getMessage(), e);
			}
		}finally{
			client.release();
		}
	}

	/* (non-Javadoc)
	 * @see otsopack.full.java.network.coordination.ISpaceManager#leave(java.lang.String)
	 */
	@Override
	public void leave(String secret) throws SpaceManagerException {
		final ClientResource client = createClientResource(this.uri + StatesResource.ROOT + "/" + secret);
		try{
			try{
				client.delete(MediaType.APPLICATION_JSON);
			}catch(ResourceException e){
				throw new SpaceManagerException("Could not leave from space manager " + this.uri + ": " + e.getMessage(), e);
			}
		}finally{
			client.release();
		}
	}

	@Override
	public void startup() throws SpaceManagerException {
	}

	@Override
	public void shutdown() throws SpaceManagerException {
	}
}