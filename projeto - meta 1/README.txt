para correr o programa é necessario compilar e executar os seguintes ficheiros presentes na pasta "codigo":
 Multicast.java , RMIServer.java , RMIServer_Aux , RMIClient;


Compilar e executar Multicast.java:
	javac -classpath jsoup-1.12.1.jar;. MulticastServer.java
	java -classpath jsoup-1.12.1.jar;. MulticastServer

*Apos executar o Multicast.java o programa ira demorar alguns segundos a estar pronto a receber informacao do cliente, pois esta a anexar os URLs
*para o programa funcionar normalmente pedimos que seja feita uma espera para executar os ficheiros abaixo indicados

 
Compilar e executar o resto dos ficheiros:
	javac RMIServer.java
	java RMIServer.java

	javac RMIServer_Aux.java
	java RMIServer_Aux

	javac RMIClient.java
	java RMIClient

*Pode haver mais que um RMIClient.java a executar ao mesmo tempo 
