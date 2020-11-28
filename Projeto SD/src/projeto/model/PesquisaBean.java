/**
 * Raul Barbosa 2014-11-07
 */
package projeto.model;


import com.github.scribejava.core.model.Token;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.io.IOException;
import java.io.*;
import java.io.BufferedReader;
import java.util.Scanner;
public class PesquisaBean {
	static Rmi h;
	static String termo;
	static int id;
	static String website;
	private static  String IP_GABRIEL = "localhost";
	public static String username;
	public static String password;
	public static String notificacao;
	public static String url;
	public static Token accessToken;
	public PesquisaBean(){
		try {

			 h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, 7000).lookup("projeto");
		} catch (Exception e) {
			try{
				h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, 7000).lookup("projeto");
			} catch (Exception es) {
				System.out.println("Exception in main: " + es);
				e.printStackTrace();
			}
		}
	}
	public String getTermo() {
		return termo;
	}
	public String getNotificacao(){
		return notificacao;
	}
	public static void setId(int id1){
		id = id1;
	}
	public static void setWebsite(String url){
		website = url;
	}
	public static void setUsername(String username1){
		username = username1;
	}
	public static void setPassword(String password1){
		password = password1;
	}
	public void setTermo(String termo) {
		PesquisaBean.termo = termo;
	}
	public static void anexar_url(String website){
		try {
			h.anexa_url(website);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public static String menu_pesquisa(){
		//-------------------------------------------------------------------------------------------------
		//------------------------------------------- MENU PESQUISA ---------------------------------------
		//-------------------------------------------------------------------------------------------------
		String resp = "Nenhum!";
		try {
			resp =  h.faz_pesquisa(id, termo);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	public static void setNotificacao(String notificacao1){
		notificacao = notificacao1;
	}
	public static String system_info(){
		String resp = "";

		try {
			resp = h.system_info();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return resp;
	}
	public static int menu_login(String username, String password){
		//------- LOGIN ---------
		//1 - User mal
		//2 - Pass mal
		//3 - Admin
		//4 - User
		//5- Erro
		int resp = 0;
		try {
			resp =  h.recebe_username_password(username, password);
			id = h.pedir_id(username);
			System.out.println(id);
		}catch (Exception e) {
			e.printStackTrace();
		}
		String message = null;
			try {
				message = h.verifica_notificacoes(id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if(message.compareTo(" ") != 0){
				notificacao = message;
			}


		//System.out.println(id);


		return resp;

	}
	public static int menu_registo(String nome, String username, String password){
		//------- Registo ---------

		//1 - User não válido
		Boolean user_valid = true;
		try {
			user_valid = h.verifica_username_registo(username);//para saber se username E valido

		}catch (Exception e) {
				System.out.println("Exception in main: " + e);
				e.printStackTrace();
		}

		if(!user_valid){//username nao E valido
			return 1;
		}


		try {
			h.guarda_novo_utlizador(nome, username, password);
		}catch (Exception e) {
				System.out.println("Exception in main: " + e);
				e.printStackTrace();
			}


		return 2;



	}
	public static String menu_perfil_utilizador(){
		//-------------------------------------------------------------------------------------------------
		//------------------------------------------- MENU USER -------------------------------------------
		//-------------------------------------------------------------------------------------------------
		String frase = null;
		String[] aux = null;
		try {
			frase = h.pedir_nome_username_password_pesquisa(id);
			//aux = frase.split("|XXX|");
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		/*
		nome = aux[0];
		username = aux[1];
		password = aux[2];
		pesquisa = aux[3];*/
		return frase;
	}
	public static int menu_conceder_privilegios(String username) {


		Boolean comp = true;
		int aux = 0;
		try {
			comp = h.verifica_username_registo(username);//para saber se username E valido
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}

		if (comp) {//username nao existe
			aux = 1;
			return aux;
		}
		//======================================================
		//==================== Concede privilegios

		try {
			h.conceder_privilegios(username);//para saber se username E valido
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		return aux;
	}
	public static String menu_pesquisa_url(){
		String resp = "";
		try {
			resp =  h.faz_pesquisa_url(id, termo);
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		return resp;
	}
	public static void setUrl(String url1){
		url = url1;
	}
	public String getUrl(){
		return url;
	}

	public void setAccessToken(Token token1){
		accessToken = token1;
	}

	public Token getAccessToken(){
		return accessToken;
	}


}












