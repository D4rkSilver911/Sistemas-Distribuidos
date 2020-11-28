import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/** classe que comunica com o MulticastServer apenas para atualizacao do ficheiro users
* recebe de x em x tempo um ciclo de pacotes
* cada pacote contem a informacao de um User
*/

public class Aux_TCP implements Runnable {
    String word_recebida, word_envia;
    DataInputStream recebe_Multicast;
    DataOutputStream envia_Multicast;
    Socket clientSocket;
    ServerSocket welcomeSocket;
    int thread_number;
    Thread t;
    ArrayList<User> lista_utilizadores;
    ObjectInputStream in_lista;
    Aux_TCP(){
        String name = "Thread 2 Comunica entre Multicasts ";
        t = new Thread(this, name);
        System.out.println("Criou "+name+" numero: ");
        t.start();
        lista_utilizadores = new ArrayList<User>();
    }

    public void run(){
        int conta =0;
        try{
            t.sleep(1000);
        }catch(InterruptedException e){
            System.out.println("Erro sleep thread");
        }
        try{
            /**cria a ligacao
            */
            clientSocket = new Socket("localhost",5555);
                    
            recebe_Multicast = new DataInputStream(clientSocket.getInputStream());/**create input stream, attached to socket*/ 
            envia_Multicast = new DataOutputStream(clientSocket.getOutputStream());/**create output stream, attached to socket*/
            in_lista = new ObjectInputStream(clientSocket.getInputStream());
        }catch(Exception ex){}

        try{
            while(true){
                lista_utilizadores = new ArrayList<User>();
                System.out.println("\n\nA receber do Multicast secundario...");
                /** recebe uma string com um numero
                * esse numero significa o numero de elementos na lista de utilizadores
                */
                word_recebida = recebe_Multicast.readUTF();
                System.out.println(word_recebida);
                int num =Integer.parseInt(word_recebida);
                /** depois de ja saber quantos pacotes vou receber
                * crio um ciclo for onde vai receber um User de cada vez e guardar a informacao na lista de utilizadores
                */
                User novo = new User();
                for(int i=0; i< num;i++){
                    try{
                        novo = (User) in_lista.readObject();
                        lista_utilizadores.add(novo);
                    }catch(ClassNotFoundException e){
                        System.out.println("erro a ler");
                    }
                }
                /** quando acabo de atualizar a lista de utilizadores guardo tudo num ficheiro
                */
                System.out.println("Atualizou ficheiro");
                atualiza_ficheiro_user();

                /**a thread dorme 30 segundos para nao sempre a comunicar e a abrir e fechar ficheiros
                */
                try{
                    t.sleep(30000);
                }catch(InterruptedException e){
                    System.out.println("Erro sleep thread");
                }
            }
        }catch(EOFException e){System.out.println("EOF:" + e);
        }catch(IOException e){System.out.println("IO:" + e);
        }finally {
            if (clientSocket != null)
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("close:" + e.getMessage());
            }
        }
    }

    /**atualiza o ficheiro user_aux com a os dados da lista de utilizadores
    */
    public void atualiza_ficheiro_user(){//Atualiza o ficheiro users
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

}