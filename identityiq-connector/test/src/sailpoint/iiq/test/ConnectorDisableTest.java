package sailpoint.iiq.test;

import openconnector.ConnectorConfig;
import openconnector.Result;
import openconnector.SystemOutLog;
import sailpoint.iiq.connector.IIQConnector;

import com.google.gson.Gson;

public class ConnectorDisableTest {

	public static void main(String[] args) {
	
		IIQConnector connector = new IIQConnector();
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAttribute( IIQConnector.ARGS_URL, "http://localhost:8080/identityiq" );
		config.setAttribute( IIQConnector.ARGS_USER,  "spadmin" );
		config.setAttribute( IIQConnector.ARGS_PASSWORD, "admin" );
		config.setAttribute( IIQConnector.ARGS_PAGE_SIZE, "100" );
		connector.configure( config, new SystemOutLog() );
		
		Result result = connector.disable( "user0005@a1.local", null );
		
		Gson gson = new Gson();
		
		System.out.println( gson.toJson( result ) );
		
	}

}
