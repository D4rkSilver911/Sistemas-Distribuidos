import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RMIServer_Aux extends UnicastRemoteObject implements Rmi {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<User> lista_utilizadores = new ArrayList<>();

	public RMIServer_Aux() throws RemoteException {
		super();
	}

    public boolean verifica_username_registo(String username){
        boolean resp = true;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //envia TRUE-> username valido, FALSE-> usarname ja existe
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "1:"; //Funcao numero 1
            mensagem = mensagem.concat(username);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);
        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux = mensagem1.split(":");

            resp = Boolean.parseBoolean(aux[0]);

            System.out.println("1-> _Verifica username registo:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }

        return resp;
    }

    public boolean verifica_username_altera(String username, int id){
        boolean resp = true;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //envia TRUE-> username valido ou pertence a ele
            //envia FALSE-> usarname pertence a outro utilizador
            //envia TRUE-> username valido, FALSE-> usarname ja existe
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "2:"; //Funcao numero 2
            mensagem = mensagem.concat(username+":"+id);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);
        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux = mensagem1.split(":");

            resp = Boolean.parseBoolean(aux[0]);

            System.out.println("2-> Verifica username altera:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public String pedir_nome_username_password_pesquisa(int id){
        String resp = null;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o ID para saber o nome, username, password
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
         
            String mensagem = "14:"; //Funcao numero 14
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);
        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");
            resp = aux1[0];
            resp = resp.concat(":"+aux1[1]+":"+aux1[2]+":"+aux1[3]);

            
            System.out.println("14-> Pedir Nome-Username-Password-Pesquisa:  "+ resp);
        //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public int pedir_id(String username){
        int resp =0;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o username para saber o id
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "3:"; //Funcao numero 3
            mensagem = mensagem.concat(username);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);
        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux = mensagem1.split(":");

            resp = Integer.parseInt(aux[0]);
            System.out.println("3-> Pedir ID:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public String pedir_nome(int id){
        String resp = null;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o ID para saber o nome
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
         
            String mensagem = "4:"; //Funcao numero 4
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);
        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = aux1[0];
            System.out.println("4-> Pedir Nome:  "+ resp);
        //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public String pedir_username(int id){
        String resp = null;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o ID para saber o username
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "5:"; //Funcao numero 5
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);
        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = aux1[0];

            System.out.println("5-> Pedir Username:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public String pedir_password(int id){
        String resp = null;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o ID para saber a password
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
            
            String mensagem = "6:"; //Funcao numero 6
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = aux1[0];


            System.out.println("6-> Pedir Password:  "+resp );
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public String pedir_pesquisas(int id){
        String resp = null;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o ID para saber as pesquisas
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "7:"; //Funcao numero 7
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = aux1[0];

            System.out.println("7-> Pedir Pesquisas:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public boolean verifica_administrador(int id){
        boolean resp = true;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia id para saber administrador
            //TRUE -> administrador
            //False -> utilizador
            //Envia o ID para saber o nome
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "8:"; //Funcao numero 8
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = Boolean.parseBoolean(aux1[0]);

            System.out.println("8-> Verifica Administrador:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public int guarda_novo_utlizador(String nome, String username, String password){
        int resp = 0;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o nome, username, password
            //vai guardar essa informacao no ficheiro texto
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "9:"; //Funcao numero 9
            mensagem = mensagem.concat(nome+":"+username+":"+password);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);

            String[] aux = mensagem1.split(":");
            resp = Integer.parseInt(aux[0]);

            System.out.println("Novo Utilizador:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public boolean guarda_atualizacao_utlizador(int id, String nome, String username, String password){
        boolean resp = true;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o nome, username, password
            //vai guardar essa informacao no ficheiro texto
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "12:"; //Funcao numero 9
            mensagem = mensagem.concat(id+":"+nome+":"+username+":"+password);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);

            String[] aux = mensagem1.split(":");
            resp = Boolean.parseBoolean(aux[0]);

            System.out.println("Nova Atualizacao:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public int recebe_username_password(String username, String password){
        int resp = 0;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";

            //Verificar Username e password
            //Envia o username e password
            //1 -> username nao existe
            //2 -> password incorreta
            //3 -> entrou um administrador
            //4 -> entrou um utilizador

            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "10:"; //Funcao numero 10
            mensagem = mensagem.concat(username+":"+password);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

            

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            
            buffer = pack_recebido.getData();
            
            String mensagem1 = new String(buffer);
            String[] aux = mensagem1.split(":");
            resp = Integer.parseInt(aux[0]);
            //System.out.println("10 -> Recebe username password:  "+ mensagem1+"||");
            //resp = Integer.parseInt(mensagem1);
            System.out.println("10 -> Recebe username password:  "+ resp);
            
            
            //-----------------------------------
            
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public void conceder_privilegios(String username){

        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            //Envia o username  

            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "11:"; //Funcao numero 10
            mensagem = mensagem.concat(username);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
    }

    public String verifica_notificacoes(int id){
        String resp = null;
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o ID para saber as pesquisas
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "13:"; //Funcao numero 13
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = aux1[0];

            System.out.println("13-> Notificacoes:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    public String verifica_notificacoes_constante(int id){
        String resp = null;
        try{
            int port = 4322;
            String group_envio_notifica = "224.0.230.2";
            String group_recebe_notifica = "224.0.230.3";
            //Envia o ID para saber as pesquisas
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "13:"; //Funcao numero 13
            String aux=String.valueOf(id);
            mensagem = mensagem.concat(aux);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio_notifica), port);
            socket.send(pack);

        

            //============================================================================================
            //============================================================================================
            //RECEBER INFORMAÇÃO DO SERVER
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe_notifica));
            byte[] buffer = new byte[256];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            buffer = pack_recebido.getData();
            String mensagem1 = new String(buffer);
            String[] aux1 = mensagem1.split(":");

            resp = aux1[0];

            System.out.println("13-> Notificacoes:  "+ resp);
            //-----------------------------------
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }

    //LER O URL QUE O ADMIN QUER INDEXAR
    public void anexa_url(String url){
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            //Envia o username  

            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO
        
            String mensagem = "15:"; //Funcao numero 10
            mensagem = mensagem.concat(url);
            mensagem = mensagem.concat(":");
                
            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
    }
    public String system_info(){
        String finall = "";
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";

            System.out.println("Entrou aqui!");
            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO

            String mensagem = "17:"; //Funcao numero 17 (Enviar apenas o pedido de info de sistema)

            byte buf[] = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

            //recebe a info do server
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[10000];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            byte[] buffer_final = new byte[pack_recebido.getLength()];
            System.arraycopy(pack_recebido.getData(), pack_recebido.getOffset(), buffer_final, 0, pack_recebido.getLength());
            String mensagem1 = new String(buffer_final);
            String[] resp = mensagem1.split("^");
            finall = resp[0];



        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return finall;
    }

    public String faz_pesquisa(int id, String pesquisa){

        String resp = "";
        try{
            int port = 4321;
            String group_envio = "224.0.230.0";
            String group_recebe = "224.0.230.1";
            //Envia o username

            MulticastSocket socket = new MulticastSocket();
            //INTRODUZIR E ENVIAR INFORMAÇÃO

            String mensagem = "16:"; //Funcao numero 10

            mensagem = mensagem.concat(String.valueOf(id));
            mensagem = mensagem.concat(":");
            mensagem = mensagem.concat(pesquisa);
            mensagem = mensagem.concat(":");


            //ENVIA A INFO
            byte[] buf = mensagem.getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group_envio), port);
            socket.send(pack);

            //recebe a info do server
            MulticastSocket socket_receber = null;
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
            byte[] buffer = new byte[100000];
            DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
            socket_receber.receive(pack_recebido);
            byte[] buffer_final = new byte[pack_recebido.getLength()];
            System.arraycopy(pack_recebido.getData(), pack_recebido.getOffset(), buffer_final, 0, pack_recebido.getLength());
            String mensagem1 = new String(buffer_final);
            String[] termos = mensagem1.split(", ");
            for(String termo : termos){
                resp = resp.concat(termo);
                resp = resp.concat("\n");
            }
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }
        return resp;
    }
	// =========================================================
	public static void main(String args[]) {
        int port = 4321;
        String group_envio = "224.3.2.1";
        String group_recebe = "224.3.2.2";
        String user = null;
        String pass = null;
        String user_pass = null;
        int ttl = 1;
        MulticastSocket socket_receber = null; //Socket para receber
        

        //=======================================================================================
        //============= Comunicar com o RMI Client
        System.getProperties().put("java.security.policy", "RMIServer.policy");
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

		try {
			RMIServer_Aux h = new RMIServer_Aux();
			Registry r = LocateRegistry.createRegistry(6000);
			r.rebind("projeto", h);
            //lista_utilizadores = le_ficheiros();
			System.out.println("RMI Server ready!");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		}
        //=======================================================================================
        try{
            Scanner keyboardScanner = new Scanner(System.in);
            
            MulticastSocket socket = new MulticastSocket(); //Enviar
            socket_receber = new MulticastSocket(port);
            socket_receber.joinGroup(InetAddress.getByName(group_recebe));
           
            while(true){
                /*
                //RECEBER INFORMAÇÃO DO SERVER
                byte[] buffer = new byte[256];
                DatagramPacket pack_recebido = new DatagramPacket(buffer, buffer.length);
                socket_receber.receive(pack_recebido);

                System.out.println("Received packet from " + pack_recebido.getAddress().getHostAddress() + ":" + pack_recebido.getPort() + " with message:");
                String message = new String(pack_recebido.getData(), 0, pack_recebido.getLength());
                System.out.println(message);
                //-----------------------------------
                */
            }

            
        }catch(IOException e){
            System.out.println("Exception in RMIServer.main: " + e);
        }


	}

}