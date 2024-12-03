
package com.senai.sd.cripto;

import com.senai.sd.cripto.service.DecriptaAESService;
import com.senai.sd.cripto.service.DecriptaRSAService;
import com.senai.sd.cripto.service.EncriptaAESService;
import com.senai.sd.cripto.service.KeystoreService;
import java.io.IOException;
import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.NORM_PRIORITY;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;


public class CriptoInterfaceImpl extends UnicastRemoteObject implements CriptoInterface {
    
    private static CriptoInterfaceImpl instanciaCriptoInt = null;
    private SecretKey secretKey;
    private PrivateKey privateKey;
    private Certificate[] certificado;
    private KeystoreService keystoreService;
    private DecriptaRSAService decriptaRSAService;
    private DecriptaAESService decriptaAESService;
    private EncriptaAESService encriptaAESService;    
    
    
    private CriptoInterfaceImpl() throws RemoteException {
	    super();
            keystoreService = this.iniciaKeystore();
            this.certificado = keystoreService.getCertificado();
            this.privateKey = keystoreService.getPrivateKey();
            this.secretKey = keystoreService.getChaveSimetrica();            
            
    }

    @Override
    public SecretKey getChaveSimetrica() throws RemoteException {
        
        synchronized(this){
            return this.secretKey;
        }        
    }

    @Override
    public Certificate[] getCertificado() throws RemoteException {
        synchronized(this){
            return this.getCertificado();
        }        
    }
    
    @Override
    public byte[] decriptaRSA(byte[] dadosCript) throws RemoteException {

        synchronized(this){
            byte[] dadosDecript = null;
            decriptaRSAService = new DecriptaRSAService(dadosCript,dadosDecript,privateKey);
            decriptaRSAService.setPriority(MAX_PRIORITY);
            decriptaRSAService.setName("DecriptaRSA");
            decriptaRSAService.start();
            while(decriptaRSAService.isAlive()){
                decriptaRSAService.getState();
            }            
            return dadosDecript;            
        }
    }

    @Override
    public byte[] decriptaAES(byte[] dados) throws RemoteException {
        
        synchronized(this){ 
            byte[] dadosDecript = null;
            decriptaAESService = new DecriptaAESService(dados,dadosDecript,secretKey);
            decriptaAESService.setPriority(MAX_PRIORITY);
            decriptaAESService.setName("DecriptaAES");
            decriptaAESService.start();
            try{
                decriptaAESService.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro na Thread... " + ex.getLocalizedMessage());
            }
            dadosDecript = decriptaAESService.getDadosDecriptados();
                                
            return dadosDecript; 
        }
        
    }
     @Override
    public byte[] encriptaAES(byte[] dados) throws RemoteException {
        
        synchronized(this){
            byte[] dadosEncript = null;
            encriptaAESService = new EncriptaAESService(dados,dadosEncript,secretKey);
            encriptaAESService.setPriority(NORM_PRIORITY);
            encriptaAESService.setName("EncriptaAES");
            encriptaAESService.start();
            try {
                    encriptaAESService.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro na Thread... " + ex.getLocalizedMessage());
            }
            dadosEncript = encriptaAESService.getDadosEncriptados();
            
            return dadosEncript;
            }      
            
    }    
    
    public KeystoreService iniciaKeystore(){
        
        try {
            keystoreService = new KeystoreService();
            
        } catch (KeyStoreException ex) {
            Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keystoreService;
        
    }
    
     
    public synchronized static CriptoInterfaceImpl getinstance() {
        if(instanciaCriptoInt == null){
            try {
                instanciaCriptoInt = new CriptoInterfaceImpl();
                return instanciaCriptoInt;
            } catch (RemoteException ex) {
                Logger.getLogger(CriptoInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro grave: " + ex.getMessage());
                return null;
            }
        } else {
            return instanciaCriptoInt;
        }
    }

   
   
    
    
    
    
}
