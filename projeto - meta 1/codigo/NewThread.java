import java.rmi.registry.LocateRegistry;
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
class NewThread implements Runnable {
  String name;
  Thread t;
  int identificador;
  boolean menu = false;
  boolean saida = false;
  boolean rmi_comp = true;
  NewThread(int id) {
    this.identificador = id;
    name = "Notificacoes RMI Server";
    t = new Thread(this, name);
    //System.out.println("New thread: " + t);
    t.start(); // Start the thread
  }
  public void saida_cliente(){
    this.saida = true;
  }

  public void run() { 
    int rmi1 = 0;
    int rmi2 = 0;
    // entry point
    while(menu == false){
      if(rmi_comp == true){
        rmi1 = 7000;
        rmi2 = 6000;
      }
      else{
        rmi1 = 6000;
        rmi2 = 7000;
      }
      try {

        Rmi h = (Rmi) LocateRegistry.getRegistry(rmi1).lookup("projeto");

        String message = h.verifica_notificacoes_constante(identificador);
        if(message.compareTo(" ") != 0){
          System.out.println("\n\nNotificacao: " + message);
          System.out.println("Para aceder ao Menu Administrador volte a iniciar sessao.");
          menu = true;
        }
                    
      } catch (Exception e) {
        try{
          Rmi h = (Rmi) LocateRegistry.getRegistry(rmi2).lookup("projeto");

          String message = h.verifica_notificacoes_constante(identificador);
          if(message.compareTo(" ") != 0){
            System.out.println("\n\nNotificacao: " + message);
            System.out.println("Para aceder ao Menu Administrador volte a iniciar sessao.");
            menu = true;

            if(rmi_comp == true){
              rmi_comp = false;
            }
            else{
              rmi_comp = true;
            }
          }

        } catch (Exception es) {
          System.out.println("Exception in main: " + es);
          e.printStackTrace();
        }
      }
      try{
        t.sleep(20000);
      }catch(InterruptedException e){
        System.out.println("Erro sleep thread");
      }
      if(saida == true){
        menu = true;
      }
    }
  }
}