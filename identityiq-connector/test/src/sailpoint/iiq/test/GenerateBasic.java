package sailpoint.iiq.test;

import sailpoint.tools.Base64;

public class GenerateBasic {

	public GenerateBasic() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		System.out.println( getAuthorization ( "GDLP6AGYzeVnQioR", "JeOowQ0y7isBjxWaUTcXb5PhqTkEzyLU") );

	}

	
	public static String getAuthorization ( String user, String password ) {
		
		String authString = user + ":" + password;

		return "Basic " + Base64.encodeBytes( authString.getBytes() );

	}
}
