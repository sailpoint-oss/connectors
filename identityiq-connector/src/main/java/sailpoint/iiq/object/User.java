package sailpoint.iiq.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import openconnector.Connector;
import openconnector.Item;
import openconnector.Item.Operation;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("rawtypes")
public class User extends Resource {

	public static final String SCHEMA_KEY_ATTRIBUTES = "attributes.";
	
	@SerializedName("urn:ietf:params:scim:schemas:sailpoint:1.0:User")
	Map<String,Object> attributes;
	
	Name name;
	String displayName;
	String userName;
	boolean active;
	List<PhoneNumber> phoneNumbers;
	List<Email> emails;
	List<Address> addresses;
	List<String> schemas;
	String password; 
	
	public User() {
		super();
		this.name = new Name();
		this.displayName = null;
		this.userName = null;
		this.active = false;
		this.phoneNumbers = new ArrayList<PhoneNumber>();
		this.emails = new ArrayList<Email>();
		this.addresses = new ArrayList<Address>();
		this.schemas = new ArrayList<String>();
		this.schemas.add( "urn:ietf:params:scim:schemas:sailpoint:1.0:User" );
		this.schemas.add( "urn:ietf:params:scim:schemas:core:2.0:User" );
		this.schemas.add( "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User" );
		this.password = null;
		this.attributes = new HashMap<String,Object>();
	}
	
	public void modify( List<Item> items ) {
		
		if ( items != null && !items.isEmpty() ) {
			for ( Item item : items ) {
				modify ( item );
			}
		}
		
	}
	
	public void modify( Item item ) {
		
		if ( item != null ) {
			
			String itemName = item.getName();
			
			Object itemValue = item.getValue();
			
			switch( itemName ) {
			
				case "displayName": 
					this.displayName = (String) ( ( item.getOperation() != Operation.Remove ) ? item.getValue() : null );
					break;
				case "userName":
					this.userName = (String) ( ( item.getOperation() != Operation.Remove ) ? item.getValue() : null );
					break;
				case "active":
					this.active = (boolean) ( ( item.getOperation() != Operation.Remove ) ? item.getValue() : false );
					break;
				case "password":
					this.password = (String) ( ( item.getOperation() != Operation.Remove ) ? item.getValue() : null );
					break;
				default:
					
					if ( itemName.startsWith( SCHEMA_KEY_ATTRIBUTES ) ){
						
						this.attributes.put( StringUtils.removeStart( itemName, SCHEMA_KEY_ATTRIBUTES ), itemValue );
					
					} else if ( itemName.startsWith( Name.SCHEMA_KEY ) ) {
					
						this.name.modify( item );
					
					} else if ( itemName.startsWith( Metadata.SCHEMA_KEY ) ) {
					
						this.metadata.modify( item );
					
					} else if ( itemName.startsWith( Address.SCHEMA_KEY ) ) {
						
						for ( Address address : this.addresses ) {
							if ( address.matches( itemName ) ) {
								address.modify( item );
								this.addresses.add( address );
							}
						}			
					} else if ( itemName.startsWith( Email.SCHEMA_KEY ) ) {
						
						String[] itemNames = StringUtils.split( itemName, "." );
						
						/*
						 * If we don't have any emails already, then this is easy.  Just add an email to the existing email list.
						 */
						if ( emails != null && emails.isEmpty() ) {
							
							emails.add( new Email ( (String) itemValue, itemNames[1], true ) );
							
						/*
						 * If we already have emails in the list, then we'll need to see what we update.
						 */
						} else {
							for ( Email email : this.emails ) {
								if ( email.matches( itemName ) ) {
									this.emails.remove( email );
									email.modify( item );
									this.emails.add( email );
								}
							}						
						}
					} else if ( itemName.startsWith( PhoneNumber.SCHEMA_KEY ) ) {
						for ( PhoneNumber phoneNumber : this.phoneNumbers ) {
							if ( phoneNumber.matches( itemName ) ) {
								 phoneNumber.modify( item );
								 this.phoneNumbers.add( phoneNumber );
							}
						}			
					} else if ( itemName.startsWith( "manager." ) ) {				
						// Do some sort of manager operation...
					}					
					break;
			}
		}
	}	
	
	public String toString() {
		return new Gson().toJson( this );
	}
	
	public Map<String, Object> getMap() {
		
		Map<String, Object> map = super.getMap();
	    
		map.put( "displayName", this.displayName );
	    map.put( "userName", this.userName );
	    map.put( "active",  (Boolean) this.active );
	    map.put( Connector.ATT_DISABLED, (Boolean) !this.active );
	    map.putAll( name.getMap() );
	    
	    if ( emails != null && !emails.isEmpty() ) {
		    for ( Email email : emails ) {
		    	map.putAll( email.getMap() );
		    }	    	
	    }

	    if ( addresses != null && !addresses.isEmpty() ) {
	    	for ( Address address : addresses ) {
	    		map.putAll( address.getMap() );
	    	}
	    }
	    
	    if ( phoneNumbers != null && !phoneNumbers.isEmpty() ) {
	    	for ( PhoneNumber phoneNumber : phoneNumbers ) {
	    		map.putAll( phoneNumber.getMap() );
	    	}
	    }
	    
	    if ( attributes != null ) {
	    	
	    	for ( Map.Entry<String, Object> entry : attributes.entrySet() ) {
	    		
	    		String key = entry.getKey();
	    		Object value = entry.getValue();
	    		
	    		if ( value instanceof String )
	    			map.put( SCHEMA_KEY_ATTRIBUTES + key, value );
	    		if ( value instanceof Integer )
	    			map.put( SCHEMA_KEY_ATTRIBUTES + key, value );
	    		if ( value instanceof Double )
	    			map.put( SCHEMA_KEY_ATTRIBUTES + key, ((Double) value).intValue() );
	    		if ( value instanceof Boolean )
	    			map.put( SCHEMA_KEY_ATTRIBUTES + key, value );
	    		if ( value instanceof List )
	    			map.put( SCHEMA_KEY_ATTRIBUTES + key, value );
	    		if ( value instanceof Map ) {
	    			
	    			Map valueMap = (HashMap) value;
	    			
	    			if ( valueMap.containsKey( "value" ) ){
	    				map.put( SCHEMA_KEY_ATTRIBUTES + key, valueMap.get( "value" ) );
	    			}	
	    		}
		    }
	    }
		return map;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails( List<Email> emails ) {
		this.emails = emails;
	}

	public Map getAttributes() {
		return attributes;
	}

	@SuppressWarnings("unchecked")
	public void setAttributes( Map attributes ) {
		this.attributes = attributes;
	}
	
	public void setAttribute( String key, Object value ) {
		if ( attributes != null )
			attributes.put( key, value );
	}
	
	public void addAttributes( Map<String,Object> map ) {
		if ( attributes != null )
			attributes.putAll( map );
	}
	
	public Object getAttribute( String key ) {
		return ( attributes != null ) ? attributes.get( key ) : null;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public class Address {
		
		public static final String SCHEMA_KEY = "addresses.";
		
		private static final String SCHEMA_KEY_ADDRESS_FORMATTED = "formatted";
		private static final String SCHEMA_KEY_ADDRESS_STREET = "streetAddress";
		private static final String SCHEMA_KEY_ADDRESS_LOCALITY = "locality";
		private static final String SCHEMA_KEY_ADDRESS_REGION = "region";
		private static final String SCHEMA_KEY_ADDRESS_POSTAL_CODE = "postalCode";
		private static final String SCHEMA_KEY_ADDRESS_COUNTRY = "country";
		private static final String SCHEMA_KEY_ADDRESS_TYPE = "type";
				
		String formatted;
		String streetAddress;
		String locality;
		String region;
		String postalCode;
		String country;
		String type;
		
		public Map<String, Object> getMap() {
			Map<String, Object> map = new HashMap<String,Object>();			
			map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_ADDRESS_FORMATTED, this.formatted );
			map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_ADDRESS_STREET, this.streetAddress );
			map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_ADDRESS_LOCALITY, this.locality );
			map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_ADDRESS_REGION, this.region );
			map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_ADDRESS_POSTAL_CODE, this.postalCode );
			map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_ADDRESS_COUNTRY, this.country );
			return map;
		}
		
	    public boolean matches(String itemName) {
			// TODO Auto-generated method stub
			return false;
		}

		public void modify( Item item ) {
			
			if ( item != null ) {

				if ( item.getOperation() == Operation.Add || item.getOperation() == Operation.Set ){
						
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_FORMATTED ) )
						setFormatted( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_STREET ) )
						setStreetAddress( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_LOCALITY ) )
						setLocality( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_REGION ) )
						setRegion( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_POSTAL_CODE ) )
						setPostalCode( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_COUNTRY ) )
						setCountry( (String) item.getValue() );
						
				} else if ( item.getOperation() == Operation.Remove ) {
						
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_FORMATTED ) )
						setFormatted( null );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_STREET ) )
						setStreetAddress( null );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_LOCALITY ) )
						setLocality( null );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_REGION ) )
						setRegion( null );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_POSTAL_CODE ) )
						setPostalCode( null );
					if ( item.getName().endsWith( SCHEMA_KEY_ADDRESS_COUNTRY ) )
						setCountry( null );
				}				
			}
	    }

		public String getFormatted() {
			return formatted;
		}

		public void setFormatted(String formatted) {
			this.formatted = formatted;
		}

		public String getStreetAddress() {
			return streetAddress;
		}

		public void setStreetAddress(String streetAddress) {
			this.streetAddress = streetAddress;
		}

		public String getLocality() {
			return locality;
		}

		public void setLocality(String locality) {
			this.locality = locality;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}
		
	}
	
	public class Name {
		
		public static final String SCHEMA_KEY = "name.";
		
		private static final String SCHEMA_KEY_NAME_FORMATTED = "formatted";
		private static final String SCHEMA_KEY_NAME_FAMILY_NAME = "familyName";
		private static final String SCHEMA_KEY_NAME_GIVEN_NAME = "givenName";
		private static final String SCHEMA_KEY_NAME_MIDDLE_NAME =  "middleName";
		private static final String SCHEMA_KEY_NAME_HONORIFIC_PREFIX =  "honorificPrefix";
		private static final String SCHEMA_KEY_NAME_HONORIFIC_SUFFIX =  "honorificSuffix";
		
		String formatted;
		String familyName;
		String givenName;
		String middleName;
		String honorificPrefix;
		String honorificSuffix;
		
		public Map<String, Object> getMap() {
			
			Map<String, Object> map = new HashMap<String,Object>();
			
			map.put( SCHEMA_KEY + SCHEMA_KEY_NAME_FORMATTED, this.formatted );
			map.put( SCHEMA_KEY + SCHEMA_KEY_NAME_FAMILY_NAME, this.familyName );
			map.put( SCHEMA_KEY + SCHEMA_KEY_NAME_GIVEN_NAME, this.givenName );
			map.put( SCHEMA_KEY + SCHEMA_KEY_NAME_MIDDLE_NAME, this.middleName );
			map.put( SCHEMA_KEY + SCHEMA_KEY_NAME_HONORIFIC_PREFIX, this.honorificPrefix );
			map.put( SCHEMA_KEY + SCHEMA_KEY_NAME_HONORIFIC_SUFFIX, this.honorificSuffix );
			
			return map;
		}
		
		public void modify( Item item ) {
			
			if ( item != null ) {

				if ( item.getOperation() == Operation.Add || item.getOperation() == Operation.Set ){
					
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_FORMATTED ) )
						setFormatted( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_FAMILY_NAME ) )
						setFamilyName( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_GIVEN_NAME ) )
						setGivenName( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_MIDDLE_NAME ) )
						setMiddleName( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_HONORIFIC_PREFIX ) )
						setHonorificPrefix( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_HONORIFIC_SUFFIX ) )
						setHonorificSuffix( (String) item.getValue() );
					
				} else if ( item.getOperation() == Operation.Remove ) {
					
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_FORMATTED ) )
						setFormatted( null );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_FAMILY_NAME ) )
						setFamilyName( null );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_GIVEN_NAME ) )
						setGivenName( null );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_MIDDLE_NAME ) )
						setMiddleName( null );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_HONORIFIC_PREFIX ) )
						setHonorificPrefix( null );
					if ( item.getName().endsWith( SCHEMA_KEY_NAME_HONORIFIC_SUFFIX ) )
						setHonorificSuffix( null );
				}				
			}
		}
		
		public String getFormatted() {
			return formatted;
		}
		public void setFormatted(String formatted) {
			this.formatted = formatted;
		}
		public String getFamilyName() {
			return familyName;
		}
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}
		public String getGivenName() {
			return givenName;
		}
		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}
		public String getMiddleName() {
			return middleName;
		}
		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}
		public String getHonorificPrefix() {
			return honorificPrefix;
		}
		public void setHonorificPrefix(String honorificPrefix) {
			this.honorificPrefix = honorificPrefix;
		}
		public String getHonorificSuffix() {
			return honorificSuffix;
		}
		public void setHonorificSuffix(String honorificSuffix) {
			this.honorificSuffix = honorificSuffix;
		}
	}
	
	public class PhoneNumber {
		
		public static final String SCHEMA_KEY = "phoneNumbers.";
		
		private static final String SCHEMA_KEY_PHONE_VALUE = "value";
		private static final String SCHEMA_KEY_PHONE_TYPE = "type";
		
		String value;
		String type;
		
		public Map<String, Object> getMap() {
			Map<String, Object> map = new HashMap<String,Object>();
	    	map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_PHONE_VALUE, this.value );
	    	return map;
		}
		
	    public boolean matches(String itemName) {
			// TODO Auto-generated method stub
			return false;
		}

		public void modify( Item item ) {
			
			if ( item != null ) {

				if ( item.getOperation() == Operation.Add || item.getOperation() == Operation.Set ){
						
					if ( item.getName().endsWith( SCHEMA_KEY_PHONE_VALUE ) )
						setValue( (String) item.getValue() );
					if ( item.getName().endsWith( SCHEMA_KEY_PHONE_TYPE ) )
						setType( (String) item.getValue() );
						
				} else if ( item.getOperation() == Operation.Remove ) {
						
					if ( item.getName().endsWith( SCHEMA_KEY_PHONE_VALUE ) )
						setValue( null );
					if ( item.getName().endsWith( SCHEMA_KEY_PHONE_TYPE ) )
						setType( null );
				}				
			}
	    }

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	public class Email {
		
		public static final String SCHEMA_KEY = "emails.";
		
		private static final String SCHEMA_KEY_EMAIL_VALUE = "value";
		private static final String SCHEMA_KEY_EMAIL_TYPE = "type";
		private static final String SCHEMA_KEY_EMAIL_PRIMARY = "primary";
	    
		String value;
	    String type;
	    boolean primary;
	    
	    public Email( String value ) {
			super();
			this.value = value;
			this.type = "work";
			this.primary = false;
		}
	    
	    public Email( String value, String type ) {
			super();
			this.value = value;
			this.type = type;
			this.primary = false;
		}
	    
	    public Email( String value, String type, boolean primary ) {
			super();
			this.value = value;
			this.type = type;
			this.primary = primary;
		}

		public Map<String, Object> getMap() {
	    	Map<String, Object> map = new HashMap<String,Object>();
	    	map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_EMAIL_VALUE, this.value );
	    	map.put( SCHEMA_KEY + this.type + "." + SCHEMA_KEY_EMAIL_PRIMARY, this.primary );
	    	return map;
	    }
	    
		public boolean matches( String itemName ) {
	    	
	    	System.out.println( "itemName: " + itemName );
	    	System.out.println( "type: " + this.type );
	    	System.out.println( "Contained?: " + StringUtils.containsIgnoreCase( itemName, this.type ) );
	    	
	    	return StringUtils.containsIgnoreCase( itemName, this.type );
		}

		public void modify( Item item ) {
				
			if ( item != null ) {

				if ( item.getOperation() == Operation.Add || item.getOperation() == Operation.Set ){
						
					if ( item.getName().endsWith( SCHEMA_KEY_EMAIL_VALUE ) )
						this.value = (String) item.getValue();
					if ( item.getName().endsWith( SCHEMA_KEY_EMAIL_TYPE ) )
						this.type = (String) item.getValue();
					if ( item.getName().endsWith( SCHEMA_KEY_EMAIL_PRIMARY ) )
						this.primary = (boolean) item.getValue();
						
				} else if ( item.getOperation() == Operation.Remove ) {
						
					if ( item.getName().endsWith( SCHEMA_KEY_EMAIL_VALUE ) )
						this.value = null;
					if ( item.getName().endsWith( SCHEMA_KEY_EMAIL_TYPE ) )
						this.type = null;
					if ( item.getName().endsWith( SCHEMA_KEY_EMAIL_PRIMARY ) )
						this.primary = false;
				}				
			}
	    }

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isPrimary() {
			return primary;
		}

		public void setPrimary(boolean primary) {
			this.primary = primary;
		}	      
	}
}
