import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.*;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;

class Thread_Multicast implements Runnable {
  String name;
  Thread t;
  ArrayList<User> lista_utilizadores;
  private String MULTICAST_ADDRESS_RECEBE_NOTIFICA = "224.0.230.2";
  private String MULTICAST_ADDRESS_ENVIO_NOTIFICA = "224.0.230.3";
  private int PORT = 4322;
  private long SLEEP_TIME = 5000;
  MulticastSocket socket = null;

  Thread_Multicast(ArrayList<User> lista) {
    this.lista_utilizadores = lista;
    name = "Notificacoes Multicast";
    t = new Thread(this, name);
    System.out.println("\nNew thread: " + t);
    t.start(); // Start the thread
  }

  public void atualiza_ficheiro_user(){//Atualiza o ficheiro users
        File f1 = new File("com/company/users.txt");
        if (f1.exists() && f1.isFile()){
            try{
                FileWriter fw = new FileWriter(f1);
                BufferedWriter bw = new BufferedWriter(fw);
                //  0 1    2    3      4        5       6        7           8    
                //  ':id:admin:nome:username:password:ativo:notificacoes:pesquisas'
                for(int i=0; i< lista_utilizadores.size();i++){
                    int aux = 0;
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

  public void atualiza_lista(ArrayList<User> lista){
    this.lista_utilizadores = lista;
  }

  public void run() {      // entry point
    try {
      InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS_RECEBE_NOTIFICA);
      socket = new MulticastSocket(PORT);  // Socket para receber
      MulticastSocket socket_envia = new MulticastSocket(); //Socket para enviar
      socket.joinGroup(group);
      socket.setTimeToLive(1);
      while(true){
      
        //RECEBE INFORMAÇÃO DO RMISERVER
        byte buf[] = new byte[1024];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        socket.receive(pack);
        buf = pack.getData();
        String mensagem = new String(buf);
        System.out.println("Received packet from " + pack.getAddress().getHostAddress() + ":" + pack.getPort() + " with message:");
        String message = new String(pack.getData(), 0, pack.getLength());
        System.out.println(message);
                
        String[] aux = mensagem.split(":");
      
        //envio da notificacao
        //verifica notificacoes
        int id = Integer.parseInt(aux[1]);//id do utilizador da notificacao
        String confirma = lista_utilizadores.get(id-1).get_notificacoes();
        if(confirma.compareTo(" ") != 0){
          lista_utilizadores.get(id-1).set_notificacoes(" ");
          atualiza_ficheiro_user();
        }

        //envia resposta
        System.out.println("Thread-> Verifica notificacoes: "+confirma+" -> "+id);

        confirma = confirma.concat(":");
        byte buf_envia[] = confirma.getBytes();
        DatagramPacket pack_envio = new DatagramPacket(buf_envia, buf_envia.length,
        InetAddress.getByName(MULTICAST_ADDRESS_ENVIO_NOTIFICA),PORT);

        socket_envia.send(pack_envio);
      }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        socket.close();
      }
      try{
        t.sleep(20000);
      }catch(InterruptedException e){
        System.out.println("Erro sleep thread");
      }
  }
}