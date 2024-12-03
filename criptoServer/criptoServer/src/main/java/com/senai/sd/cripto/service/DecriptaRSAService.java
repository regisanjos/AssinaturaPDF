
package com.senai.sd.cripto.service;

import java.security.PrivateKey;
import javax.crypto.Cipher;


public class DecriptaRSAService extends Thread {
    
    private byte[] dadosCript;
    private byte[] dadosDecript;
    private PrivateKey privateKey;
    
    
    public DecriptaRSAService(byte[] dadosCript,byte[] dadosDecript, PrivateKey privateKey){
        this.dadosCript = dadosCript;
        this.dadosDecript = dadosDecript;
        this.privateKey = privateKey;
        
    }
    
    
    public synchronized void decriptaRSA (){
        dadosDecript = null;
        try {
            Cipher cifra = Cipher.getInstance("RSA");
            cifra.init(Cipher.DECRYPT_MODE, privateKey);
            dadosDecript = cifra.doFinal(dadosCript);
        } catch ( Exception e ) {
		System.out.println("DecriptaRSAService - Erro na decriptação..." + e.getMessage());
        }
    }
    
    @Override
    public void run(){
        
        this.decriptaRSA();
    }
    
    
    
}
