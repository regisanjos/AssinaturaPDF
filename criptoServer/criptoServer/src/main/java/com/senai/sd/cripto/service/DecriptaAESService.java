
package com.senai.sd.cripto.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class DecriptaAESService extends Thread {
    
    private byte[] dadosCript;
    private byte[] dadosDecript;
    private SecretKey secretKey;
    private Base64Utils base64u = new Base64Utils();
    
    public DecriptaAESService(byte[] dadosCript,byte[] dadosDecript, SecretKey secretKey){
        this.dadosCript = dadosCript;
        this.dadosDecript = dadosDecript;
        this.secretKey = secretKey;        
    }
        
    /**
    * Método para decriptar dados com uma chave simétrica
    */
   public synchronized void decriptaAES(){
       dadosDecript = null;
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);
             //cifra.init(Cipher.DECRYPT_MODE, new SecretKeySpec (chaveSeg,"AES"),ivspec);
             cifra.init(Cipher.DECRYPT_MODE, secretKey,ivspec);
             dadosDecript = cifra.doFinal(dadosCript);             
         } 
         catch (Exception ex){
           System.out.println("DecriptaAESService: Erro na decriptação simétrica, Verifique! " + ex.getMessage());
         }
     }
     
     public byte[] getDadosDecriptados(){
         return this.dadosDecript;
     }
   
    @Override
    public void run(){
       this.decriptaAES();
   }
    
}
