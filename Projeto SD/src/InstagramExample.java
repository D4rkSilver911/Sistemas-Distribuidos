import java.util.Scanner;

import com.github.scribejava.apis.InstagramApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

// Step 1: Create Instagram account

// Step 2: Create Application (https://www.instagram.com/developer)

public class InstagramExample {

	// Access codes #1: per application used to get access codes #2	
	private static final String API_APP_KEY = "";
	private static final String API_APP_SECRET = "";
	
	// Access codes #2: per user per application
	private static final String API_USER_TOKEN = "";
	
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);

		OAuthService service = new ServiceBuilder()
		.provider(InstagramApi.class)
		.apiKey(API_APP_KEY)
		.apiSecret(API_APP_SECRET)
		.scope("basic")
		.grantType("authorization_code")
		.callback("http://eden.dei.uc.pt/~fmduarte/echo.php")
		.build();

		try {


			if ( API_USER_TOKEN.equals("")) {
				System.out.println("Fetching the Request Token...");
				System.out.println("Now go and authorize Scribe here:");
				System.out.println(service.getAuthorizationUrl(Token.empty()));
				System.out.println("And paste the verifier here");
				System.out.print(">>");
				Verifier verifier = new Verifier(in.nextLine());
				
				Token accessToken = service.getAccessToken(null, verifier);
				System.out.println("define API_USER_TOKEN: " + accessToken.getToken());
				System.exit(0);
			}

			Token accessToken = new Token( API_USER_TOKEN, "ignore");

			OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.instagram.com/v1/users/self/", service);
			service.signRequest(accessToken, request);
			Response response = request.send();
			System.out.println("Got it! Lets see what we found...");
			System.out.println("HTTP RESPONSE: =============");
			System.out.println(response.getCode());
			System.out.println(response.getBody());
			System.out.println("END RESPONSE ===============");

		} catch(OAuthException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}



	}

}
