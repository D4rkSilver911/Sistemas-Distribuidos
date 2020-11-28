import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/** classe que com o MulticastServer2 apenas para atualizacao do ficheiro users
* envia de x em x tempo um ciclo de pacotes
* cada pacote contem a informacao de um User
*/
public class Conecao1_TCP implements Runnable {
    String word_recebida, word_envia;
    DataInputStream recebe_Multicast;
    DataOutputStream envia_Multicast;
    ObjectInputStream recebe_Multicast_lista;
    ObjectOutputStream envia_Multicast_lista;
    Socket connectionSocket;
    ServerSocket welcomeSocket;
    int thread_number;
    Thread t;
    ArrayList<User> lista_utilizadores;
    Conecao1_TCP(ArrayList<User> lista){
        this.lista_utilizadores = lista;
        
        
        String name = "Thread Comunica entre Multicasts ";
        t = new Thread(this, name);
        System.out.println("Criou "+name+" numero: ");
        t.start();
        
    }

    public void run(){
        int conta = 0;
        try{
            welcomeSocket = new ServerSocket(5555);/**create welcoming socket at port 5555*/
            connectionSocket = welcomeSocket.accept();/**wait, on welcoming socket for contact by client*/
            recebe_Multicast = new DataInputStream(connectionSocket.getInputStream());/*create input stream, attached to socket*/ 
            envia_Multicast = new DataOutputStream(connectionSocket.getOutputStream());/*create output stream, attached to socket*/
            envia_Multicast_lista = new ObjectOutputStream(connectionSocket.getOutputStream());

        }catch(Exception ex){}
        try{
            while(true){
                /** envia uma string com o numero de elementos na lista de utilizadores
                */
                word_envia = String.valueOf(lista_utilizadores.size());
                envia_Multicast.writeUTF(word_envia);

                System.out.println("\n\nA enviar do Multicast para Multicast secundario...");
                System.out.println("Atualizar Lista Utilizadores");

                /** envia um conjunto de pacotes
                * cada pacote tem a informacao de um utilizador
                */
                for(int i=0; i< lista_utilizadores.size(); i++){
                    
                    User p = lista_utilizadores.get(i);
                    envia_Multicast_lista.writeObject(p);
                }

                try{
                    t.sleep(30000);
                }catch(InterruptedException e){
                    System.out.println("Erro sleep thread");
                }
            }
        }catch(EOFException e){System.out.println("EOF:" + e);
        }catch(IOException e){System.out.println("IO:" + e);
        }
    }

}