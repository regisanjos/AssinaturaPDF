
package com.senai.sd.criptoass;

import com.senai.sd.criptoass.service.DecriptaPDF;
import com.senai.sd.criptoass.service.EncriptaPDF;
import static java.lang.Thread.NORM_PRIORITY;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class AssinadorInterfaceImpl extends UnicastRemoteObject implements AssinadorInterface {
    
    public static AssinadorInterfaceImpl instanciaAssinadorInterfaceImpl = null;
    private final byte[] palavraSecreta = "BebaCocaCola2024".getBytes(); 
   
    
    private AssinadorInterfaceImpl() throws RemoteException {
        
    }

    @Override
    public byte[] assinarPDF(byte[] arquivoPDF) throws RemoteException {
        /*
        1- arquivoPDF está criptografado
        2- descriptar o arquivoPDF
        3- Assinar o arquivoPDF
        4- Encriptar o arquivoPDF
        5- Retonrar o arquivoPDF
        */
        
        byte[] arqPDFdec = null;       
        DecriptaPDF decriptaPDF = new DecriptaPDF(arquivoPDF, palavraSecreta);
        decriptaPDF.setPriority(NORM_PRIORITY);
        decriptaPDF.setName("DecriptaPDF");
        decriptaPDF.start();
        while(decriptaPDF.isAlive()){
            try {
                decriptaPDF.join();
            } catch (InterruptedException ex) {
                System.out.println("Thread DecriptaPDF interrompida, verifique...\n" + ex.getMessage());
            }
        }
        arqPDFdec = decriptaPDF.getArqPDFdec();
        System.out.println("Tamanho PDF decriptado..: " + arqPDFdec.length);
        
        byte[] arqPDFass = AssinadorServer.assinaPDF(arqPDFdec);
        
        EncriptaPDF encriptaPDF = new EncriptaPDF(arqPDFass,palavraSecreta);
        encriptaPDF.setPriority(NORM_PRIORITY);
        encriptaPDF.setName("EncriptaPDF");
        encriptaPDF.start();
        while(encriptaPDF.isAlive()){
            try {
                encriptaPDF.join();
            } catch (InterruptedException ex) {
                System.out.println("Thread EncriptaPDF interrompida, verifique...\n" + ex.getMessage());
            }
        }
        return encriptaPDF.getArqPDFenc();
    }

    
    public synchronized static AssinadorInterfaceImpl getInstance(){
        
        if(instanciaAssinadorInterfaceImpl == null){
            try {
                instanciaAssinadorInterfaceImpl = new AssinadorInterfaceImpl();
            } catch (RemoteException ex) {
                System.out.println("Erro ao instanciar a classe AssinadorInterfaceImpl. " + ex.getLocalizedMessage());            }
            return instanciaAssinadorInterfaceImpl;
        } else {
            return instanciaAssinadorInterfaceImpl;
        }
        
    }

    
}
