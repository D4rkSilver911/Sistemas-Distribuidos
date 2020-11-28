package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        HashMap<String, HashSet<String>> index = new HashMap<>();
        menu(index);
    }
    public static HashMap<String, HashSet<String>> menu(HashMap<String, HashSet<String>> index) throws IOException {
        Scanner sc = new Scanner(System.in);


        System.out.println("Insira o que quer fazer: ");
        System.out.println("1. Anexar URLS");
        System.out.println("2. Pesquisar");
        System.out.print("Opção: ");
        int opcao = sc.nextInt();
        switch (opcao){
            case 1:
                BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Website a Indexar: ");
                String ws = bf.readLine();

                index = add_url(index, ws);
                index = menu(index);
                break;

            case 2: fazer_pesquisa(index);
                    index = menu(index);
                    break;
        }
        return index;
    }
    public static HashMap<String, HashSet<String>>  add_url(HashMap<String, HashSet<String>> index, String ws) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> links_rec = new ArrayList<>();

        int contador = 0;
        // Read website

            try {

                if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                    ws = "http://".concat(ws);

                // Attempt to connect and get the document
                Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/

                // Title
                System.out.println(doc.title() + "\n");

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

                    System.out.println("Link: " + link.attr("href"));
                    System.out.println("Text: " + link.text() + "\n");
                    contador++;

                    links_rec.add(link.attr("href"));


                }





                System.out.println("Nº de resultados: " + contador);
                // Get website text and count words
                String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
                index = countWords(text, ws, index);
            } catch (IOException e) {
                e.printStackTrace();
            }


        return index;

    }



    private static HashMap<String, HashSet<String>> countWords(String text, String url, HashMap<String, HashSet<String>> index) {
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

        // Display words and counts




        /*
        for (String word : countMap.keySet()) {
            if (word.length() >= 3) { // Shall we ignore small words?
                System.out.println(word + "\t" + countMap.get(word));
            }
        }*/

        return index;
    }

    private static void fazer_pesquisa(HashMap<String, HashSet<String>> index){
        Scanner sc = new Scanner(System.in);
        System.out.print("Insira o(s) termo(s) de pesquisa (separados por 'espaço'): ");
        String pesquisa = sc.nextLine();
        String[] termos = pesquisa.split("\\s+");
        int aux = 0;
        Iterator it = index.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry key = (HashMap.Entry) it.next();
            //System.out.println(" D " + key.getKey() + " " + key.getValue());
            //System.out.println(" P " + pesquisa);
            for(int i = 0; i < termos.length; i++){
                if(key.getKey().equals(termos[i])){
                    aux = 1;
                    System.out.println("\n" + key.getKey() + " " + key.getValue());
                }
            }



        }
        if (aux == 0){
            System.out.println("\nNenhum resultado encontrado!\n");
        }



    }
}
