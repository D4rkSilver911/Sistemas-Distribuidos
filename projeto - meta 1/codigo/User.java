import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

//Classe User


public class User implements Serializable {
    private String nome, username, password;
    private int id;
    private boolean administrador;
    private ArrayList<String> pesquisas_anteriores ;
    private String notificacoes;
        
    //Criar um utilizador

    User(String aux_nome, String aux_username, String aux_password, int aux_id, boolean aux_admin, ArrayList<String> aux_pesquisa, String aux_notificacoes ){

        this.nome = aux_nome;
        this.username = aux_username;
        this.password = aux_password;
        this.id = aux_id;
        this.administrador = aux_admin;
        this.pesquisas_anteriores = aux_pesquisa;
        this.notificacoes = aux_notificacoes;

    }
    User(){}
    protected void set_notificacoes(String aux){
        this.notificacoes = aux;
    }

    protected void set_nome(String aux){
        this.nome = aux;
    }

    protected void set_username(String aux){
        this.username = aux;
    }

    protected void set_password(String aux){
        this.password = aux;
    }

    protected void set_id(int aux){
        this.id = aux;
    }

    protected void set_administrador(boolean aux){
        this.administrador = aux;
    }   

    protected void set_pesquisa(ArrayList<String> aux){
        this.pesquisas_anteriores = aux;
    }


    //===========================================
    protected String get_notificacoes(){
        return this.notificacoes;
    }

    protected String get_nome(){
        return this.nome;
    }

    protected String get_username(){
        return this.username;
    }

    protected String get_password(){
        return this.password;
    }

    protected int get_id(){
        return this.id;
    }

    protected boolean get_administrador(){
        return this.administrador;
    }

    protected ArrayList<String> get_pesquisas(){
        return this.pesquisas_anteriores;
    }


}




