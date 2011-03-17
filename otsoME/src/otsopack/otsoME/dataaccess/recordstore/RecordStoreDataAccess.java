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
package otsopack.otsoME.dataaccess.recordstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.apache.log4j.Logger;

import otsopack.commons.data.Graph;
import otsopack.commons.data.SemanticFormat;
import otsopack.commons.data.Template;
import otsopack.commons.dataaccess.IDataAccess;
import otsopack.commons.exceptions.SpaceAlreadyExistsException;
import otsopack.commons.exceptions.SpaceNotExistsException;
import otsopack.commons.exceptions.TSException;
import otsopack.commons.exceptions.UnsupportedTemplateException;
import otsopack.otsoME.dataaccess.recordstore.space.RecordFactory;
import otsopack.otsoME.dataaccess.recordstore.space.SpaceRecord;

public class RecordStoreDataAccess implements IDataAccess {
	private final static Logger log = Logger.getInstance(RecordStoreDataAccess.class.getName());
	
	public static final String storeName = "METADATA";
	public static final String SPACE_STORE_PREFIX = "SP_";
	public static int RECORDNAME_MAX_CHARS = 32;
	RecordStore metadata = null;
	StoreUpdater updater = null;
	Thread thUpdater = null;
	Vector/*<SpaceRecord>*/ spaces = null;
	Vector/*<SpaceRecord>*/ joinedSpaces = null;
	final Object lockJoinedSpaces = new Object();
	
	public RecordStoreDataAccess() {
		spaces = new Vector();
		joinedSpaces = new Vector();
		updater = new StoreUpdater(this);
	}

	protected SpaceRecord getSpace(String spaceURI) throws SpaceNotExistsException {
		//TODO spaceURI = Util.normalizeSpaceURI(spaceURI, "");
		for(int i=0; i<spaces.size(); i++) {
			if(((SpaceRecord)spaces.elementAt(i)).getSpaceURI().equals(spaceURI))
				return (SpaceRecord)spaces.elementAt(i);
		}
		throw new SpaceNotExistsException();
	}
	
	protected SpaceRecord getJoinedSpace(String spaceURI) throws SpaceNotExistsException {
		//TODO spaceURI = Util.normalizeSpaceURI(spaceURI, "");
		for(int i=0; i<joinedSpaces.size(); i++) {
			if(((SpaceRecord)joinedSpaces.elementAt(i)).getSpaceURI().equals(spaceURI)) {
				return (SpaceRecord)joinedSpaces.elementAt(i);
			}
		}
		throw new SpaceNotExistsException();
	}
	
	public Vector getJoinedSpaces() {
		return (joinedSpaces.size()==0)?null:joinedSpaces;
	}
	
		//TODO check!
		//We suppose that only a instance of tscME would be running in each mobile phone
		//Otherwise, we should check metadata RecordStore instead of spaces Vector.
		private boolean recordExists(String recordName) {
			for(int i=0; i<spaces.size(); i++) {
				if(((SpaceRecord)spaces.elementAt(i)).getRecordStoreName().equals(recordName))
					return true;
			}
			return false;
		}
		
		private String getRandomName(int length) {
			String ret = "";
			Random generator = new Random();
			generator.setSeed(System.currentTimeMillis());
			while(ret.length()<length) {
				int i = generator.nextInt();
				i = (i<0)? -i: i;
				int j = ('z'-'a')+1;
				char f = (char) ( 'a' + ( i % j ) );
				ret +=  String.valueOf(f);
			}
			return ret;
		}
	
	private String generateStoreName(String spaceURI) {
		/*String basename = "";
		for(int i=spaceURI.length()-1; i>=0 && (ret.length() < (RECORDNAME_MAX_CHARS - SPACE_STORE_PREFIX.length())); i--) {
			char c = spaceURI.charAt(i);
			if( (c>='a' && c<='z') || (c>='A' && c<='Z') ) {
				basename =  String.valueOf(c) + basename;
			}
		}*/
		String ret;
		do {
			ret = SPACE_STORE_PREFIX + getRandomName(RECORDNAME_MAX_CHARS-SPACE_STORE_PREFIX.length());
		} while( recordExists(ret) );
		return ret;
	}

	public void startup() throws TSException {
        try {
        	metadata = RecordStore.openRecordStore(storeName,true);
        	RecordEnumeration re = metadata.enumerateRecords(null, null, false);
        	ByteArrayInputStream bais = null;
        	DataInputStream inputStream = null;
	        while(re.hasNextElement()) {
		        try {	
		        	bais = new ByteArrayInputStream(re.nextRecord());
		            inputStream = new DataInputStream(bais);
					String spaceURI = inputStream.readUTF();
					String recordStoreName = inputStream.readUTF();
					spaces.addElement( RecordFactory.createSpaceRecord(spaceURI, recordStoreName) );
		        } catch (IOException e) {
					e.printStackTrace();
					throw new TSException(e.getMessage());
				}
	        }
	 	} catch (RecordStoreException e) {
	 		throw new TSException(e.getMessage());
	 	}
        
	 	thUpdater = new Thread(updater);
	 	thUpdater.start();
	}	
	
	public void shutdown() throws TSException {
		for(int i=0; i<joinedSpaces.size(); i++) {
			SpaceRecord sr = (SpaceRecord)joinedSpaces.elementAt(i);
			sr.shutdown();//TODO verify
			joinedSpaces.removeElement(sr);
		}
		try {
			metadata.closeRecordStore();
		} catch(RecordStoreException e) {
			throw new TSException(e.getMessage());
		}
		/* 	all the joinedSpaces had been already shutdowned,
			so only the previously shutdowned ones would remain
			without being updated in the RecordStore.
		*/
		updater.shutdown();
		try {
			thUpdater.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thUpdater = null;
	}
	
	public void createSpace(String spaceURI) throws SpaceAlreadyExistsException {
		try {
			getSpace(spaceURI);
			throw new SpaceAlreadyExistsException();
		} catch( SpaceNotExistsException sne ) {
			SpaceRecord sr = RecordFactory.createSpaceRecord(spaceURI, generateStoreName(spaceURI));
			spaces.addElement(sr);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
		   	try {
				outputStream.writeUTF(sr.getSpaceURI());
				outputStream.writeUTF(sr.getRecordStoreName());
				byte[] data = baos.toByteArray();
				metadata.addRecord(data, 0, data.length);
			} catch (RecordStoreException e) {
				e.printStackTrace();
        	} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void joinSpace(String spaceURI) throws SpaceNotExistsException {
		try {
			getJoinedSpace(spaceURI);
		} catch(SpaceNotExistsException sne) {
			SpaceRecord sr = getSpace(spaceURI);
			try {
				sr.startup();
			} catch (TSException e) {
				e.printStackTrace();
			}
			synchronized(lockJoinedSpaces) {
				joinedSpaces.addElement(sr);
			}
		}
	}

	public void leaveSpace(String spaceURI) throws SpaceNotExistsException {
		SpaceRecord sr = getJoinedSpace(spaceURI);
		synchronized(lockJoinedSpaces) {
			joinedSpaces.removeElement(sr);
		}
		updater.closeSpace(sr);
	}

	public Graph query(String spaceURI, Template template, SemanticFormat outputFormat) throws SpaceNotExistsException, UnsupportedTemplateException {
		final long start = System.currentTimeMillis();
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		final Graph ret = sr.query(template,outputFormat);
		log.debug("Query with template ("+(System.currentTimeMillis()-start)+"ms).");
		return ret;
	}

	public Graph read(String spaceURI, Template template, SemanticFormat outputFormat) throws SpaceNotExistsException, UnsupportedTemplateException {
		final long start = System.currentTimeMillis();
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		final Graph ret = sr.read(template,outputFormat);
		log.debug("Read with template ("+(System.currentTimeMillis()-start)+"ms).");
		return ret;
	}

	public Graph read(String spaceURI, String graphURI, SemanticFormat outputFormat) throws SpaceNotExistsException {
		final long start = System.currentTimeMillis();
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		final Graph ret = sr.read(graphURI,outputFormat);
		log.debug("Read with uri ("+(System.currentTimeMillis()-start)+"ms).");
		return ret;
	}

	public Graph take(String spaceURI, Template template, SemanticFormat outputFormat) throws SpaceNotExistsException, UnsupportedTemplateException {
		final long start = System.currentTimeMillis();
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		final Graph ret = sr.take(template,outputFormat);
		log.debug("Take with template ("+(System.currentTimeMillis()-start)+"ms).");
		return ret;
	}

	public Graph take(String spaceURI, String graphURI, SemanticFormat outputFormat) throws SpaceNotExistsException {
		final long start = System.currentTimeMillis();
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		final Graph ret = sr.take(graphURI,outputFormat);
		log.debug("Take with uri ("+(System.currentTimeMillis()-start)+"ms).");
		return ret;
	}

	public String write(String spaceURI, Graph triples) throws SpaceNotExistsException {
		final long start = System.currentTimeMillis();
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		final String ret = sr.write(triples);
		log.debug("Write ("+(System.currentTimeMillis()-start)+"ms).");
		return ret;
	}
	
	public String[] getLocalGraphs(String spaceURI) throws SpaceNotExistsException {
		final SpaceRecord sr = getJoinedSpace(spaceURI);
		return sr.getLocalGraphs();
	}
}