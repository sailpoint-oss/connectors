package sailpoint.iiq.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import sailpoint.iiq.object.Resource;
import sailpoint.iiq.object.Resources;
import sailpoint.iiq.object.User;

import com.google.gson.Gson;

public class ResourcesTest {

	public static void main(String[] args) throws IOException {
		
		String resourceString = FileUtils.readFileToString( new File ( "/Users/neil.mcglennon/Desktop/file.json" ) );
	
		//System.out.println( resourceString );
		
		Gson gson = new Gson();
		
		Resources resources = gson.fromJson( resourceString, Resources.class );
		
		for ( Resource resource : resources.getResources() ){
			
			User user = (User) resource;
			System.out.println( user );
			System.out.println( gson.toJson( user.getMap() ) );
		}
			
	}

}
