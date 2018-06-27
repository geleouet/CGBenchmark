package fr.svivien.cgbenchmark;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.svivien.cgbenchmark.api.LoginApi;
import fr.svivien.cgbenchmark.api.SessionApi;
import fr.svivien.cgbenchmark.api.SessionChallengeApi;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.request.login.LoginRequest;
import fr.svivien.cgbenchmark.model.request.login.LoginResponse;
import fr.svivien.cgbenchmark.model.request.session.SessionChallengeRequest;
import fr.svivien.cgbenchmark.model.request.session.SessionRequest;
import fr.svivien.cgbenchmark.model.request.session.SessionResponse;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class SessionLogIn {
	
	private static final Log LOG = LogFactory.getLog(SessionLogIn.class);

	public void retrieveAccountCookieAndSession(AccountConfiguration accountCfg, String multiName) {
		if (accountCfg.getAccountIde() != null) {
			// already logged in
			return; 
		}
		
	    LOG.info("Retrieving cookie and session for account " + accountCfg.getAccountName());
	
//	    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(600, TimeUnit.SECONDS).build();
		
	    OkHttpClient client = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).build();
	    Retrofit retrofit = new retrofit2.Retrofit.Builder().client(client).baseUrl(Constants.CG_HOST).addConverterFactory(GsonConverterFactory.create()).build();
	    LoginApi loginApi = retrofit.create(LoginApi.class);
	
	    LoginRequest loginRequest = new LoginRequest(accountCfg.getAccountLogin(), accountCfg.getAccountPassword());
	    Call<LoginResponse> loginCall = loginApi.login(loginRequest);
	
	    // Calling getSessionHandle API
	    retrofit2.Response<LoginResponse> loginResponse;
	    try {
	        loginResponse = loginCall.execute();
	    } catch (IOException | RuntimeException e) {
	        throw new IllegalStateException("Login request failed");
	    }
	
	    if (loginResponse.body().success == null || loginResponse.body().success.userId == null) {
	        throw new IllegalStateException("Login failed, please check login/pwd in configuration");
	    }
	
	    // Selecting appropriate cookie; we keep the one that expires the later
	    Optional<Cookie> optCookie = loginResponse.headers().values(Constants.SET_COOKIE).stream()
	            .map(c -> Cookie.parse(HttpUrl.parse(Constants.CG_HOST), c))
	            .filter(c -> c.name().equals(Constants.REMCG) && c.expiresAt() > new Date().getTime())
	            .sorted((a, b) -> (int) (b.expiresAt() - a.expiresAt()))
	            .findFirst();
	
	    if (!optCookie.isPresent()) {
	        throw new IllegalStateException("Cannot find required cookie in getSessionHandle response");
	    }
	
	    // Setting the cookie in the account configuration
	    accountCfg.setAccountCookie(optCookie.get().toString());
	
	    SessionApi sessionApi = retrofit.create(SessionApi.class);
	    SessionRequest sessionRequest = new SessionRequest(loginResponse.body().success.userId, multiName);
	    boolean isContest = false;
	    Call<SessionResponse> sessionCall = sessionApi.getSessionHandle(isContest ? Constants.CONTEST_SESSION_SERVICE_URL : Constants.PUZZLE_SESSION_SERVICE_URL, sessionRequest, Constants.CG_HOST, accountCfg.getAccountCookie());

//	    Call<SessionResponse> sessionCall = sessionApi.getSessionHandle(sessionRequest, Constants.CG_HOST + "/puzzle/" + multiName, accountCfg.getAccountCookie());
	    retrofit2.Response<SessionResponse> sessionResponse;
	    try {
	        sessionResponse = sessionCall.execute();
	    } catch (IOException | RuntimeException e) {
	        throw new IllegalStateException("Session request failed");
	    }
	
	    if (sessionResponse.body().success != null) {
	    	// Setting the IDE session in the account configuration
	    	accountCfg.setAccountIde(sessionResponse.body().success.handle);
	    	accountCfg.setUserId(loginResponse.body().success.userId);
	    }
	    else {
	    	SessionChallengeRequest sessionChallengeRequest = new SessionChallengeRequest(loginResponse.body().success.userId, multiName);
	    	SessionChallengeApi sessionChallengeApi = retrofit.create(SessionChallengeApi.class);
	    	 Optional<Cookie> sessionCookie = loginResponse.headers().values(Constants.SET_COOKIE).stream()
	 	            .map(c -> Cookie.parse(HttpUrl.parse(Constants.CG_HOST), c))
	 	            .filter(c -> c.name().equals(Constants.JSESSIONID) && c.expiresAt() > new Date().getTime())
	 	            .sorted((a, b) -> (int) (b.expiresAt() - a.expiresAt()))
	 	            .findFirst();
	    	
	    	sessionCall = sessionChallengeApi.getSessionHandle(sessionChallengeRequest, Constants.CG_HOST + "/ide/challenge/" + multiName, accountCfg.getAccountCookie()+";"+sessionCookie.get().toString());
	    	try {
		        sessionResponse = sessionCall.execute();
		    } catch (IOException | RuntimeException e) {
		        throw new IllegalStateException("Session request failed");
		    }
	    	
	    	// Setting the IDE session in the account configuration
	    	accountCfg.setAccountIde(sessionResponse.body().success.testSessionHandle);
	    	accountCfg.setUserId(loginResponse.body().success.userId);
	    }
	}

}
