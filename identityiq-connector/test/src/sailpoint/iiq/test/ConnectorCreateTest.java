package sailpoint.iiq.test;

import java.util.ArrayList;

import openconnector.ConnectorConfig;
import openconnector.Item;
import openconnector.SystemOutLog;
import openconnector.Item.Operation;
import openconnector.Result;
import sailpoint.iiq.connector.IIQConnector;

import com.google.gson.Gson;

public class ConnectorCreateTest {

	public static void main(String[] args) {
	
		IIQConnector connector = new IIQConnector();
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAttribute( IIQConnector.ARGS_URL, "http://localhost:8080/identityiq" );
		config.setAttribute( IIQConnector.ARGS_USER,  "spadmin" );
		config.setAttribute( IIQConnector.ARGS_PASSWORD, "admin" );
		config.setAttribute( IIQConnector.ARGS_PAGE_SIZE, "100" );
		connector.configure( config, new SystemOutLog() );
		
		ArrayList<Item> items = new ArrayList<Item>();
		items.add( new Item( "userName", Operation.Add, "user0005@a1.local" ) );
		items.add( new Item( "active", Operation.Add, true ) );
		items.add( new Item( "displayName", Operation.Add, "Test User (0005)" ) );
		items.add( new Item( "name.formatted", Operation.Add, "Test User (0005)" ) );
		items.add( new Item( "name.givenName", Operation.Add, "Test" ) );
		items.add( new Item( "name.middleName", Operation.Add, "S" ) );
		items.add( new Item( "name.familyName", Operation.Add, "User (0005)" ) );
		items.add( new Item( "emails.work.value", Operation.Add, "user0005@a1.local" ) );
		items.add( new Item( "attributes.division", Operation.Add, "Division" ) );
		items.add( new Item( "attributes.userprincipalname", Operation.Add, "user0005@a1.local" ) );
		items.add( new Item( "attributes.organization", Operation.Add, "Organization" ) );
		items.add( new Item( "attributes.usertype", Operation.Add, "Affiliate" ) );
		items.add( new Item( "attributes.isManager", Operation.Add, false ) );
		items.add( new Item( "attributes.company", Operation.Add, "Company" ) );
		items.add( new Item( "attributes.distinguishedname", Operation.Add, "CN=Test User (0005),OU=A1TestUsers,DC=A1,DC=local" ) );
		items.add( new Item( "attributes.title", Operation.Add, "Title" ) );
		items.add( new Item( "attributes.department", Operation.Add, "Department" ) );
		items.add( new Item( "attributes.employeeid", Operation.Add, "0005" ) );
		items.add( new Item( "attributes.affiliateaccountname", Operation.Add, "user0005" ) );
		items.add( new Item( "attributes.mobilenumber", Operation.Add, "+17035572351" ) );
		
//		items.add( new Item( "name.honorificSuffix", Operation.Add, "" ) );
//		items.add( new Item( "attributes.jobtitle", Operation.Add, "Guy" ) );
//		items.add( new Item( "attributes.location", Operation.Add, "Austin" ) );
//		items.add( new Item( "attributes.region", Operation.Add, "Americas" ) );
		
		Result result = connector.create( "user0005@a1.local", items );
		
		Gson gson = new Gson();
		
		System.out.println( gson.toJson( result ) );	
	}
}
