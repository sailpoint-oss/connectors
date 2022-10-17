package sailpoint.iiq.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import openconnector.Filter;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sailpoint.iiq.connector.IIQConnector;
import sailpoint.iiq.object.Resource;
import sailpoint.iiq.object.Resources;
import sailpoint.iiq.object.User;
import sailpoint.object.ResourceObject;
import sailpoint.tools.GeneralException;

public class ObjectTest {

	public ObjectTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, GeneralException {
	
/*		OkHttpClient client = new OkHttpClient();

		int startIndex = 1;
		int count = 5;
		
		Request request = new Request.Builder()
		  .url( "http://localhost:8080/identityiq/scim/v2/Users/?startIndex=" + startIndex + "&count=" + count )
		  .get()
		  .addHeader("authorization", "Basic c3BhZG1pbjphZG1pbg==")
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response response = client.newCall( request ).execute();
		
		Gson gson = new Gson();
		
		String jsonString = response.body().string();
		
		System.out.println( jsonString );
		
		Resources resources = gson.fromJson( jsonString, Resources.class );
		*/
		//System.out.println( gson.toJson( resources ) );
		
//		if ( resources.)
//		
//		for ( Resource resource : resources.getResources() ){
//			
//			User user = (User) resource;
//			
//			System.out.println( gson.toJson( user.getMap() ) );
//			
//			
//			
//			
//			//System.out.println( gson.toJson( resource ) );
//		}
		
		IIQConnector iiqConnector = new IIQConnector();
		
		Filter filter = null;
		Iterator<Map<String,Object>> it = iiqConnector.iterate( filter );
		
		while ( it.hasNext() ) {
			it.next();
			//System.out.println( it.next() );
		}
		
		

	}

}
