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
import java.util.*;


public class jsoup_test{
	public static void main(String[] args) {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		//Ler URL
		System.out.print("Website: ");
		try{


			String url = bf.readLine();
			if(! url.startsWith("http://") && url.startsWith("https://")){
				url = "http://".concat(url); //Se nao comecar com http ele insere automaticamente
			}

			//Usar o Jsoup para conectar ao site
			Document doc = Jsoup.connect(url).get();

			//Titulo
			System.out.println(doc.title() + "\n");

			//Ir buscar as palavras do site
			String text = doc.text();
			countWords(text);

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void countWords(String text){
		Map<String, Integer> countMap = new TreeMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
		String line;

		//Get words
		while(true){
			try{
				if((line = reader.readLine()) == null){
					break;
				}
				String[] words = line.split("[ ,;:.?!(){}\\[\\]<>']+");
				for(String word : words){
					word = word.toLowerCase();

					if("".equals(word)){
						continue; //Se a palavra for null dar continue
					}

					if(!countMap.containsKey(word)){//Se a palavra nao esta no countmap
						countMap.put(word,1); //Dar add dela e meter o count a 1
					}
					else{ //Se tiver basta somar 1 ao count
						countMap.put(word, countMap.get(word)+1); 
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		//Close reader
		try{
			reader.close();
		}catch(IOException e ){
			e.printStackTrace();
		}

		//display words
		for(String word : countMap.keySet()){
			if(word.length() >= 3){
				System.out.println(word +  "\t" + countMap.get(word));
			}
		}
	}
}