

package com.senai.sd.cripto;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class CriptoServer {
    
    private final String end_ip_serv = "localhost";
    private final Integer porta_srv = 1099;


    public CriptoServer() {
        try {
            CriptoInterface criptoInt = CriptoInterfaceImpl.getinstance();
            Registry registry = LocateRegistry.createRegistry(porta_srv);
            registry = LocateRegistry.getRegistry(end_ip_serv, porta_srv);
            registry.bind("Cripto_Server", criptoInt);
            System.out.println("CriptoServer - Servidor RMI iniciado na porta " + porta_srv + "\n"
                    + "Cripto Server no ar!\n");
        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e + "\n Servidor saindo..");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        CriptoServer criptoServer = new CriptoServer();
    }
        
}
