package sailpoint.iiq.connector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import openconnector.AbstractConnector;
import openconnector.ConnectorConfig;
import openconnector.ConnectorException;
import openconnector.Filter;
import openconnector.Item;
import openconnector.ObjectNotFoundException;
import openconnector.Result;
import openconnector.Result.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sailpoint.iiq.object.Resources;
import sailpoint.iiq.object.User;
import sailpoint.tools.Base64;

import com.google.gson.Gson;

public class IIQConnector extends AbstractConnector {

	private static Log log = LogFactory.getLog( IIQConnector.class );
	
	/*
	 * Args for the Application definition.
	 */
	public static final String ARGS_URL = "host";
    public static final String ARGS_USER = "user";
    public static final String ARGS_PASSWORD = "password";
    public static final String ARGS_PAGE_SIZE = "pageSize";
    public static final String ARGS_ACCOUNT_FILTER = "accountFilter";
  
    /*
     * Default HTTP Headers
     */
    private static final String HTTP_HEADER_AUTHORIZATION_KEY = "authorization";
    private static final String HTTP_HEADER_CACHE_CONTROL_KEY = "cache-control";
    private static final String HTTP_HEADER_CACHE_CONTROL_VALUE = "no-cache";
    private static final String HTTP_HEADER_CONTENT_TYPE_KEY = "content-type";
    private static final String HTTP_HEADER_CONTENT_TYPE_VALUE = "application/json";
    
    /*
     * Query Parameters
     */
    private static final String QUERY_PARAM_START_INDEX = "startIndex";
    private static final String QUERY_PARAM_COUNT = "count";
    private static final String QUERY_PARAM_SORT_ORDER = "sortOrder";
    private static final String QUERY_PARAM_FILTER = "filter";
    
    /*
     * SCIM HTTP Endpoints
     */
    public static final String ENDPOINT_SCIM_USERS = "/scim/v2/Users/";
    public static final String ENDPOINT_SCIM_RESOURCE_TYPES = "/scim/v2/ResourceTypes/";
	
    /*
     * Defaults
     */
    public static final String DEFAULT_BASE_URL = "http://localhost:8080/identityiq";
    
    public static final int DEFAULT_PAGE_SIZE = 100;
    
    private static final String DEFAULT_BASIC_AUTH = "Basic c3BhZG1pbjphZG1pbg==";
    
    /*
     * Instance variables
     */
    String baseUrl = DEFAULT_BASE_URL;
    
    String authorization = DEFAULT_BASIC_AUTH;
    
    String user = "spadmin";
    
    String password = "admin";
    
    int pageSize = DEFAULT_PAGE_SIZE;
    
    String accountFilter = null;
    
    Gson gson = new Gson();
    
    /*
	 * This is an HTTP client, which we will re-use.
	 */
	OkHttpClient client = new OkHttpClient();
    
	@Override
	public void configure( ConnectorConfig config, openconnector.Log log) {
		
		super.configure( config, (openconnector.Log) log ); 
		
		this.baseUrl = StringUtils.removeEnd( config.getString( ARGS_URL ), "/" );
		
		this.user = config.getString( ARGS_USER );
		
		this.password = config.getString( ARGS_PASSWORD );
		
		this.authorization = setAuthorization( user, password );
		
		this.pageSize = Integer.parseInt( config.getString( ARGS_PAGE_SIZE ) );
		
		this.accountFilter = config.getString( ARGS_ACCOUNT_FILTER );
		
		this.gson = new Gson();
		
	}
    
    @Override
    public List<String> getSupportedObjectTypes() {
    	List<String> types = super.getSupportedObjectTypes();
        types.add( OBJECT_TYPE_ACCOUNT );
        return types;
    }

    /*
     * Overridden to support explicit features.
     */
    public List<Feature> getSupportedFeatures( String objectType ) {
        List<Feature> features = super.getSupportedFeatures( objectType );
        features.add( Feature.CREATE );
        features.add( Feature.UPDATE );
        features.add (Feature.DELETE );
        features.add( Feature.ENABLE );
        features.add( Feature.SET_PASSWORD );
        return features;
    }
	
    /*
     * Test connection method for SCIM connector, this method is used to test
     * whether the SCIM server is alive or not 
     */
    @Override
    public void testConnection() throws ConnectorException {
    	
    	OkHttpClient client = new OkHttpClient();
        		
        try {
        
        	Request request = new Request.Builder()
	  			.url( baseUrl + ENDPOINT_SCIM_RESOURCE_TYPES )
	  			.get()
	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  			.build();

        	Response response = client.newCall( request ).execute();
        	
        	if ( response == null )
        		throw new ConnectorException( "No response received from web-service call." );
        	else if ( !response.isSuccessful() )
        		throw new ConnectorException( "Received " + response.message() + " [" + response.code() + "] for request [" + request + "]" );
        	      
        } catch ( Exception e ) {
        
        	if ( e.getCause() != null) {
                throw new ConnectorException("Test Connection Failed: " + e.getCause() );
            } else {
                throw new ConnectorException("Test Connection Failed: " + e.getMessage() );
            }
        } 
    }
    
    @Override
	public Iterator<Map<String, Object>> iterate( Filter filter ) throws ConnectorException, UnsupportedOperationException {
		return new UserIterator( pageSize, accountFilter );
	}
    
    private class UserIterator implements Iterator<Map<String, Object>> {
	
    	List<User> users = new ArrayList<User>();
    	
    	PagedUsersIterator usersIterator = new PagedUsersIterator( 1, DEFAULT_PAGE_SIZE, null );
    	
		@SuppressWarnings("unused")
		UserIterator() {
    		usersIterator = new PagedUsersIterator( DEFAULT_PAGE_SIZE, null );
		}
    	
       	UserIterator( int pageSize, String accountFilter ) {
       		usersIterator = new PagedUsersIterator( pageSize, accountFilter );
    	}
	
    	@Override
    	public boolean hasNext() {
		
    		if ( users == null )
    			users = new ArrayList<User>();
    		
    		if ( users != null ) {
    			if ( !users.isEmpty() )
    				return true;
    			else if ( users.isEmpty() )
    				return usersIterator.hasNext();
    		}
    		
			return false;
    	}

    	@Override
    	public Map<String, Object> next() {
    		
    		User user = null; 
    		
    		if ( users != null ) {
    			
    			if ( users.isEmpty() ) {
    				
    				List<User> nextPageOfUsers = usersIterator.next();
    				
    				if ( nextPageOfUsers != null && !nextPageOfUsers.isEmpty() )
    					users.addAll( nextPageOfUsers );
    			}
    		
    			if ( !users.isEmpty() ) {
    				user = users.remove( 0 );
    			}
    		}
    		
    		log.debug( "Users size: " + users.size() );
    		log.debug( "Returning object: " + user );
    		
    		return ( user != null ) ? user.getMap() : new HashMap<String,Object>();
    	}
	}
    
    private class PagedUsersIterator implements Iterator<List<User>> {
    	
    	/*
    	 * Page Size, defaults to 100 records per page.
    	 */
    	int pageSize = 100;
    	
    	/*
    	 * This is the current index; We start at 1.
    	 */
    	int currentIndex = 1;
    	
    	/*
    	 * This is the total number of records we know about, used for hasNext().
    	 * -1 means that the totalResults has not been set yet.
    	 * 0 or more means that the totalResults
    	 */
    	int totalResults = -1;
    	
    	/*
    	 * Account filter string
    	 */
    	String accountFilter = null;
    	
    	@SuppressWarnings("unused")
		PagedUsersIterator ( ) {
    		this.currentIndex = 1;
    		this.pageSize = DEFAULT_PAGE_SIZE;
    		this.accountFilter = null;
    	}
    	
    	PagedUsersIterator ( int pageSize, String accountFilter  ) {
    		
    		this.currentIndex = 1;
    		
    		if ( pageSize < 1 ) {
    			log.error( "Paged Users Iterator: Bad page size detected. Falling back to default of " + DEFAULT_PAGE_SIZE + ".");
    			this.pageSize = DEFAULT_PAGE_SIZE;
    		} else {
    			this.pageSize = pageSize;
    		}
    		
    		this.accountFilter = accountFilter;   		
    	}
    	
    	PagedUsersIterator ( int startIndex, int pageSize, String accountFilter ) {
    		
    		this.currentIndex = startIndex;
    		
    		if ( pageSize < 1 ) {
    			log.error( "Paged Users Iterator: Bad page size detected. Falling back to default of " + DEFAULT_PAGE_SIZE + ".");
    			this.pageSize = DEFAULT_PAGE_SIZE;
    		} else {
    			this.pageSize = pageSize;
    		}
    		
    		this.accountFilter = accountFilter;
    	}
	
    	@Override
    	public boolean hasNext() {
		
    		/*
    		 * If we have a totalResult as -1 that means 
    		 * we don't know how many results there are, so we assume we can proceed.
    		 */
    		if ( totalResults == -1 )
    			return true;
    		else 
    			return currentIndex <= totalResults;
    	}

    	@SuppressWarnings("unchecked")
		@Override
    	public List<User> next() {
    		
    		log.info( "Paged Users Iterator: Fetching next page with settings currentIndex["+currentIndex+"], pageSize["+pageSize+"], totalResults["+totalResults+"], accountFilter["+accountFilter+"]" );
    		
    		OkHttpClient client = new OkHttpClient();
        	
        	List<User> users = null;
    		
        	try {
                
        		HttpUrl.Builder urlBuilder = HttpUrl.parse( baseUrl + ENDPOINT_SCIM_USERS ).newBuilder();
        		urlBuilder.addQueryParameter( QUERY_PARAM_START_INDEX, Integer.toString( currentIndex ) );
        		urlBuilder.addQueryParameter( QUERY_PARAM_COUNT, Integer.toString( pageSize ) );
        		urlBuilder.addQueryParameter( QUERY_PARAM_SORT_ORDER, "ascending" );
        		
        		if ( accountFilter != null )
        			urlBuilder.addQueryParameter( QUERY_PARAM_FILTER, accountFilter );
        		
        		Request request = new Request.Builder()
    	  			.url( urlBuilder.build() )
            		.get()
    	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
    	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
    	  			.build();
        		  	
            	Response response = client.newCall( request ).execute();
            	
            	evaluateResponse( response, request );
            	
            	String jsonString = response.body().string();
        		
            	Resources resources = gson.fromJson( jsonString, Resources.class );
            	
            	/*
            	 * This is the paged set of users which we will be returning.
            	 */
            	users = (List<User>) resources.getResources();
            	
            	/*
            	 * Update the totalResults based what our query indicates. 
            	 * We'll do a null check here to make sure we have a resource object.
            	 */
            	totalResults = (resources != null ) ? resources.getTotalResults() : 0;
            	
            	/*
            	 * Update the currentIndex for next iterations.
            	 */
            	currentIndex = currentIndex + pageSize;
            	
            } catch ( Exception e ) {
            
            	if ( e.getCause() != null) {
                    throw new ConnectorException("Account Aggregation Failed: " + e.getCause() );
                } else {
                    throw new ConnectorException("Account Aggregation Failed: " + e.getMessage() );
                }
            }
        	
        	/*
        	 * Return our list of users, if we have any.
        	 * Note: this can return null.
        	 */
        	return users;
    	}
    }
	
    @Override
    public Map<String, Object> read ( String id ) throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {
        
    	OkHttpClient client = new OkHttpClient();
    	
    	Map<String, Object> returnObject = null;
    	
    	try {
            
        	Request request = new Request.Builder()
	  			.url( baseUrl + ENDPOINT_SCIM_USERS + id )
	  			.get()
	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  			.build();

        	Response response = client.newCall( request ).execute();
        	
        	evaluateResponse( response, request );
        	
        	String jsonString = response.body().string();
    		
        	User user = gson.fromJson( jsonString, User.class );
        	
        	returnObject = user.getMap();
        	
        } catch ( Exception e ) {
        
        	if ( e.getCause() != null) {
                throw new ConnectorException("Account Aggregation Failed: " + e.getCause() );
            } else {
                throw new ConnectorException("Account Aggregation Failed: " + e.getMessage() );
            }
        }
    	
        return returnObject;
    }
	
    @Override
    public Result create( String id, List<Item> items ) {

    	Result result = new Result( Result.Status.Committed );
        
    	OkHttpClient client = new OkHttpClient();
    	
    	Response response = null;
    	
    	try {
        	
        	/*
        	 * Step 1. Create a new User object and modify it. 
        	 */
        	User user = new User();
        	user.setUserName( id );
        	user.modify( items );
        	
        	log.debug( gson.toJson( user ) );
        	
        	/*
        	 * Step 2. Submit the newly created user for creation.
        	 */
        	MediaType mediaType = MediaType.parse( HTTP_HEADER_CONTENT_TYPE_VALUE );
        	RequestBody body = RequestBody.create( mediaType, gson.toJson( user ) );
        	Request request = new Request.Builder()
        	  .url( baseUrl + ENDPOINT_SCIM_USERS )
        	  .post( body )
        	  .addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  		  .addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  		  .addHeader( HTTP_HEADER_CONTENT_TYPE_KEY, HTTP_HEADER_CONTENT_TYPE_VALUE )
        	  .build();
        	
        	response = client.newCall( request ).execute();
        	
        	log.debug( "IIQ Connector: Create called with request [" + request + "] and received response [" + response + "]" );
        	
        	result = getResultFromResponse( response, request );
         
        } catch (Exception e) {
            result = new Result( Result.Status.Failed );
            result.add( e.getMessage() );
        } finally {
        	response.body().close();
        }
        
        return result;
    }

    @Override
    public Result update( String id, List<Item> items ) {
    	
    	Result result = new Result( Result.Status.Committed );
        
    	OkHttpClient client = new OkHttpClient();
    	
    	try {
        	
        	/* 
        	 * Step 1. Get existing User object.
        	 */
          	Request request = new Request.Builder()
    	  			.url( baseUrl + ENDPOINT_SCIM_USERS + id )
    	  			.get()
    	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
    	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
    	  			.build();

            Response response = client.newCall( request ).execute();
            	
            result = getResultFromResponse( response, request );
            	
            String jsonString = response.body().string();
        	
            /*
             * Step 2. Parse the existing object, and modify it according to the plan.
             */
            User user = gson.fromJson( jsonString, User.class );
        	user.modify( items );
        	
        	/*
        	 * Step 3. Submit the modified User to the system.
        	 */
        	MediaType mediaType = MediaType.parse( HTTP_HEADER_CONTENT_TYPE_VALUE );
        	RequestBody body = RequestBody.create( mediaType, gson.toJson( user ) );
        	request = new Request.Builder()
        	  .url( baseUrl + ENDPOINT_SCIM_USERS + id )
        	  .put( body )
        	  .addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  		  .addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  		  .addHeader( HTTP_HEADER_CONTENT_TYPE_KEY, HTTP_HEADER_CONTENT_TYPE_VALUE )
        	  .build();
        	
        	response = client.newCall( request ).execute();
        	
        	log.debug( "IIQ Connector: Update called with request [" + request + "] and received response [" + response + "]" );
        	
        	result = getResultFromResponse( response, request );
        
        } catch (Exception e) {
            result = new Result( Result.Status.Failed );
            result.add( e.getMessage() );
            e.printStackTrace();
        } 
        
        return result;
    }
	
    @Override
    public Result delete( String id, Map<String, Object> options ) throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {
        
    	Result result = new Result( Result.Status.Committed );
        
    	OkHttpClient client = new OkHttpClient();
    	
        try {
        
        	Request request = new Request.Builder()
  			.url( baseUrl + ENDPOINT_SCIM_USERS + id )
  			.delete( null )
  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  		.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
  			.build();

        	Response response = client.newCall( request ).execute();
        	
        	log.debug( "IIQ Connector: Delete called with request [" + request + "] and received response [" + response + "]" );
        	
        	result = getResultFromResponse( response, request );
        
        } catch (Exception e) {
            result = new Result( Result.Status.Failed );
            result.add( e );
        } 
        
        return result;
    }
    
    
    
    @Override
	public Result disable( String id, Map<String, Object> options ) throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {
   	
    	Result result = new Result( Result.Status.Committed );
        
    	OkHttpClient client = new OkHttpClient();
    	
    	try {
        	
           	/* 
        	 * Step 1. Get existing User object.
        	 */
          	Request request = new Request.Builder()
    	  			.url( baseUrl + ENDPOINT_SCIM_USERS + id )
    	  			.get()
    	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
    	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
    	  			.build();

            Response response = client.newCall( request ).execute();
            	
            result = getResultFromResponse( response, request );
            	
            String jsonString = response.body().string();
        	
            /*
             * Step 2. Parse the existing object, and modify it according to the plan.
             */
            User user = gson.fromJson( jsonString, User.class );
        	user.setActive( false );
        	
        	/*
        	 * Step 3. Submit the modified User to the system.
        	 */
        	MediaType mediaType = MediaType.parse( HTTP_HEADER_CONTENT_TYPE_VALUE );
        	RequestBody body = RequestBody.create( mediaType, gson.toJson( user ) );
        	request = new Request.Builder()
        	  .url( baseUrl + ENDPOINT_SCIM_USERS + id )
        	  .put( body )
        	  .addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  		  .addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  		  .addHeader( HTTP_HEADER_CONTENT_TYPE_KEY, HTTP_HEADER_CONTENT_TYPE_VALUE )
        	  .build();
        	
        	response = client.newCall( request ).execute();
        	
        	log.debug( "IIQ Connector: Disable called with request [" + request + "] and received response [" + response + "]" );
        	
        	result = getResultFromResponse( response, request );
        
        } catch (Exception e) {
            result = new Result( Result.Status.Failed );
            result.add( e );
        } 
        
    	log.debug( result );
    	
        return result;
	}

	@Override
	public Result enable( String id, Map<String, Object> options ) throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {
   	
		Result result = new Result( Result.Status.Committed );
        
    	OkHttpClient client = new OkHttpClient();
    	
    	try {
        	
           	/* 
        	 * Step 1. Get existing User object.
        	 */
          	Request request = new Request.Builder()
    	  			.url( baseUrl + ENDPOINT_SCIM_USERS + id )
    	  			.get()
    	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
    	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
    	  			.build();

            Response response = client.newCall( request ).execute();
            	
            result = getResultFromResponse( response, request );
            	
            String jsonString = response.body().string();
        	
            /*
             * Step 2. Parse the existing object, and modify it according to the plan.
             */
            User user = gson.fromJson( jsonString, User.class );
        	user.setActive( true );
        	
        	/*
        	 * Step 3. Submit the modified User to the system.
        	 */
        	MediaType mediaType = MediaType.parse( HTTP_HEADER_CONTENT_TYPE_VALUE );
        	RequestBody body = RequestBody.create( mediaType, gson.toJson( user ) );
        	request = new Request.Builder()
        	  .url( baseUrl + ENDPOINT_SCIM_USERS + id )
        	  .put( body )
        	  .addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  		  .addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  		  .addHeader( HTTP_HEADER_CONTENT_TYPE_KEY, HTTP_HEADER_CONTENT_TYPE_VALUE )
        	  .build();
        	
        	response = client.newCall( request ).execute();
        	
        	log.debug( "IIQ Connector: Enable called with request [" + request + "] and received response [" + response + "]" );
        	
        	result = getResultFromResponse( response, request );
        
        } catch (Exception e) {
            result = new Result( Result.Status.Failed );
            result.add( e );
        } 
        
        return result;
	}

	@Override
    public Result setPassword( String id, String newPassword, String currentPassword, Date expiration, Map<String, Object> options ) throws ConnectorException, ObjectNotFoundException, UnsupportedOperationException {
        
    	Result result = new Result( Result.Status.Committed );
        
    	OkHttpClient client = new OkHttpClient();
    	
    	try {
        	
           	/* 
        	 * Step 1. Get existing User object.
        	 */
          	Request request = new Request.Builder()
    	  			.url( baseUrl + ENDPOINT_SCIM_USERS + id )
    	  			.get()
    	  			.addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
    	  			.addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
    	  			.build();

            Response response = client.newCall( request ).execute();
            	
            result = getResultFromResponse( response, request );
            	
            String jsonString = response.body().string();
        	
            /*
             * Step 2. Parse the existing object, and modify it according to the plan.
             */
            User user = gson.fromJson( jsonString, User.class );
        	user.setPassword( currentPassword );
        	
        	/*
        	 * Step 3. Submit the modified User to the system.
        	 */
        	MediaType mediaType = MediaType.parse( HTTP_HEADER_CONTENT_TYPE_VALUE );
        	RequestBody body = RequestBody.create( mediaType, gson.toJson( user ) );
        	request = new Request.Builder()
        	  .url( baseUrl + ENDPOINT_SCIM_USERS + id )
        	  .put( body )
        	  .addHeader( HTTP_HEADER_AUTHORIZATION_KEY, authorization )
	  		  .addHeader( HTTP_HEADER_CACHE_CONTROL_KEY, HTTP_HEADER_CACHE_CONTROL_VALUE )
	  		  .addHeader( HTTP_HEADER_CONTENT_TYPE_KEY, HTTP_HEADER_CONTENT_TYPE_VALUE )
        	  .build();
        	
        	response = client.newCall( request ).execute();
        	
        	result = getResultFromResponse( response, request );
        
        } catch (Exception e) {
            result = new Result( Result.Status.Failed );
            result.add( e );
        } 
        
        return result;
    }
       
    /*
     * Handles evaluation of various HTTP codes and translates them ConnectorExceptions.
     */
	private void evaluateResponse( Response response, Request request ) throws ConnectorException {
		
		if ( response != null ) {
			
			if ( !response.isSuccessful() ) {
				throw new ConnectorException( "Error: Received " + response.message() + " [" + response.code() + "] for request [" + request + "]" );
			}
			
		} else {
			throw new ConnectorException( "Error: No response received from web-service call." );
		}
		
	}
	
	private Result getResultFromResponse( Response response, Request request ) {
		
		Result result = new Result();
		
		if ( response != null ) {
			
			if ( response.isSuccessful() ) {
				result.setStatus( Status.Committed );
			} else {
				result.setStatus( Status.Failed );
				result.add( "Error: Received " + response.message() + " [" + response.code() + "] for request [" + request + "]" );
			}
			
		} else {
			result.setStatus( Status.Failed );
			result.add( "Error: No response received from web-service call." );
		}
		
		return result;
				
	}
	
	private String setAuthorization ( String user, String password ) {
		
		String authString = user + ":" + password;

		this.authorization = "Basic " + Base64.encodeBytes( authString.getBytes() );
		
		return this.authorization;

	}

}