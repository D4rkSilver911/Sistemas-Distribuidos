/**
 * Raul Barbosa 2014-11-07
 */
package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;
import projeto.model.PesquisaBean;

public class PesquisaAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String website = null;
    private String username = null, password = null;
    private String username_registo = null, password_registo = null, nome_registo = null;
    private String username_privilegios = null;

    @Override

    public String execute() throws Exception {
        return SUCCESS;
    }
    //Login
    public String login() throws Exception {
        if(this.username != null && !username.equals("")) {
            System.out.println("Entrou aqui 1");
            PesquisaBean.setUsername(username);
            PesquisaBean.setPassword(password);
            int resposta = PesquisaBean.menu_login(username, password);
            System.out.println("Action Resposta = " + resposta);
            if(resposta == 1 || resposta == 2){
                return "fail";
            }

            if(resposta == 3){
                session.put("logged", true);
                return "admin";
            }
            if(resposta == 4){
                session.put("logged", true);
                return "user";
            }
            else{
                return "fail";
            }
        }
        else{
            return "fail";
        }

    }
    //Registo
    public String registo() throws Exception{
        if(this.username_registo != null && !username_registo.equals("") && this.nome_registo != null && !nome_registo.equals("")){
            int resposta = PesquisaBean.menu_registo(nome_registo, username_registo, password_registo);
            if(resposta == 1){
                return "username_invalido";
            }
            if(nome_registo.length() < 3){
                return "nome_invalido";
            }
            if(username_registo.length() < 4){
                return "username_curto";
            }

            else{
                return SUCCESS;
            }

        }
        else{
            return "fail";
        }

    }
    //Pesquisa
    public String pesquisar() throws Exception{
        return SUCCESS;
    }
    public String pesquisar_url() throws Exception{
        return SUCCESS;
    }
    public String ver_perfil() throws Exception{
        return SUCCESS;
    }
    public String logout(){
        PesquisaBean.setId(0);
        PesquisaBean.setNotificacao("");
        session.put("logged", false);
        return SUCCESS;
    }
    public String anexar(){
        PesquisaBean.anexar_url(website);
        return SUCCESS;
    }
    public String privilegios(){
        int resposta = PesquisaBean.menu_conceder_privilegios(username_privilegios);
        if(resposta == 1){
            return "fail";
        }
        return SUCCESS;
    }
    public PesquisaBean getPesquisaBean() {
        if(!session.containsKey("pesquisaBean"))
            this.setPesquisaBean(new PesquisaBean());
        return (PesquisaBean) session.get("pesquisaBean");
    }

    public void setPesquisaBean(PesquisaBean pesquisaBean) {
        this.session.put("pesquisaBean", pesquisaBean);
    }

    public void setWebsite(String url){
        website = url;
    }
    public void setUsername_privilegios(String user){
        username_privilegios = user;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setUsername_registo(String username){
        this.username_registo = username;
    }
    public void setPassword_registo(String password){
        this.password_registo = password;
    }
    public void setNome_registo(String nome){
        this.nome_registo = nome;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
    public Map<String, Object> getSession( ) {
        return session;
    }
}
