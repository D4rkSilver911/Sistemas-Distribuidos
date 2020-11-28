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

public class MulticastServer2 extends Thread {
    private String MULTICAST_ADDRESS_RECEBE = "224.0.230.0";
    private String MULTICAST_ADDRESS_ENVIO = "224.0.230.1";
    private String MULTICAST_ADDRESS_RECEBE_NOTIFICA = "224.0.230.2";
    private String MULTICAST_ADDRESS_ENVIO_NOTIFICA = "224.0.230.3";

    private int PORT = 4321;
    private long SLEEP_TIME = 5000;

    private ArrayList<User> lista_utilizadores = new ArrayList<>();
    private HashMap<String, HashSet<String>> index = new HashMap<>(); //Index invertido
    private ArrayList<String> urls = new ArrayList<>(); //URLS
    private HashMap<String, String> titles = new HashMap<>(); //Array de urls e os seus titles
    private HashMap<String, String> descricao = new HashMap<>(); //Array de urls e as suas descrições


    public static void main(String[] args) {
        MulticastServer2 server = new MulticastServer2();
        server.start();
    }

    public MulticastServer2() {
        super("Server 2 " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running...");
        System.out.println("Anexando URLS... ");
        //ler_urls();
        System.out.println("Completo!");
        //print_array();


        //------------------------------------------ Buscar Informaçao --------------------------------------------
        //le_ficheiros();//------ Abrir Ficheiro Utilizadores-------


        try {
            //-------------------------------------------- Inicar Sockets ---------------------------------------------
        	InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_RECEBE);
            socket = new MulticastSocket(PORT);  // Socket para receber
            MulticastSocket socket_envia = new MulticastSocket(); //Socket para enviar
            socket.joinGroup(group);
            //socket.setTimeToLive(1);

            new Aux_TCP();

            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /* Função de pesquisar no index invertido e dar return na resposta à pesquisa do user*/
    public void faz_pesquisa(String pesquisa, MulticastSocket socket_envia) {
        String[] termos = pesquisa.split("\\s+");
        int contador = 0;
        int aux = 0;
        String resposta = "";
        String[] temp2 = new String[10000];
        Iterator it = index.entrySet().iterator();



        while (it.hasNext()) {
            HashMap.Entry key = (HashMap.Entry) it.next();

            for (int i = 0; i < termos.length; i++) {
                if (key.getKey().equals(termos[i])) {

                    String[] temp = key.getValue().toString().split(", ");

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
                                        resposta = resposta.concat(contador + " - "+ key2.getValue().toString() + "\n" +  "  - " + key2.getKey().toString() + "\n" +
                                                "  - " + key3.getValue().toString()+"\n\n");
                                    }
                                }


                            }
                        }
                    }
                    aux = 1;

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

    /*Função de adicionar o nº de resultados à resposta da função anterior no inicio*/
    public String addChar(String str, String ch, int position) {
        return str.substring(0, position) + ch + "\n" +  str.substring(position);
    }


    public void anexar_url(String ws){

        int contador = 0;

        //ArrayList<String> links_rec = new ArrayList<>(); //recursivo?
       
                if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                    ws = "http://".concat(ws);

                // Attempt to connect and get the document
            try{
                Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/
                if(!titles.containsKey(ws)){ titles.put(ws, doc.title());}



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

                    //System.out.println("Link: " + link.attr("href"));
                    //System.out.println("Text: " + link.text() + "\n");
                    contador++;

                    if (!titles.containsKey(link.attr("href").toString())) {
                        titles.put(link.attr("href").toString(),
                                link.text());
                    }


                    Elements paragraphs = link.select("p");
                    for(Element p : paragraphs){

                        if (!descricao.containsKey(link.attr("href").toString())) {
                            descricao.put(link.attr("href").toString(), p.text());
                            break;
                        }
                        break;

                    }

                    url_to_array(link.attr("href").toString());
                }

                //System.out.println("Nº de resultados: " + contador);
                // Get website text and count words
                String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
                Elements paragraphs = doc.select("p");
                for(Element p : paragraphs){
                    //System.out.println(ws + " | " + p.text());
                    if (!descricao.containsKey(ws)) {
                        descricao.put(ws, p.text());
                        break;
                    }
                    break;
                }

                countWords(text, ws);
            }        catch (IOException e) {
                e.printStackTrace();
            }



                // Title
                //System.out.println("\n" + doc.title());


        //System.out.println("URL: " + ws +" anexado com sucesso!");
    }
    
    public void anexar_url2(String ws){


        int contador = 0;
        int j = 0;
        //ArrayList<String> links_rec = new ArrayList<>(); //recursivo?
        try {

            if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                ws = "http://".concat(ws);

            // Attempt to connect and get the document
            //System.out.println("Website: " + ws);
            Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/
            if(!titles.containsKey(ws)){ titles.put(ws, doc.title());}

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

                //System.out.println("Link: " + link.attr("href"));
                //System.out.println("Text: " + link.text() + "\n");
                contador++;

                if(!titles.containsKey(link.attr("href").toString())){ titles.put(link.attr("href").toString(),
                        link.text());};




                Elements paragraphs = link.select("p");
                for(Element p : paragraphs){
                   // System.out.println(ws + " | " + p.text());
                    if (!descricao.containsKey(link.attr("href").toString())) {
                        descricao.put(link.attr("href").toString(), p.text());
                        break;
                    }
                    break;
                }


            }





            //System.out.println("Nº de resultados: " + contador);
            // Get website text and count words
            String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>

                Elements paragraphs = doc.select("p");
                for(Element p : paragraphs){
                    //System.out.println(ws + " | " + p.text());
                    if (!descricao.containsKey(ws)) {
                        descricao.put(ws, p.text());
                        break;
                    }
                    break;
                }






            countWords(text, ws);
        }catch (IOException e) {
            e.printStackTrace();
        }}

        //System.out.println("URL: " + ws +" anexado com sucesso!");
    

    public void url_to_array(String ws){
        if(!urls.contains(ws)){
            urls.add(ws);
        }
    }

    public void ler_urls(){
        File file = new File ("urls.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
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
                        anexar_url2(temp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void print_array() {
        System.out.println("Array: ");
        Iterator it = titles.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry key = (HashMap.Entry) it.next();
            System.out.println("URL " + key.getKey() + "|  Titulo " + key.getValue());


        }
    }

    public void url_to_file(String ws){ //Recebe o vetor e adiciona no file
     File f1 = new File("urls.txt");



     if(f1.exists() && f1.isFile()){
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
    /*
    public void le_ficheiros(){// vai ler o ficheiro users e coloca toda a informação num ArrayList

        File f1 = new File("users_aux.txt");
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
                    String pesquisas = aux[7];

                    boolean aux_ad = true;
                    boolean ativo = true;
                    if(admin == 0){
                        aux_ad = false;
                    }
                    User novo = new User(nome, username, password, id, aux_ad, pesquisas, notificacoes);
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
    }*/

    public void atualiza_ficheiro_user(){//Atualiza o ficheiro users
        File f1 = new File("users_aux.txt");
        if (f1.exists() && f1.isFile()){
            try{
                FileWriter fw = new FileWriter(f1);
                BufferedWriter bw = new BufferedWriter(fw);
                //  0 1    2    3      4        5          6           7
                //  ':id:admin:nome:username:password:notificacoes:pesquisas'
                for(int i=0; i< lista_utilizadores.size();i++){
                    int aux = 0;
                    int aux_ativo = 0;
                    if(lista_utilizadores.get(i).get_administrador()== true){
                        aux = 1;
                    }

                    bw.append(":"+lista_utilizadores.get(i).get_id()+":"+aux+":"+lista_utilizadores.get(i).get_nome()+":"+lista_utilizadores.get(i).get_username()+":"+lista_utilizadores.get(i).get_password()+":"+lista_utilizadores.get(i).get_notificacoes()+":"+lista_utilizadores.get(i).get_pesquisas() );
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

}