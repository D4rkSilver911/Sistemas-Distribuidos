package projeto.action;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.opensymphony.xwork2.ActionContext;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import projeto.model.PesquisaBean;
import uc.sd.apis.FacebookApi2;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import java.rmi.RemoteException;

import java.util.Scanner;


public class FacebookAction extends ActionSupport implements SessionAware{
    private static final String NETWORK_NAME = "Facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private static final String API_KEY = "578436576222141";
    private static final String API_SECRET = "08a6c41da6bde59d98fdcf9d1b35bb94";
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    private String username;
    private int n = 0;
    private Token accessToken;
    private String url;

    public String execute() throws RemoteException{
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi2.class)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback("http://localhost:8080/Projeto_SD_war_exploded/facebook_action") // Do not change this.
                .scope("public_profile")
                .build();

        HttpServletRequest servletRequest = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
        String code = servletRequest.getParameter("code");
        System.out.println(code);
        Verifier verifier = new Verifier(code);
        accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);

        service.signRequest(accessToken, request);
        Response response = request.send();

        this.setUsername(response.getBody());

        int resposta = PesquisaBean.menu_registo(username, username, "password");
        if(resposta == 1){
            return "username_invalido";
        }
        else{
            return SUCCESS;
        }

    }



    public void setPesquisaBean(PesquisaBean pesquisaBean) {
        this.session.put("pesquisaBean", pesquisaBean);
    }
    public void setUsername(String username){
        JSONParser parser = new JSONParser();

        try {
            JSONObject json = (JSONObject) parser.parse(username);
            this.username = (String) json.get("name");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
    public PesquisaBean getPesquisaBean() {
        if(!session.containsKey("pesquisaBean"))
            this.setPesquisaBean(new PesquisaBean());
        return (PesquisaBean) session.get("pesquisaBean");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
