package sailpoint.iiq.test;

import java.util.Iterator;
import java.util.Map;

import openconnector.ConnectorConfig;
import openconnector.Filter;
import openconnector.SystemOutLog;
import sailpoint.iiq.connector.IIQConnector;

public class ConnectorIterateTestWithFilter {

	public static void main(String[] args) {
	
		IIQConnector connector = new IIQConnector();
				
		ConnectorConfig config = new ConnectorConfig();
		config.setAttribute( IIQConnector.ARGS_URL, "http://localhost:8080/identityiq" );
		config.setAttribute( IIQConnector.ARGS_USER,  "spadmin" );
		config.setAttribute( IIQConnector.ARGS_PASSWORD, "admin" );
		config.setAttribute( IIQConnector.ARGS_PAGE_SIZE, "100" );
		config.setAttribute( IIQConnector.ARGS_ACCOUNT_FILTER, "userName ne \"spadmin\" and urn:ietf:params:scim:schemas:sailpoint:1.0:User:region eq \"Americas\"" );
		connector.configure( config, new SystemOutLog() );
		
		Filter filter = null;
		Iterator<Map<String,Object>> it = connector.iterate( filter );
		
		int count = 0;
		
		while ( it.hasNext() ) {
			
			System.out.println( it.next() );
		
			count++;
		}
		
		System.out.println( "Accounts iterated [" + count + "]" );

	}

}
