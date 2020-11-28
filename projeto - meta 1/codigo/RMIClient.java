import java.rmi.registry.LocateRegistry;
import java.io.IOException;
import java.io.*;
import java.io.BufferedReader;
import java.util.Scanner;

/**RMI Cliente
*tem a interface que o utilizador tem acesso
*/
public class RMIClient {
    private static  String IP_GABRIEL = "localhost";
    /**Cria uma variavel rmi_comp que vai andar a circular por todas as variaveis
    * Esta variavel serve para saber que servidor RMI esta ativo
    * chama a funcao menu:inicial()
    * chama o RMIServer.policy que serve para dois PCs diferentes poderem comunicar
    */
	public static void main(String args[]) {

		/* This might be necessary if you ever need to download classes:
		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		*/
        System.getProperties().put("java.security.policy", "RMIServer.policy");
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        boolean rmi_comp = true;//true-> rmi1 = 7000; false-> rmi1 = 6000

        menu_inicial(rmi_comp);
	}

	/**mostra a interface do menu inicial
    * E possivel realizar pesquisa
    * Fazer login
    * Fazer registo
    */
    public static void menu_inicial(boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU ------------------------------------------------
        //-------------------------------------------------------------------------------------------------

            //------- Menu Inicial ---------
            //1-> Iniciar Sessao
            //2-> Fazer Registo
            //3-> Informaçao paginas web
            //4-> Sair da aplicaçao
            int menu=0;
            do{
                System.out.println("\n\n\n\n\n\n\n");
                System.out.println("            _______________________________________");
                System.out.println("            |            Menu Inicial             |");
                System.out.println("            |                                     |");
                System.out.println("            |   1-> Iniciar Sessao                |");
                System.out.println("            |   2-> Fazer Registo                 |");
                System.out.println("            |   3-> Fazer Pesquisa Termo          |");
                System.out.println("            |   4-> Fazer Pesquisa Link           |");
                System.out.println("            |   5-> Sair                          |");
                System.out.println("            |_____________________________________|");

                System.out.print("              Que opcao deseja realizar: ");
                String frase = sc.nextLine();
                try{
                    menu =Integer.parseInt(frase);

                }catch(NumberFormatException ne){
                    System.out.println("\n");
                    System.out.println("Introduza um numero!");
                    System.out.println("\n");
                    menu_inicial(rmi_comp);
                }

                switch(menu){
                    case 1:{//inicia sessao
                        menu_login(rmi_comp);
                        break;
                    }
                    case 2:{//registo
                        menu_registo(rmi_comp);
                        break;
                    }
                    case 3:{
                        menu_pesquisa(0, rmi_comp);
                        break;
                    }
                    case 4:{
                      menu_pesquisa_url(0, rmi_comp);
                      break;
                    }

                }

            }while(menu !=5);
            System.exit(0);
    }

    /**O utilizador tem de escrever os seus dados(username e password)
    * O servidor comunica com o RMI Server para validar os dados
    * Apos ser aceite, entra ou no menu utilizador ou no menu administrador
    * Se entrar um administrador vai enviar uma mensagem para o RMI Server para saber se o administrador recebeu alguma
    *notificacao enquanto esteve offline
    */
    public static void menu_login(boolean rmi_comp){

        Scanner sc= new Scanner(System.in);
        int id = 0;
        int rmi1 = 0;
        int rmi2 = 0;

        //------- LOGIN ---------

        boolean menu = true;
        do{
            System.out.println("\n\n\n\n\n\n\n");
            System.out.println("                _____________");
            System.out.println("                |           |");
            System.out.println("                |   LOGIN   |");
            System.out.println("                |___________|");
            System.out.println("                             ");
            System.out.println("                             ");
            System.out.print("                Username: ");
            String username = sc.nextLine();
            System.out.print("                Password: ");
            String password = sc.nextLine();

            int resp = 0;
            if(rmi_comp == true){
                rmi1 = 7000;
                rmi2 = 6000;
            }
            else{
                rmi1 = 6000;
                rmi2 = 7000;
            }
            try {

                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                resp = h.recebe_username_password(username, password);

            } catch (Exception e) {
                try{
                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                    resp =h.recebe_username_password(username, password);
                    if(rmi_comp == true){
                        rmi_comp = false;
                    }
                    else{
                        rmi_comp = true;
                    }

                } catch (Exception es) {
                    System.out.println("Exception in main: " + es);
                    e.printStackTrace();
                }
            }


            switch(resp){
                case 1:{//username incorreto, continua a correr menu
                    System.out.println("\n\n    Username nao existe!\n    Tente novamente.");
                    break;
                }
                case 2:{//password incorreta... continua a correr menu
                    System.out.println("\n\n    Password incorreta!    Tente novamente.");
                    break;
                }
                case 3:{
                    System.out.println("Entrou um Administrador");
                    //pedir ID
                    if(rmi_comp == true){
                        rmi1 = 7000;
                        rmi2 = 6000;
                    }
                    else{
                        rmi1 = 6000;
                        rmi2 = 7000;
                    }
                    try {

                        Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                        id = h.pedir_id(username);

                    } catch (Exception e) {
                        try{
                            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                            id = h.pedir_id(username);
                            if(rmi_comp == true){
                                rmi_comp = false;
                            }
                            else{
                                rmi_comp = true;
                            }
                        } catch (Exception es) {
                            System.out.println("Exception in main: " + es);
                            e.printStackTrace();
                        }
                    }
                    //========================================================================================
                    //============================ verificar se tem notificacao
                    if(rmi_comp == true){
                        rmi1 = 7000;
                        rmi2 = 6000;
                    }
                    else{
                        rmi1 = 6000;
                        rmi2 = 7000;
                    }

                    try {

                        Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");

                        String message = h.verifica_notificacoes(id);
                        if(message.compareTo(" ") != 0){
                            System.out.println("\n\nNotificacao: " + message);
                        }

                    } catch (Exception e) {
                        try{
                            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");

                            String message = h.verifica_notificacoes(id);
                            if(message.compareTo(" ") != 0){
                                System.out.println("\n\nNotificacao: " + message);
                            }
                            if(rmi_comp == true){
                                rmi_comp = false;
                            }
                            else{
                                rmi_comp = true;
                            }
                        } catch (Exception es) {
                            System.out.println("Exception in main: " + es);
                            e.printStackTrace();
                        }
                    }
                    menu_administrador(id, rmi_comp);
                    break;
                }
                case 4:{
                    //pedir ID
                    if(rmi_comp == true){
                        rmi1 = 7000;
                        rmi2 = 6000;
                    }
                    else{
                        rmi1 = 6000;
                        rmi2 = 7000;
                    }
                    try {

                        Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                        id = h.pedir_id(username);

                    } catch (Exception e) {
                        try{
                            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                            id = h.pedir_id(username);
                            if(rmi_comp == true){
                                rmi_comp = false;
                            }
                            else{
                                rmi_comp = true;
                            }
                        } catch (Exception es) {
                            System.out.println("Exception in main: " + es);
                            e.printStackTrace();
                        }
                    }
                    menu_utilizador(id, rmi_comp);
                    break;
                }
            }




        }while(menu == true);
    }

    /**Cria um novo perfil
    *Apos preencher todos os dados entra no menu utilizador
    */
    public static void menu_registo(boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        int id =0;
        int rmi1 = 0;
        int rmi2 = 0;

        //------- Registo ---------

        boolean menu = true;

        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("                _____________");
        System.out.println("                |           |");
        System.out.println("                |  Registo  |");
        System.out.println("                |___________|");
        System.out.println("                             ");
        System.out.println("                             ");
        //====================================================================================
        //=============== NOME
        System.out.print("           Nome: ");
        String nome = sc.nextLine();

        if(nome.length() < 3){
            System.out.println("\n\n");
            System.out.println("    O nome deve possuir pelo menos 3 caracteres");
            menu_registo(rmi_comp);
        }
        //====================================================================================
        //============  Verificar Username
        //============  Nao pode haver usernames repetidos
        System.out.print("           Username: ");
        String username = sc.nextLine();

        if(username.length() < 4){
            System.out.println("\n\n");
            System.out.println("    O username deve possuir pelo menos 4 caracteres");
            menu_registo(rmi_comp);
        }
        boolean comp= true;
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }

        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            comp = h.verifica_username_registo(username);//para saber se username E valido

        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                comp = h.verifica_username_registo(username);

                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }
            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }

        if(comp == false){//username nao E valido
            System.out.println("\n\n");
            System.out.println("    Username ja existe!");
            System.out.println("    Tente outro username.");
            menu_registo(rmi_comp);
        }

        //========================================================================================
        //================== Verificar password

        System.out.print("           Password: ");
        String password = sc.nextLine();
        if(username.length() < 4){
            System.out.println("\n\n");
            System.out.println("    A password deve possuir pelo menos 4 caracteres");
            menu_registo(rmi_comp);
        }

        //==================================================================================
        //============== Guardar informacao
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }

        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            id = h.guarda_novo_utlizador(nome, username, password);
        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                id = h.guarda_novo_utlizador(nome, username, password);

                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }

            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }

        menu_utilizador(id, rmi_comp);
    }

    /**Cria uma thread que esta sempre a comunicar com o RMI Server que por sua vez comunica com a thread criada no Multicast
    * A thread recebe o id do utilizador e enquanto este estiver ativo vai estar a verificar as notificacoes
    * E possivel relizar pesquisas
    * Ver os dados do perfil
    * Alerar perfil
    */
    public static void menu_utilizador(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU USER -------------------------------------------
        //-------------------------------------------------------------------------------------------------

            //------- Menu Utilizador ---------
            //1-> Realizar Pesquisa
            //2-> Ver Perfil
            //3-> Sair da aplicaçao
            int menu=0;
            NewThread t;
            do{
                System.out.println("\n\n\n\n\n\n\n");
                System.out.println("            _______________________________________");
                System.out.println("            |           Menu Utilizador           |");
                System.out.println("            |                                     |");
                System.out.println("            |   1-> Realizar Pesquisa Termo       |");
                System.out.println("            |   2-> Ver Perfil                    |");
                System.out.println("            |   3-> Alterar Perfil                |");
                System.out.println("            |   4-> Realizar Pesquisa URL         |");
                System.out.println("            |   5-> Sair                          |");
                System.out.println("            |_____________________________________|");

                System.out.print("              Que opcao deseja realizar: ");


                t = new NewThread(id);
                String frase = sc.nextLine();
                try{
                    menu =Integer.parseInt(frase);

                }catch(NumberFormatException ne){
                    System.out.println("\n");
                    System.out.println("Introduza um numero!");
                    System.out.println("\n");
                    menu_utilizador(id, rmi_comp);
                }

                switch(menu){
                    case 1:{//Realiza Pesquisa
                        menu_pesquisa(id, rmi_comp);
                        break;
                    }
                    case 2:{//registo
                        menu_perfil_utilizador(id, rmi_comp);
                        break;
                    }
                    case 3:{//alterar perfil
                        menu_altera_perfil(id, rmi_comp);
                        break;
                    }
                    case 4:{
                      menu_pesquisa_url(id, rmi_comp);
                      break;
                    }
                }

            }while(menu !=5);
            t.saida_cliente();
            menu_inicial(rmi_comp);
    }

    /**Permite fazer pesquisas
    * Ver perfil
    * Alterar perfil
    * Ver informacao do sistema
    * Conceder privilegios
    * Indexar URL
    */
    public static void menu_administrador(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU USER -------------------------------------------
        //-------------------------------------------------------------------------------------------------

            //------- Menu Utilizador ---------
            //1-> Realizar Pesquisa
            //2-> Ver Perfil
            //3-> Sair da aplicaçao
            int menu=0;
            do{
                System.out.println("");
                System.out.println("            _______________________________________");
                System.out.println("            |         Menu Administrador          |");
                System.out.println("            |                                     |");
                System.out.println("            |   1-> Realizar Pesquisa Termo       |");
                System.out.println("            |   2-> Realizar Pesquisa URL         |");
                System.out.println("            |   3-> Ver Perfil                    |");
                System.out.println("            |   4-> Alterar Perfil                |");
                System.out.println("            |   5-> Indexar novo URL              |");
                System.out.println("            |   6-> Conceder Privilegios          |");
                System.out.println("            |   7-> Informacao Sistema            |");
                System.out.println("            |   8-> Sair                          |");
                System.out.println("            |_____________________________________|");


                System.out.print("\n              Que opcao deseja realizar: ");

                String frase = sc.nextLine();

                try{
                    menu =Integer.parseInt(frase);

                }catch(NumberFormatException ne){
                    System.out.println("\n");
                    System.out.println("Introduza um numero!");
                    System.out.println("\n");
                    menu_administrador(id, rmi_comp);
                }

                switch(menu){
                    case 1:{//Realiza Pesquisa
                        menu_pesquisa(id, rmi_comp);
                        break;
                    }
                    case 2:{
                      menu_pesquisa_url(id, rmi_comp);
                    }
                    case 3:{//registo
                        menu_perfil_utilizador(id, rmi_comp);
                        break;
                    }
                    case 4:{//alterar perfil
                        menu_altera_perfil(id, rmi_comp);
                        break;
                    }
                    case 5:{//indexar URL
                    	anexa_url(id, rmi_comp);



                        break;
                    }
                    case 6:{//Conceder Privilegios
                        menu_conceder_privilegios(id, rmi_comp);
                        break;
                    }
                    case 7:{//informacao sistema
                        menu_informacao_sistema(id, rmi_comp);
                        break;
                    }
                }

            }while(menu !=8);
            menu_inicial(rmi_comp);
    }

    public static void anexa_url(int id, boolean rmi_comp){
        int rmi1 = 0;
        int rmi2 = 0;

    	BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String ws = "";
        try{
        	System.out.println("Website: ");
        	ws = bf.readLine();
        }catch(IOException e){
        	e.printStackTrace();
        }
        //====================================================================
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }
        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            h.anexa_url(ws);

        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                h.anexa_url(ws);
                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }
            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }

        menu_administrador(id, rmi_comp);
    }

    /**Mostra a informacao do perfil de um User
    * Nome, Username, Password, Pesquisa
    */
    public static void menu_perfil_utilizador(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        int rmi1 = 0;
        int rmi2 = 0;

        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU USER -------------------------------------------
        //-------------------------------------------------------------------------------------------------

            //------- Menu Utilizador ---------
            //1-> Realizar Pesquisa
            //3-> Sair da aplicaçao
            int menu=0;
            //2-> Ver Perfil
            do{
                System.out.println("\n\n\n\n\n\n\n");
                System.out.println("            _________________________");
                System.out.println("            |                       |");
                System.out.println("            |   Perfil Utilizador   |");
                System.out.println("            |_______________________|");
                System.out.println("\n\n");

                //============================================================================================
                //============ Pedir ao servidor dados
                String nome = "";
                String username = "";
                String password = "";
                String pesquisa = "";
                if(rmi_comp == true){
                    rmi1 = 7000;
                    rmi2 = 6000;
                }
                else{
                    rmi1 = 6000;
                    rmi2 = 7000;
                }
                try {
                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");

                    String frase = h.pedir_nome_username_password_pesquisa(id);
                    String[] aux = frase.split(":");
                    nome = aux[0];
                    username = aux[1];
                    password = aux[2];
                    pesquisa = aux[3];

                } catch (Exception e) {
                    try{
                        Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");

                        String frase = h.pedir_nome_username_password_pesquisa(id);
                        String[] aux = frase.split(":");
                        nome = aux[0];
                        username = aux[1];
                        password = aux[2];
                        pesquisa = aux[3];

                        if(rmi_comp == true){
                            rmi_comp = false;
                        }
                        else{
                            rmi_comp = true;
                        }
                    } catch (Exception es) {
                        System.out.println("Exception in main: " + es);
                        e.printStackTrace();
                    }
                }

                System.out.println("            Nome: " + nome );
                System.out.println("            Username: " + username );
                System.out.println("            Password: " + password );
                System.out.println("            Pesquisas: " + pesquisa );

                System.out.println("\n\nPrima Enter para regressar ao Menu Utilizador");
                String aux = sc.nextLine();
                menu = 1;


            }while(menu ==0);
            //========= Verifica se é administrador
            boolean comp = true;
            if(rmi_comp == true){
                rmi1 = 7000;
                rmi2 = 6000;
            }
            else{
                rmi1 = 6000;
                rmi2 = 7000;
            }
            try {

                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                comp = h.verifica_administrador(id);

            } catch (Exception e) {
                try{
                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                    comp = h.verifica_administrador(id);

                    if(rmi_comp == true){
                        rmi_comp = false;
                    }
                    else{
                        rmi_comp = true;
                    }
                } catch (Exception es) {
                    System.out.println("Exception in main: " + es);
                    e.printStackTrace();
                }
            }
            //======= Envia para a pagina certa
            if(comp == true){
                menu_administrador(id, rmi_comp);
            }
            else{
                menu_utilizador(id, rmi_comp);
            }
    }

    /**Pede novos dados ao utilizador
    * Nome -> minimo de 3 letras
    * Username -> minimo 4 caracteres... pode manter o mesmo ou mudar para um ainda nao existente
    * Password -> minimo 4 caracteres
    */
    public static void menu_altera_perfil(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        int rmi1 = 0;
        int rmi2 = 0;

        //------- Registo ---------

        boolean menu = true;

        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("                _____________________");
        System.out.println("                |                   |");
        System.out.println("                |   Alterar Perfil  |");
        System.out.println("                |___________________|");
        System.out.println("                             ");
        System.out.println("                             ");
        System.out.print("           Nome: ");
        String nome = sc.nextLine();
        System.out.print("           Username: ");
        String username = sc.nextLine();
        //====================================================================================
        //============  Verificar Username
        //============  Nao pode haver usernames repetidos
        if(username.length() < 4){
            System.out.println("\n\n");
            System.out.println("    O username deve possuir pelo menos 4 caracteres");
            menu_altera_perfil(id, rmi_comp);
        }

        boolean comp = true;
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }
        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            comp = h.verifica_username_altera(username, id);//para saber se username E valido

        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                comp = h.verifica_username_altera(username, id);

                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }

            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }

        //====== Compara resultados
        //TRUE-> pode assumir este username
        //False-> volta ao inicio
        if(comp == false){
            System.out.println("    O username ja existe");
            menu_altera_perfil(id, rmi_comp);
        }

        //========================================================================================
        //================== Verificar password

        System.out.print("           Password: ");
        String password = sc.nextLine();
        if(username.length() < 4){
            System.out.println("\n\n");
            System.out.println("    A password deve possuir pelo menos 4 caracteres");
            menu_altera_perfil(id, rmi_comp);
        }

        //==================================================================================
        //Guardar informacao
        boolean admin = true;
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }
        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            admin = h.guarda_atualizacao_utlizador( id, nome, username, password);
        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                admin = h.guarda_atualizacao_utlizador(id, nome, username, password);

                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }

            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }


        //======= Volta ao menu
        if(admin == true){
            menu_administrador(id, rmi_comp);
        }
        else{
            menu_utilizador(id, rmi_comp);
        }
    }

    /**O administrador envia o username da pessoa a quem quer dar os pro«ivilegios
    */
    public static void menu_conceder_privilegios(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        boolean comp = true;
        int rmi1 = 0;
        int rmi2 = 0;

        //------- Registo ---------

        boolean menu = true;

        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("                ___________________________");
        System.out.println("                |                         |");
        System.out.println("                |   Conceder Privilegios  |");
        System.out.println("                |_________________________|");
        System.out.println("                             ");
        System.out.println("                             ");
        System.out.print("           Username da pessoa a que deseja dar privilegios: ");
        String username = sc.nextLine();

        //========================================================
        //======== Verifica username
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }
        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            comp = h.verifica_username_registo(username);//para saber se username E valido

        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                comp = h.verifica_username_registo(username);

                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }

            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }

        if(comp == true){//username nao existe
            System.out.println("Username nao existe!");
            menu_conceder_privilegios(id, rmi_comp);
        }
        //======================================================
        //==================== Concede privilegios
        if(rmi_comp == true){
            rmi1 = 7000;
            rmi2 = 6000;
        }
        else{
            rmi1 = 6000;
            rmi2 = 7000;
        }
        try {

            Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
            h.conceder_privilegios(username);//para saber se username E valido

        }catch (Exception e) {
            try{
                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                h.conceder_privilegios(username);

                if(rmi_comp == true){
                    rmi_comp = false;
                }
                else{
                    rmi_comp = true;
                }

            } catch (Exception es) {
                System.out.println("Exception in main: " + es);
                e.printStackTrace();
            }
        }
        menu_administrador(id, rmi_comp);
    }

    /**Um User pode escrever uma frase ou uma palavra
    * E dado os ressultados para todas as palavras dadas com 3 ou mais letras
    */
    public static void menu_pesquisa(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        int rmi1 = 0;
        int rmi2 = 0;

        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU PESQUISA ---------------------------------------
        //-------------------------------------------------------------------------------------------------

            int menu=0;
            //2-> Ver Perfil
            do{
                System.out.println("\n\n\n");
                System.out.println("            ____________________________");
                System.out.println("            |                          |");
                System.out.println("            |     Realizar Pesquisa    |");
                System.out.println("            |__________________________|");
                System.out.println("\n\n");
                System.out.println("            Pesquisa: ");

                String pesquisa = sc.nextLine();
                String[] termos = pesquisa.split("\\s+");
                String resp = "";
                if(rmi_comp == true){
                    rmi1 = 7000;
                    rmi2 = 6000;
                }
                else{
                    rmi1 = 6000;
                    rmi2 = 7000;
                }
                try {

                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                    resp =  h.faz_pesquisa(id, pesquisa);

                }catch (Exception e) {
                    try{
                        Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                        resp = h.faz_pesquisa(id, pesquisa);

                        if(rmi_comp == true){
                            rmi_comp = false;
                        }
                        else{
                            rmi_comp = true;
                        }

                    } catch (Exception es) {
                        System.out.println("Exception in main: " + es);
                        e.printStackTrace();
                    }
                }


                System.out.print("Resultados: ");
                System.out.println(resp);


                System.out.println("\n\n\tPrima Enter para regressar ao Menu Utilizador");
                String aux1 = sc.nextLine();
                menu = 1;


            }while(menu ==0);

            //========= Verifica se é administrador
        if(id == 0){
            menu_inicial(rmi_comp);
        }
            boolean comp = true;
            if(rmi_comp == true){
                rmi1 = 7000;
                rmi2 = 6000;
            }
            else{
                rmi1 = 6000;
                rmi2 = 7000;
            }
            try {

                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                comp = h.verifica_administrador(id);

            } catch (Exception e) {
                try{
                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                    comp = h.verifica_administrador(id);

                    if(rmi_comp == true){
                        rmi_comp = false;
                    }
                    else{
                        rmi_comp = true;
                    }

                } catch (Exception es) {
                    System.out.println("Exception in main: " + es);
                    e.printStackTrace();
                }
            }
            //======= Envia para a pagina certa
            if(comp == true){
                menu_administrador(id, rmi_comp);
            }
            else{
                menu_utilizador(id, rmi_comp);
            }
    }



    /**Um User  pode escrever um URL e recebe os links que vão dar a esse URL
    */
    public static void menu_pesquisa_url(int id, boolean rmi_comp){
        Scanner sc= new Scanner(System.in);
        int rmi1 = 0;
        int rmi2 = 0;

        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU PESQUISA ---------------------------------------
        //-------------------------------------------------------------------------------------------------

            int menu=0;
            //2-> Ver Perfil
            do{
                System.out.println("\n\n\n");
                System.out.println("            ____________________________");
                System.out.println("            |                          |");
                System.out.println("            |  Realizar Pesquisa URL   |");
                System.out.println("            |__________________________|");
                System.out.println("\n\n");
                System.out.println("            URL: ");

                String pesquisa = sc.nextLine();
                String[] termos = pesquisa.split("\\s+");
                String resp = "";
                if(rmi_comp == true){
                    rmi1 = 7000;
                    rmi2 = 6000;
                }
                else{
                    rmi1 = 6000;
                    rmi2 = 7000;
                }
                try {

                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                    resp =  h.faz_pesquisa_url(id, pesquisa);

                }catch (Exception e) {
                    try{
                        Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                        resp = h.faz_pesquisa_url(id, pesquisa);

                        if(rmi_comp == true){
                            rmi_comp = false;
                        }
                        else{
                            rmi_comp = true;
                        }

                    } catch (Exception es) {
                        System.out.println("Exception in main: " + es);
                        e.printStackTrace();
                    }
                }


                System.out.print("Resultados: ");
                System.out.println(resp);


                System.out.println("\n\n\tPrima Enter para regressar ao Menu Utilizador");
                String aux1 = sc.nextLine();
                menu = 1;


            }while(menu ==0);

            //========= Verifica se é administrador
        if(id == 0){
            menu_inicial(rmi_comp);
        }
            boolean comp = true;
            if(rmi_comp == true){
                rmi1 = 7000;
                rmi2 = 6000;
            }
            else{
                rmi1 = 6000;
                rmi2 = 7000;
            }
            try {

                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                comp = h.verifica_administrador(id);

            } catch (Exception e) {
                try{
                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                    comp = h.verifica_administrador(id);

                    if(rmi_comp == true){
                        rmi_comp = false;
                    }
                    else{
                        rmi_comp = true;
                    }

                } catch (Exception es) {
                    System.out.println("Exception in main: " + es);
                    e.printStackTrace();
                }
            }
            //======= Envia para a pagina certa
            if(comp == true){
                menu_administrador(id, rmi_comp);
            }
            else{
                menu_utilizador(id, rmi_comp);
            }
    }
    /**Informacao do sistema
    */
    public static void menu_informacao_sistema(int id, boolean rmi_comp) {
        Scanner sc = new Scanner(System.in);
        int rmi1 = 0;
        int rmi2 = 0;

        //-------------------------------------------------------------------------------------------------
        //------------------------------------------- MENU INFORMACAO -------------------------------------
        //-------------------------------------------------------------------------------------------------

        int menu = 0;
        do {
            System.out.println("\n\n\n\n\n\n\n");
            System.out.println("            ____________________________");
            System.out.println("            |                          |");
            System.out.println("            |    Informacao Sistema    |");
            System.out.println("            |__________________________|");
            System.out.println("\n\n");





            if (rmi_comp == true) {
                rmi1 = 7000;
                rmi2 = 6000;
            } else {
                rmi1 = 6000;
                rmi2 = 7000;
            }
            try {

                Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi1).lookup("projeto");
                String resp = "";

                resp = h.system_info();

                System.out.println(resp);

            } catch (Exception e) {
                try {
                    Rmi h = (Rmi) LocateRegistry.getRegistry(IP_GABRIEL, rmi2).lookup("projeto");
                    String resp = "";
                    resp = h.system_info();
                    System.out.println(resp);
                    if (rmi_comp == true) {
                        rmi_comp = false;
                    } else {
                        rmi_comp = true;
                    }
                } catch (Exception es) {
                    System.out.println("Exception in main: " + es);
                    e.printStackTrace();
                }
            }
            System.out.println("\n\n\tPrima Enter para regressar ao Menu Utilizador");
            String aux1 = sc.nextLine();
            menu = 1;

        } while (menu == 0);
        menu_administrador(id, rmi_comp);
    }}
