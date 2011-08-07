/*****************************************************************************
 * All public interface based on Starteam API are a property of Borland, 
 * those interface are reproduced here only for testing purpose. You should
 * never use those interface to create a competitive product to the Starteam
 * Server. 
 * 
 * The implementation is given AS-IS and should not be considered a reference 
 * to the API. The behavior on a lots of method and class will not be the
 * same as the real API. The reproduction only seek to mimic some basic 
 * operation. You will not found anything here that can be deduced by using
 * the real API.
 * 
 * Fake-Starteam is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package org.ossnoize.fakestarteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.starbase.starteam.SimpleTypedResource;

public class SimpleTypedResourceIDProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5926765067144233123L;
	private static final String resourceIDFile = "resourceid.gz";
	private static SimpleTypedResourceIDProvider provider = null;
	
	private Set<Integer> assignedResourceID = new HashSet<Integer>();

	private Random generator = new Random(); 
	
	private SimpleTypedResourceIDProvider() {	
	}

	public static SimpleTypedResourceIDProvider getProvider() {
		if(null == provider) {
			if(!readFromFile()) {
				provider = new SimpleTypedResourceIDProvider();
			}
		}
		return provider;
	}
	
	private static boolean readFromFile() {
		boolean ret = false;
		ObjectInputStream in = null;
		GZIPInputStream gzin = null;
		
		try {
			File rootDir = InternalPropertiesProvider.getInstance().getFile();
			File path = new File(rootDir.getCanonicalPath() + File.separator + resourceIDFile);
			if(path.exists()) {
				gzin = new GZIPInputStream(new FileInputStream(path));
				in = new ObjectInputStream(gzin);
				Object obj = in.readObject();
				if(obj instanceof SimpleTypedResourceIDProvider) {
					provider = (SimpleTypedResourceIDProvider) obj;
					ret = true;
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
			if(gzin != null) {
				try {
					gzin.close();
				} catch (IOException e) {}
			}
		}
		return ret;
	}

	public int registerNew(SimpleTypedResource resource) {
		int id = generator.nextInt();
		if(assignedResourceID.contains(id))
		{
			return registerNew(resource);
		}
		else
		{
			assignedResourceID.add(id);
			saveNewID();
		}
		return id;
	}

	private void saveNewID() {
		GZIPOutputStream gzout = null;
		ObjectOutputStream out = null;

		try {
			File rootDir = InternalPropertiesProvider.getInstance().getFile();
			File path = new File(rootDir.getCanonicalPath() + File.separator + resourceIDFile);
			
			gzout = new GZIPOutputStream(new FileOutputStream(path));
			out = new ObjectOutputStream(gzout);
			out.writeObject(this);
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			if(gzout != null) {
				try {
					gzout.close();
				} catch (IOException e) {}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}
	
}