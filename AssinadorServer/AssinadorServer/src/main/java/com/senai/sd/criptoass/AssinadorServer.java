
package com.senai.sd.criptoass;

import com.senai.sd.cripto.CriptoInterface;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class AssinadorServer {
    
    private static String endIpSrv = "localhost";
    private static Integer portaSrv = 1098;
    private static String endCriptoSrv = "localhost";
    private static Integer portaCriptoSrv = 1099;
    private static  CriptoInterface criptoInt;
    
    private AssinadorServer(){       
       
    }

    public static void main(String[] args) {
        
        System.out.println(
            "\nForma de execução por parâmetros:\n"
          + " java -jar AssinadorServer-1.0.0.jar endIpCriptoServer portaCritoServer endIpAssinador portaAssinador\n"
          + " exemplo: java -jar AssinadorServer-1.0.0.jar 189.8.205.53 1099 10.3.60.54 1098 & (em Linux)\n"
          + "          java -jar AssinadorServer-1.0.0.jar 189.8.205.53 1099 10.3.60.54 1098   (em Windows)\n"
        );
        
        if(args != null){
            if(args.length == 2){
                 endCriptoSrv = args[0];
                 portaCriptoSrv = Integer.valueOf(args[1]);
            } else if(args.length == 4){
                 endCriptoSrv = args[0];
                 portaCriptoSrv = Integer.valueOf(args[1]);
                 endIpSrv = args[2];
                 portaSrv = Integer.valueOf(args[3]);
            }
        }
                
                
        
        
        
        AssinadorServer assServer = new AssinadorServer();        
        /*
        Primeiro método para criação dos Objetos da Classe CriptoServer para uso
        pelas demais classes e Interface da classe AssinadorServer
        */
        
        try {
                criptoInt = ( CriptoInterface ) Naming.lookup( "rmi://" + endCriptoSrv + ":1099/Cripto_Server" );
                String servicos[] = Naming.list("rmi://" + endCriptoSrv);
                System.out.println("Lista de serviços disponíveis:");
            for (String servico : servicos) {
                System.out.println(servico);
            }
               
		}
		catch( MalformedURLException e ) {
			System.out.println();
			System.out.println( "MalformedURLException: " + e.toString() );
		}
		catch( RemoteException e ) {
			System.out.println();
			System.out.println( "RemoteException: " + e.toString() );
		}
		catch( NotBoundException e ) {
			System.out.println();
			System.out.println( "NotBoundException: " + e.toString() );
		}
		catch( Exception e ) {
			System.out.println();
			System.out.println( "Exception: " + e.toString() );
                        e.printStackTrace();
		}
        assServer.ativaAssinadorInterface();   
	}
    
    public void ativaAssinadorInterface(){
        
         try {
            AssinadorInterface assinadorInterface = AssinadorInterfaceImpl.getInstance();
            Registry registry = LocateRegistry.createRegistry(portaSrv);
            registry = LocateRegistry.getRegistry(endIpSrv, portaSrv);
            registry.bind("Assinador_Server", assinadorInterface);
            System.out.println("AssinadorServer - Servidor RMI iniciado em " + endIpSrv + ":" + portaSrv + "\n"
                    + "Assinador Server no ar!\n");
        } catch (RemoteException | AlreadyBoundException ex) {
            System.out.println("AssinadorServer... " + ex.getLocalizedMessage());
        }        
    }
   
    /*
    Método assinaPDF que recebe um array de Bytes[] que representa o PDF para assinar e
    chama o método remoto assinaPDF que será executado no servidor de criptografia CriptoServer,
    retornando um array de Bytes[] do PDF assinado digitalmente!
    Esse método será utilizado pela classe AssinadorInterfaceImpl que implementa a interface remota
    para os clientes e se utiliza desse método remoto para assinar os PDFs recebidos dos clientes.
    */
    
    public static byte[] assinaPDF(byte[] arqPDF){
        try {
            return AssinadorServer.criptoInt.assinaPDF(arqPDF);
        } catch (RemoteException ex) {
            System.out.println("Erro ao acessar o servidor CriptoServer, verifique...\n" + ex.getMessage());
        }
        return null;
    }

}
