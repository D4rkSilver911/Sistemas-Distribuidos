import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;



// Step 1: Create Twitter Account

// Step 2: Create Application (https://dev.twitter.com/apps/new)

public class TwitterRestReader {

	// Access codes #1: per application used to get access codes #2	
	private static final String API_APP_KEY = "";
	private static final String API_APP_SECRET = "";
	
	// Access codes #2: per user per application
	private static final String API_USER_TOKEN = "";
	private static final String API_USER_SECRET = "";
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);

		OAuthService service = new ServiceBuilder()
		.provider(TwitterApi.class)
		.apiKey(API_APP_KEY)
		.apiSecret(API_APP_SECRET)
		.build();

		try {


			if ( API_USER_TOKEN.equals("") || API_USER_SECRET.equals("") ) {
				System.out.println("Fetching the Request Token...");
				Token requestToken = service.getRequestToken();
				System.out.println("Now go and authorize Scribe here:");
				System.out.println(service.getAuthorizationUrl(requestToken));
				System.out.println("And paste the verifier here");
				System.out.print(">>");
				Verifier verifier = new Verifier(in.nextLine());
				Token accessToken = service.getAccessToken(requestToken, verifier);
				System.out.println("Define API_USER_TOKEN: " + accessToken.getToken());
				System.out.println("Define API_USER_SECRET: " + accessToken.getSecret());
				System.exit(0);
			}

			Token accessToken = new Token( API_USER_TOKEN, API_USER_SECRET);

			OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/statuses/home_timeline.json", service);
			service.signRequest(accessToken, request);
			Response response = request.send();
			System.out.println("Got it! Lets see what we found...");
			System.out.println("HTTP RESPONSE: =============");
			System.out.println(response.getCode());
			System.out.println(response.getBody());
			System.out.println("END RESPONSE ===============");

			JSONArray arr= (JSONArray) JSONValue.parse(response.getBody());
			for(int i=0; i< arr.size(); i++) {
				JSONObject item = (JSONObject) arr.get(i);
				JSONObject user = (JSONObject) item.get("user");
				System.out.println(user.get("name") + " said: " + item.get("text"));
			}
			
		} catch(OAuthException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}



	}

}
