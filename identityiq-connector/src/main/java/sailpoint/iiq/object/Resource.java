package sailpoint.iiq.object;

import java.util.HashMap;
import java.util.Map;

import openconnector.Item;

import com.google.gson.annotations.SerializedName;

public class Resource {

	@SerializedName("id")
	String id;
	
	@SerializedName("externalId")
	String externalId;
	
	@SerializedName("meta")
	Metadata metadata;
	
	public Map<String, Object> getMap() {
		
    	Map<String, Object> map = new HashMap<String,Object>();
    	
    	map.put( "id", this.id );
    	map.put( "externalId", this.externalId );
    	
		return map;
	}
	
	public Resource() {
		super();
		this.id = null;
		this.externalId = null;
		this.metadata = new Metadata();
	}
	
	public Resource(String id, String externalId, Metadata metadata) {
		super();
		this.id = id;
		this.externalId = externalId;
		this.metadata = metadata;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public class ComplexValue {
		Map attributes;
	}
	
	/*
	 * Metadata
	 */
	public class Metadata {
		
		public static final String SCHEMA_KEY = "meta.";
		
		private static final String SCHEMA_KEY_META_CREATED = "created";
		private static final String SCHEMA_KEY_META_LAST_MODIFIED = "lastModified";
		private static final String SCHEMA_KEY_META_LOCATION = "location";
		private static final String SCHEMA_KEY_META_VERSION = "version";
		private static final String SCHEMA_KEY_META_RESOURCE_TYPE = "resourceType";
		
		String created;
		String lastModified;
		String location;
		String version;
		String resourceType;
	    
	    public Map<String, Object> getMap() {
			Map<String, Object> map = new HashMap<String,Object>();
			map.put( SCHEMA_KEY + SCHEMA_KEY_META_CREATED, this.created );
			map.put( SCHEMA_KEY + SCHEMA_KEY_META_LAST_MODIFIED, this.lastModified );
			map.put( SCHEMA_KEY + SCHEMA_KEY_META_LOCATION, this.location );
			map.put( SCHEMA_KEY + SCHEMA_KEY_META_VERSION, this.version );
			map.put( SCHEMA_KEY + SCHEMA_KEY_META_RESOURCE_TYPE, this.resourceType );
			return map;
		}
	    
		public void modify(Item item) {
			// TODO Auto-generated method stub			
		}	 
	    
		public String getResourceType() {
			return resourceType;
		}
		public void setResourceType(String resourceType) {
			this.resourceType = resourceType;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getCreated() {
			return created;
		}
		public void setCreated(String created) {
			this.created = created;
		}
		public String getLastModified() {
			return lastModified;
		}
		public void setLastModified(String lastModified) {
			this.lastModified = lastModified;
		}

   
	}
}
