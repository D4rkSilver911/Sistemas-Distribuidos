import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.nio.channels.FileLockInterruptionException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;


import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.*;
/** Servidor Multicast principal
*/

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS_RECEBE = "224.0.230.0";
     private String MULTICAST_ADDRESS_ENVIO = "224.0.230.1";
    private String MULTICAST_ADDRESS_RECEBE_NOTIFICA = "224.0.230.2";
    private String MULTICAST_ADDRESS_ENVIO_NOTIFICA = "224.0.230.3";
    private int DEPTH =5;

    private int PORT = 4321;
    private long SLEEP_TIME = 5000;

    private ArrayList<User> lista_utilizadores = new ArrayList<>();
    private HashMap<String, HashSet<String>> index = new HashMap<>(); /**Index invertido*/
    private ArrayList<String> urls = new ArrayList<>(); /**URLS*/
    private HashMap<String, String> titles = new HashMap<>(); /**Array de urls e os seus titles*/
    private HashMap<String, String> descricao = new HashMap<>(); /**Array de urls e as suas descrições*/
    private HashMap<String, HashSet<String>> url_in_url = new HashMap<>(); /**Array de urls que vao dar a outros urls*/
    Map<String, Integer> UrlCountMap = new TreeMap<>(); /**Popularidade de cada URL*/
    Map<String, Integer> WordCountMap = new TreeMap<>(); /**Popularidade de cada termo de pesquisa*/


    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void run() {
        le_ficheiros();/**le o ficheiro users e guarda tudo no atributo lista de utilizadores */
        new Conecao1_TCP(lista_utilizadores);/** Cria a thread que comunica com o Multicast secundario */
        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running...");
        System.out.println("Anexando URLS... ");
        ler_urls();/**Anexa todos os URLs */
        System.out.println("Completo!");
        //print_array();


        try {
            //-------------------------------------------- Inicar Sockets ---------------------------------------------
        	//** Inicia a ligacao com RMI Server */
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_RECEBE);
            socket = new MulticastSocket(PORT);  /** Socket para receber*/
            MulticastSocket socket_envia = new MulticastSocket(); /**Socket para enviar*/
            socket.joinGroup(group);
            socket.setTimeToLive(1);

           new Thread_Multicast(lista_utilizadores);/**Cria uma thread para tratar das notificacoes */


            while (true) {/** esta sempre a correr*/
                //=====================================================================================================
                /**RECEBE INFORMAÇÃO DO RMISERVER*/
                byte buf[] = new byte[1024];
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);
                buf = pack.getData();

                String mensagem = new String(buf);
                System.out.println("Received packet from " + pack.getAddress().getHostAddress() + ":" + pack.getPort() + " with message:");
                String message = new String(pack.getData(), 0, pack.getLength());
                System.out.println(message);
                /**A mensagem vem na forma de "funcao:informacao"
                *aux[0] = informacao ->  que trata se de um inteiro
                *a informacao fica separada com a ajuda do split
                */
                String[] aux10 = mensagem.split("---");
                String[] aux = mensagem.split(":");
                int funcao = Integer.parseInt(aux[0]);

                //=======================================
                /**Separar com um switch*/
                /**Vai receber uma mensagem do RMI Server com um numero*/
                /**Cada numero corresponde a uma funcao diferente*/
                /**Imprime a informação que recebeu*/


                switch(funcao){
                    case 1:{
                        //verifica username registo
                        //recebe "1:username"
                        //envia um boolean
                        String username = aux[1];
                        boolean resp = verifica_username_registo(username);
                        //envia resposta
                        System.out.println("1-> Verifica username registo: "+username+" -> "+resp);

                        String confirma = String.valueOf(resp);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 2:{
                        //verifica username altera
                        //recebe "2:username:id"
                        //envia um boolean
                        String username = aux[1];
                        int id = Integer.parseInt(aux[2]);
                        boolean resp = verifica_username_altera(username, id);

                        //envia resposta
                        System.out.println("2-> Verifica username altera: "+username+" -> "+resp);

                        String confirma = String.valueOf(resp);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 3:{
                        //pedir id
                        //recebe "3:username"
                        String username = aux[1];
                        int id = pedir_id(username);

                        //envia resposta
                        System.out.println("3-> Pedir ID: "+username+" -> "+id);
                        String confirma = Integer.toString(id);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 4:{
                        //pedir nome
                        //recebe "4:id"

                        int id = Integer.parseInt(aux[1]);
                        String confirma = pedir_nome(id);

                        //envia resposta
                        System.out.println("4-> Pedir nome: "+confirma+" -> "+id);

                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 5:{
                        //pedir username
                        //recebe "5:id"

                        int id = Integer.parseInt(aux[1]);
                        String confirma = pedir_username(id);

                        //envia resposta
                        System.out.println("5-> Pedir username: "+confirma+" -> "+id);

                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);

                        break;
                    }
                    case 6:{
                        //pedir password
                        //recebe "6:id"

                        int id = Integer.parseInt(aux[1]);
                        String confirma = pedir_password(id);

                        //envia resposta
                        System.out.println("6-> Pedir password: "+confirma+" -> "+id);

                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 7:{
                        //pedir pesquisas
                        //recebe "7:id"

                        int id = Integer.parseInt(aux[1]);
                        ArrayList<String> pesquisas = pedir_pesquisas(id);
                        String confirma = "";
                        for(int i = 0; i < pesquisas.size(); i++){
                            confirma = confirma.concat(pesquisas.get(i));
                            confirma = confirma.concat(" - ");
                        }

                        //envia resposta
                        System.out.println("7-> Pedir pesquisas: "+confirma+" -> "+id);

                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 8:{
                        //verifica administrador
                        //recebe "8:id"
                        //envia True -> Administrador
                        //      False -> Utilizador
                        int id = Integer.parseInt(aux[1]);
                        boolean resp = verifica_administrador(id);

                        //envia resposta
                        System.out.println("8-> Verifica administrador: "+resp+" -> "+id);
                        String confirma = String.valueOf(resp);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 9:{
                        //guarda novo utilizador
                        //recebe "9:nome:username:password"
                        //enbia o id

                        String nome = aux[1];
                        String username = aux[2];
                        String password = aux[3];
                        int id = lista_utilizadores.size() +1;

                        ArrayList<String> aux2 = new ArrayList<>();
                        aux2.add(" ");

                        //envia resposta
                        System.out.println("9-> Novo utilizador: "+nome+" - "+username+" - "+password);
                        User novo = new User(nome, username, password, id, false, aux2 , " ");
                        lista_utilizadores.add(novo);
                        atualiza_ficheiro_user();

                        String confirma = Integer.toString(id);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);

                        break;
                    }
                    case 10:{
                        //recebe username password
                        //recebe "10:username:password"
                        //envia 1 -> username nao existe
                        //      2 -> password incorreta
                        //      3 -> entrou Administrador
                        //      4 -> entrou Utilizador

                        String username = aux[1];
                        String password = aux[2];
                        int resp = recebe_username_password(username, password);


                        //envia resposta
                        System.out.println("10-> Tipo utilizador: "+resp+"->"+username+"| |"+password+"|");
                        String confirma = Integer.toString(resp);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        System.out.println("Enviou");
                        break;
                    }
                    case 11:{
                        String username = aux[1];

                        menu_conceder_privilegios(username);

                        break;
                    }
                    case 12:{
                        //guarda atualizacao utilizador
                        //recebe "12:id:nome:username:password"
                        //enbia o id

                        int id = Integer.parseInt(aux[1]);
                        String nome = aux[2];
                        String username = aux[3];
                        String password = aux[4];

                        //envia resposta
                        System.out.println("12-> Nova Atualizacao: "+id+"-"+nome+" - "+username+" - "+password);

                        //atualiza lista
                        lista_utilizadores.get(id-1).set_nome(nome);
                        lista_utilizadores.get(id-1).set_username(username);
                        lista_utilizadores.get(id-1).set_password(password);
                        atualiza_ficheiro_user();

                        boolean admin = lista_utilizadores.get(id-1).get_administrador();
                        String confirma = String.valueOf(admin);
                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);

                        break;
                    }
                    case 13:{
                        //verifica notificacoes
                        int id = Integer.parseInt(aux[1]);
                        if(id > lista_utilizadores.size()){
                            String confirma1 = "";
                            byte buf_envia[] = confirma1.getBytes();
                            DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                                InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                            socket_envia.send(pack_envio);
                            break;
                        }
                     
                        String confirma = lista_utilizadores.get(id-1).get_notificacoes();
                            lista_utilizadores.get(id-1).set_notificacoes(" ");
                            atualiza_ficheiro_user();
                             //envia resposta
                            System.out.println("13-> Verifica notificacoes: "+confirma+" -> "+id);

                            confirma = confirma.concat(":");
                            byte buf_envia[] = confirma.getBytes();
                            DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                                InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                            socket_envia.send(pack_envio);

                        break;

                        
                       

                       
                    }
                    case 14:{
                        //pedir nome, username, password
                        //recebe "14:id"

                        int id = Integer.parseInt(aux[1]);
                        String nome = pedir_nome(id);
                        String username = pedir_username(id);
                        String password = pedir_password(id);
                        ArrayList<String> pesquisas = pedir_pesquisas(id);
                        String confirma = "";

                        confirma = confirma.concat(nome+";;"+username+";;"+password+";;"+pesquisas);
                        //envia resposta
                        System.out.println("14-> Pedir Nome-Username-Password-Pesquisa: "+confirma+" -> "+id);

                        confirma = confirma.concat(":");
                        byte buf_envia[] = confirma.getBytes();
                        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                            InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                        socket_envia.send(pack_envio);
                        break;
                    }
                    case 15:{
                        String url = aux[1];
                        anexar_url(url,0);
                        url_to_file(url);
                        System.out.println("Done!");
                        break;
                    }
                    case 16:{
                        String pesquisa = aux[2];
                        String id1 = aux[1];
                        int id = Integer.parseInt(id1);
                        add_pesquisa(id, pesquisa);
                        faz_pesquisa(pesquisa, socket_envia);
                        add_pop_to_termo_pesquisa(pesquisa);

                        break;


                    }
                    case 17:{

                        send_system_info(socket_envia);
                        break;
                    }
                    case 18:
                    {
                      String pesquisa = aux10[2];
                      String id1 = aux10[1];
                      int id = Integer.parseInt(id1);
                      add_pesquisa(id, pesquisa);
                      faz_pesquisa_url(pesquisa, socket_envia);
                      break;

                    }
                }


                /*
                String confirma = "Informacao recebida!";
                byte buf_envia[] = confirma.getBytes();
                DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
                    InetAddress.getByName(MULTICAST_ADDRESS_ENVIO),PORT);

                socket_envia.send(pack_envio);
                */






                try { sleep((long) (Math.random() * SLEEP_TIME)); } catch (InterruptedException e) { }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public void add_pop_to_termo_pesquisa(String pesquisa){
        String[] termos = pesquisa.split(" ");
        for(String termo : termos){
            termo = termo.toLowerCase();

            if("".equals(termo)){
                continue;
            }
            if(!WordCountMap.containsKey(termo)){
                WordCountMap.put(termo,1);
            }
            else{
                WordCountMap.put(termo, WordCountMap.get(termo)+1);
            }
        }
        for (String name: WordCountMap.keySet()){
            String key = name.toString();
            String value = WordCountMap.get(name).toString();
            System.out.println(key + " " + value);
        }
    }

    public void send_system_info(MulticastSocket socket) {


        String resp = " - Top 10 termos mais pesquisados -  \n\n"+ "|XXX|"+"\n";
        int tamanho_word = WordCountMap.size();
        int tamanho_urls = UrlCountMap.size();
        int maior;
        String word_maior;
        String url_maior;

        if (WordCountMap.isEmpty()) {
            resp = resp.concat("\n\t - Nenhuma pesquisa ainda foi feita -\n\n"+ "|XXX|"+"\n");
        } else {
            int i = 0;

            if(tamanho_word < 10){
                while (i < tamanho_word) {
                    maior = 0;
                    word_maior = "";
                    for (Map.Entry<String, Integer> entry : WordCountMap.entrySet()) {

                        if (entry.getValue() >= maior && !resp.contains(entry.getKey())) {
                            maior = entry.getValue();
                            word_maior = entry.getKey();

                        }
                    }
                    resp = resp.concat(" \t - " + word_maior + " - foi pesquisada " + maior +  " vez(es)!" + "\n"+ "|XXX|"+"\n");
                    i++;
                }

            }
            else{

                while (i < 10) {
                    maior = 0;
                    word_maior = "";
                    for (Map.Entry<String, Integer> entry : WordCountMap.entrySet()) {

                        if (entry.getValue() >= maior && !resp.contains(entry.getKey()))  {
                            maior = entry.getValue();
                            word_maior = entry.getKey();

                        }
                    }
                    resp = resp.concat("\t - " + word_maior + " - foi pesquisada " + maior +  " vez(es)!"+"\n"+ "|XXX|"+"\n");
                    i++;
                }
            }
        }
        resp = resp.concat("\n - Top 10 links mais populares - \n\n"+ "|XXX|"+"\n");
        if (UrlCountMap.isEmpty()) {
            resp = resp.concat("\n\t - Nenhum link ainda é popular -\n"+ "|XXX|"+"\n");
        } else {
            int i = 0;

            if(tamanho_urls < 10){
                while (i < tamanho_urls) {
                    maior = 0;
                    url_maior = "";
                    for (Map.Entry<String, Integer> entry : UrlCountMap.entrySet()) {
                        if(entry.getValue() != null) {
                            if (entry.getValue() >= maior && !resp.contains(entry.getKey())) {
                                maior = entry.getValue();
                                url_maior = entry.getKey();

                            }

                        }

                    }
                    resp = resp.concat("\t - " + url_maior + " - tem " + maior +  " acesso(s)!" + "\n"+ "|XXX|"+"\n");
                    i++;
                }

            }
            else{

                while (i < 10) {
                    maior = 0;
                    url_maior = "";
                    for (Map.Entry<String, Integer> entry : UrlCountMap.entrySet()) {
                        if(entry.getValue() != null) {
                            if (entry.getValue() >= maior && !resp.contains(entry.getKey())) {
                                maior = entry.getValue();
                                url_maior = entry.getKey();

                            }

                        }
                    }
                    resp = resp.concat("\t - " + url_maior + " tem " + maior +  " acesso(s)!" + "\n"+ "|XXX|"+"\n");
                    i++;
                }
            }
        }


        resp = resp.concat("^");
        byte[] buf = resp.getBytes();
        DatagramPacket pack_envio = null;
        try {
            pack_envio = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS_ENVIO), PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket.send(pack_envio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void add_pesquisa(int id, String pesquisa){
        if(id==0){
            return;
        }
        //System.out.println("Id = " + id + " Pesquisa: " + pesquisa);

        lista_utilizadores.get(id-1).get_pesquisas().add(pesquisa);


        atualiza_ficheiro_user();
    }
    /** Função de pesquisar no index invertido e dar return na resposta à pesquisa do user*/
    public void faz_pesquisa(String pesquisa, MulticastSocket socket_envia) {
        String[]termos = pesquisa.split("\\s+");
        int contador = 0;
        int aux = 0;
        String resposta = "";
        String[] temp2 = new String[10000];
        Iterator it = index.entrySet().iterator();



        while (it.hasNext()) {
            HashMap.Entry key = (HashMap.Entry) it.next();

            for (int i = 0; i < termos.length; i++) {
                if(termos[i].length()>2){
                    if (key.getKey().equals(termos[i])) {
                        String[] temp =key.getValue().toString().split(", ");
                        for(int j = 0; j < temp.length; j++){
                            temp2[j] = Arrays.toString(new String[]{temp[j]});
                            temp2[j] = temp2[j].replace("[", "");
                            temp2[j] = temp2[j].replace("]", "");
                            Iterator it2 = titles.entrySet().iterator();
                            while(it2.hasNext()) {
                                HashMap.Entry key2 = (HashMap.Entry) it2.next();
                                //System.out.println("KEY2 KEY= "+key2.getKey());
                                //System.out.println("TEMP2 = " + temp2);
                                if (key2.getKey().equals(temp2[j])) {
                                    Iterator it3 = descricao.entrySet().iterator();
                                    while(it3.hasNext()){
                                        HashMap.Entry key3 = (HashMap.Entry) it3.next();
                                        //System.out.println("KEY 3 KEY = " + key3.getKey());
                                        //System.out.println("TEMP2 = " + temp2[j]);
                                        if(key3.getKey().equals(temp2[j])){
                                            contador++;
                                            resposta = resposta.concat(key.getKey().toString() +" - "+ contador + "|XXX|" + "-"+ key2.getValue().toString() + "|XXX|" +  "  - " + key2.getKey().toString() + "|XXX|" +
                                                    "  - " + key3.getValue().toString()+"\n\n"+"|XXX|"+"\n\n");
                                        }
                                    }


                                }
                            }
                        }
                        aux = 1;

                    }
                }


            }

        }
        resposta = addChar(resposta,String.valueOf(contador), 0);

        if (aux == 0){
            resposta = "Nenhum resultado encontrado!\n";
            byte[] buf = resposta.getBytes();


            DatagramPacket pack_envio = null;
            try {
                pack_envio = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS_ENVIO), PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                socket_envia.send(pack_envio);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] buf = resposta.getBytes();
        DatagramPacket pack_envio = null;
        try {
            pack_envio = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS_ENVIO), PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket_envia.send(pack_envio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Funcao de pesquisar urls que vao dar a outros URLS*/
    public void faz_pesquisa_url(String pesquisa, MulticastSocket socket_envia) {
        Iterator it = url_in_url.entrySet().iterator();
        int aux = 0;
        String[] temp2 = new String[10000];
        int contador = 1;

        String resp = "Links que vao dar ao site " + pesquisa + ":\n\n" + "|XXX|"+"\n";
        for(String key: url_in_url.keySet()){
          String keyy = key.toString();
          HashSet value = url_in_url.get(key);
          if(keyy.equals(pesquisa)){
            String[] temp = url_in_url.get(key).toString().split(", ");
            for(int j = 0; j < temp.length; j++){
                temp2[j] = Arrays.toString(new String[]{temp[j]});
                temp2[j] = temp2[j].replace("[", "");
                temp2[j] = temp2[j].replace("]", "");
                resp = resp.concat(contador+"." + "  " + temp2[j] + "\n" + "|XXX|" +"\n");
                contador++;
          }
          aux = 1;
      }
    	}




          if (aux == 0){
              resp = "Nenhum resultado encontrado!\n";
              byte[] buf = resp.getBytes();


              DatagramPacket pack_envio = null;
              try {
                  pack_envio = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS_ENVIO), PORT);
              } catch (UnknownHostException e) {
                  e.printStackTrace();
              }
              try {
                  socket_envia.send(pack_envio);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

          byte[] buf = resp.getBytes();
          DatagramPacket pack_envio = null;
          try {
              pack_envio = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_ADDRESS_ENVIO), PORT);
          } catch (UnknownHostException e) {
              e.printStackTrace();
          }
          try {
              socket_envia.send(pack_envio);
          } catch (IOException e) {
              e.printStackTrace();
          }

    }
    /**Função de adicionar o nº de resultados à resposta da função anterior no inicio*/
    public String addChar(String str, String ch, int position) {
        return str.substring(0, position) + ch + "\n" +  str.substring(position);
    	}


    public void anexar_url(String ws, int max_depth){
    	if(max_depth < DEPTH){
    		max_depth++;
    		try {

                if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                    ws = "http://".concat(ws);

                // Attempt to connect and get the document
            try{
                Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/
                if(!titles.containsKey(ws)){ titles.put(ws, doc.title());}


                String paragraphs = doc.body().text();
                try{
                	String p = paragraphs.substring(0, 100);
                	if (!descricao.containsKey(ws)){
                    descricao.put(ws, p);
                            
                }
                }catch(Exception e){
                	String p = "Sem descrição";
                	if (!descricao.containsKey(ws)){
                    descricao.put(ws, p);
                            
                }

                }
                url_to_array(ws);
                
                    

                     

                    



                // Get all links
                Elements links = doc.select("a[href]");
                for (Element link : links) {



                    // Ignore bookmarks within the page
                    if (link.attr("href").startsWith("#")) {
                        continue;
                    }

                    // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
                    if (!link.attr("href").startsWith("http")) {
                        continue;
                    }

                    if(!url_in_url.containsKey(link.attr("href").toString())){
                      HashSet<String> temp = new HashSet<String>();
                      temp.add(ws);
                      url_in_url.put(link.attr("href").toString(), temp);
                    }
                    if(url_in_url.containsKey(link.attr("href").toString())){
                      url_in_url.get(link.attr("href").toString()).add(ws);
                    }
                    //System.out.println("Link: " + link.attr("href"));
                    //System.out.println("Text: " + link.text() + "\n");


                    if (!titles.containsKey(link.attr("href").toString())) {
                        titles.put(link.attr("href").toString(),
                                link.text());
                    }


                    

                    url_to_array(link.attr("href").toString());

                    if(!UrlCountMap.containsKey(link.attr("href").toString())){
                        UrlCountMap.put(link.attr("href").toString(), 1);
                    }
                    else{
                        UrlCountMap.put(link.attr("href").toString(),
                                UrlCountMap.get(link.attr("href").toString()+1));
                    }

                    anexar_url(link.attr("href"), max_depth);

                }

                //System.out.println("Nº de resultados: " + contador);
                // Get website text and count words
                String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
             
           

                countWords(text, ws);
            }catch(HttpStatusException e){
                System.out.println("Unable to index URL: " + ws);
            }
            } catch (IOException e) {
                System.out.println("Unable to index URL: " + ws);
            }



                // Title
                //System.out.println("\n" + doc.title());


        //System.out.println("URL: " + ws +" anexado com sucesso!");
    	}

        
    }

    public void anexar_url2(String ws, int max_depth){
    	if(max_depth < DEPTH){
    		max_depth++;
    		try {

            if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                ws = "http://".concat(ws);

            // Attempt to connect and get the document
            //System.out.println("Website: " + ws);
            Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/
            if(!titles.containsKey(ws)){ titles.put(ws, doc.title());}
            String paragraphs = doc.body().text();
                try{
                	String p = paragraphs.substring(0, 100);
                	if (!descricao.containsKey(ws)){
                    descricao.put(ws, p);
                            
                }
                }catch(Exception e){
                	String p = "Sem descrição";
                	if (!descricao.containsKey(ws)){
                    descricao.put(ws, p);
                            
                }

                }
                url_to_array(ws);
                
                    

                

            // Title
            //System.out.println("\n" + doc.title());

            // Get all links
            Elements links = doc.select("a[href]");
            for (Element link : links) {

                // Ignore bookmarks within the page
                if (link.attr("href").startsWith("#")) {
                    continue;
                }

                // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
                if (!link.attr("href").startsWith("http")) {
                    continue;
                }

                if(!url_in_url.containsKey(link.attr("href").toString())){
                  HashSet<String> temp = new HashSet<String>();
                  temp.add(ws);
                  url_in_url.put(link.attr("href").toString(), temp);
                }
                if(url_in_url.containsKey(link.attr("href").toString())){
                  url_in_url.get(link.attr("href").toString()).add(ws);
                }

                //System.out.println("Link: " + link.attr("href"));
                //System.out.println("Text: " + link.text() + "\n");

                if(!titles.containsKey(link.attr("href").toString())){ titles.put(link.attr("href").toString(),
                        link.text());};



                url_to_array(link.attr("href").toString());
            
                if(!UrlCountMap.containsKey(link.attr("href").toString())){
                    UrlCountMap.put(link.attr("href").toString(), 1);
                }
                else{
                    UrlCountMap.put(link.attr("href").toString(),
                            UrlCountMap.get(link.attr("href").toString()+1));
                }
                anexar_url2(link.attr("href"), max_depth);


            }





            //System.out.println("Nº de resultados: " + contador);
            // Get website text and count words
            String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>

               





            countWords(text, ws);
        } catch(HttpStatusException e){
            System.out.println("Unable to index URL: " + ws);
        } catch (IOException e) {
            System.out.println("Unable to index URL: " + ws);
        }

        //System.out.println("URL: " + ws +" anexado com sucesso!");
    	}

        
    }

    public void url_to_array(String ws){
        if(!urls.contains(ws)){
            urls.add(ws);
        }
    }

    public void ler_urls(){
        File f1 = new File("urls.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String temp;
        int i = 0;
            try {
                while (((temp=br.readLine()) != null)) {
                    i++;
                    if (i > 100) {
                        break;
                    } else {
                        anexar_url2(temp,0);

                    }
                }
                System.out.println("Done!");
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void print_array() {
        System.out.println("Array: ");
        Iterator it = UrlCountMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry key = (HashMap.Entry) it.next();
            System.out.println("URL " + key.getKey() + "|  Titulo " + key.getValue());


        }
    }

    public void url_to_file(String ws){ //Recebe o vetor e adiciona no file
        File f1 = new File("urls.txt");



     if(f1.exists() && f1.isFile()){
     	try{
     		FileOutputStream writer = new FileOutputStream(f1);
		writer.write(("").getBytes());
		writer.close();

     	}catch(IOException e){
     		System.out.println("Erro url_to_file");
     	}
     	
         try{
             FileWriter fw = new FileWriter(f1, true);
             BufferedWriter bw = new BufferedWriter(fw);

             for (String temp : urls) {
                 bw.append(temp);
                 bw.newLine();
             }
             bw.close();
             fw.close();
         }catch(IOException e){
             System.out.println("Erro a escrever no file!");
         }


     }
     else{
         System.out.println("Ficheiro nao existe.");
     }

     ler_urls();
    } //Lê o vetor de url e mete no file

    private  void countWords(String text, String url) {
        Map<String, Integer> countMap = new TreeMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
        String line;

        // Get words and respective count
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
                String[] words = line.split("[ ,;:.?!(){}\\[\\]<>']+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if ("".equals(word)) {
                        continue;
                    }
                    if(index.containsKey(word)){ //Se ele  tiver ainda a palavra no HashMap

                       index.get(word).add(url);

                    }
                    else{
                        HashSet<String> temp = new HashSet<>();
                        temp.add(url);
                        index.put(word, temp);
                    }
                    if (!countMap.containsKey(word)) {
                        countMap.put(word, 1);
                    }
                    else {
                        countMap.put(word, countMap.get(word) + 1);

                    }
                   
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(index.size());

        // Close reader
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        // Display words and counts
        Iterator it = index.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry key = (HashMap.Entry) it.next();
            //System.out.println(" D " + key.getKey() + " " + key.getValue());
            //System.out.println(" P " + pesquisa);
            System.out.println("\n" + key.getKey() + " " + key.getValue());
        }*/


        /*
        for (String word : countMap.keySet()) {
            if (word.length() >= 3) { // Shall we ignore small words?
                System.out.println(word + "\t" + countMap.get(word));
            }
        }*/
    }

    /**le o ficheiro users e guarda tudo no atributo lista de utilizadores
    */
    public void le_ficheiros(){

        File f1 = new File("users.txt");
        if (f1.exists() && f1.isFile()){
            try{
                FileReader fr = new FileReader(f1);
                BufferedReader br = new BufferedReader(fr);

                String linha;
                while((linha = br.readLine()) != null){
                    String[] aux = linha.split(":");
                    //   0 1    2    3      4        5         6          7
                    //  ':id:admin:nome:username:password:notificacoes:pesquisas'

                    int id = Integer.parseInt(aux[1]);
                    int admin = Integer.parseInt(aux[2]);
                    String nome = aux[3];
                    String username = aux[4];
                    String password = aux[5];
                    String notificacoes = aux[6];
                    //System.out.println("Noticicações = " + aux[6]);
                    //System.out.println("Pesquisas = " + aux[7]);

                    String pesquisas = aux[7];

                    String[] pes = pesquisas.split("-");

                    ArrayList<String> temp = new ArrayList<>();
                    for(int i = 0; i < pes.length; i++){
                        temp.add(pes[i]);
                    }







                    boolean aux_ad = true;
                    boolean ativo = true;
                    if(admin == 0){
                        aux_ad = false;
                    }
                    User novo = new User(nome, username, password, id, aux_ad, temp, notificacoes);
                    lista_utilizadores.add(novo);
                }
                br.close();
            }
            catch (FileNotFoundException ex) {
                System.out.println("Erro a abrir o ficheiro.");
            }
            catch (IOException ex) {
                System.out.println("Erro a ler o ficheiro.");
            }
        }
        else {
            System.out.println("Ficheiro nao existe.");
        }
    }
    /**Atualiza o ficheiro users, vai buscar a lista de utilizadores e substitui o ficheiro
    */
    public void atualiza_ficheiro_user(){
        File f1 = new File("users.txt");
        String pesq = "";
        if (f1.exists() && f1.isFile()){
            try{
                FileWriter fw = new FileWriter(f1);
                BufferedWriter bw = new BufferedWriter(fw);
                //  0 1    2    3      4        5          6           7
                //  ':id:admin:nome:username:password:notificacoes:pesquisas'
                for(int i=0; i< lista_utilizadores.size();i++){
                    int aux = 0;
                    int aux_ativo = 0;
                    if(lista_utilizadores.get(i).get_administrador()){
                        aux = 1;
                    }
                    pesq = "";
                    for(int j = 0; j < lista_utilizadores.get(i).get_pesquisas().size(); j++){

                        pesq = pesq.concat(lista_utilizadores.get(i).get_pesquisas().get(j));
                        pesq = pesq.concat("-");
                    }
                    //System.out.println("pesq de = " + i + "| Pesquisa = " + pesq);

                    bw.append(":"+lista_utilizadores.get(i).get_id()+":"+aux+":"+lista_utilizadores.get(i).get_nome()+":"+lista_utilizadores.get(i).get_username()+":"+lista_utilizadores.get(i).get_password()+":"+lista_utilizadores.get(i).get_notificacoes()+":"+pesq+":");
                    bw.newLine();
                }
                bw.close();

                fw.close();
            }
            catch (IOException ex) {
                System.out.println("Erro a escrever no ficheiro.");
            }
        }
        else {
            System.out.println("Ficheiro nao existe.");
        }
    }

    /** Quando um utilizador se tenta registar recebe uma mensagem se o username com que se tentou registar ja esta a ser usado
    * envia TRUE-> username valido, FALSE-> usarname ja existe
    */
    public boolean verifica_username_registo(String username){
        int i=0;
        while(  i < lista_utilizadores.size() && lista_utilizadores.get(i).get_username().compareTo(username) != 0){
            i++;
        }
        if(i < lista_utilizadores.size() ){ //encontrou o username
            return false;
        }
        return true;
    }

    /** Quando um utilizador altera o seu perfil recebe uma mensagem se o username com que se tentou registar ja esta a ser usado
    * envia TRUE-> username valido, FALSE-> usarname ja existe
    */
    public boolean verifica_username_altera(String username, int id){
        int i=0;
        while(  i < lista_utilizadores.size() && lista_utilizadores.get(i).get_username().compareTo(username) != 0){
            i++;
        }
        if(i < lista_utilizadores.size() ){ //encontrou o username
            if(id-1 == i){//mantem o mesmo username
                return true;
            }
            else{//username pertence a outra pessoa
                return false;
            }

        }
        return true;
    }

    /**Envia o ID de um utilizador
    */
    public int pedir_id(String username){
        int i=0;
        int aux = 0;
        while( i < lista_utilizadores.size() && lista_utilizadores.get(i).get_username().compareTo(username) != 0){

            i++;
        }
     
        return i+1;
    
    }

    /**Envia o nome de um utilizador
    */
    public String pedir_nome(int id){
        String nome = lista_utilizadores.get(id-1).get_nome();
        return nome;
    }

    /**Envia o username de um utilizador
    */
    public String pedir_username(int id){
        String username = lista_utilizadores.get(id-1).get_username();
        return username;
    }

    /**Envia a password de um utilizador
    */
    public String pedir_password(int id){
        String password = lista_utilizadores.get(id-1).get_password();
        return password;
    }

    /**Envia as pesquisas de um utilizador
    */
    public ArrayList<String> pedir_pesquisas(int id){
        ArrayList<String> pesquisas = lista_utilizadores.get(id-1).get_pesquisas();
        return pesquisas;
    }

    /**recebe um id e verifica se esse utilizador E administrador ou nao
    */
    public boolean verifica_administrador(int id){
        if(lista_utilizadores.get(id-1).get_administrador() == false){
            return false;
        }
        return true;
    }

    /**usado no login de um utilizador
    * verifica se as informacoes sao validas
    */
    public int recebe_username_password(String username, String password){


        //Verificar Username e password
        int i=0;
        while(  i < lista_utilizadores.size() && lista_utilizadores.get(i).get_username().compareTo(username) != 0){
            i++;
        }
        if(i >= lista_utilizadores.size() ){ //correu o arraylist todo e nao encontrou o username
            return 1;
        }
        else if(lista_utilizadores.get(i).get_password().compareTo(password) !=0){ //encontrou username mas password incorreta
            System.out.println(lista_utilizadores.get(i).get_password());
            return 2;
        }
        else{//encontrou
            if(lista_utilizadores.get(i).get_administrador() == true){
                //System.out.println("Entrou um Administrador");
                return 3;
            }
            else{
                //System.out.println("Entrou um Utilizador");
                return 4;
            }
        }
    }

    /**recebe um username e atualiza a lista de utilizadores
    *tranforma um utilizador em administrador
    */
    public void menu_conceder_privilegios(String username){

        int i=0;
        while(  i < lista_utilizadores.size() && lista_utilizadores.get(i).get_username().compareTo(username) != 0){
            i++;
        }
        if(lista_utilizadores.get(i).get_administrador() == false){
            lista_utilizadores.get(i).set_administrador(true);
            lista_utilizadores.get(i).set_notificacoes("Recebeu o estatuto de administrador.");
            atualiza_ficheiro_user();
        }
    }

}
