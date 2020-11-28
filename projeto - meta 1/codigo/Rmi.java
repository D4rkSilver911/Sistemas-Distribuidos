package projeto.model;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public interface Rmi extends Remote {
	public int recebe_username_password(String username, String password) throws java.rmi.RemoteException;
	public boolean verifica_username_registo(String username) throws java.rmi.RemoteException;
	public int guarda_novo_utlizador(String nome, String username, String password) throws java.rmi.RemoteException;
	public boolean verifica_administrador(int id) throws java.rmi.RemoteException;
	public int pedir_id(String username) throws java.rmi.RemoteException;
	public String pedir_nome(int id) throws java.rmi.RemoteException;
	public String pedir_username(int id) throws java.rmi.RemoteException;
	public String pedir_password(int id) throws java.rmi.RemoteException;
	public String pedir_pesquisas(int id) throws java.rmi.RemoteException;
	public boolean verifica_username_altera(String username, int id) throws java.rmi.RemoteException;
	public void conceder_privilegios(String username) throws java.rmi.RemoteException;
	public boolean guarda_atualizacao_utlizador(int id, String nome, String username, String password) throws java.rmi.RemoteException;
	public String verifica_notificacoes(int id) throws java.rmi.RemoteException;
	public String verifica_notificacoes_constante(int id) throws java.rmi.RemoteException;
	public String pedir_nome_username_password_pesquisa(int id) throws java.rmi.RemoteException;
	public void anexa_url(String url) throws java.rmi.RemoteException;
	public String faz_pesquisa(int id, String pesquisa) throws java.rmi.RemoteException;
	public String faz_pesquisa_url(int id, String pesquisa) throws java.rmi.RemoteException;
	public String system_info() throws java.rmi.RemoteException;
}
