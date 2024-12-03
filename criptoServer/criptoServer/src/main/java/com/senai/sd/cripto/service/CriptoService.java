
package com.senai.sd.cripto.service;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CriptoService {


    public CriptoService(){
        
    }    
    
     
     /**
     Método que encripta dados, através de uma chave Pública
     recebida via RMI
     Após decriptado, retorna um array de bytes dos dados.
    * @param dados e chavePub
    * @return 
    */

     public synchronized byte[] encriptaRSA (byte[] dados, PublicKey chavePub){
        try {
        Cipher cifra = Cipher.getInstance("RSA");
        cifra.init(Cipher.ENCRYPT_MODE, chavePub);
        System.out.println("CriptoService: Tam bytes encriptados: " + dados.length);
        return cifra.doFinal(dados);
        }
        catch ( Exception e ) {
		System.out.println("Classe CriptoService - Erro na encriptação..." + e.getMessage());
                return null; //"Erro decriptação!".getBytes();
        }
       }
     /**
     * Método para encriptar dados com uma chave simétrica.
     * @param textoP e chaveS
     * @param chaveS
     * @return 
     */   
    public synchronized byte[] encriptaAES(byte[] textoP, byte[] chaveS){
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);
             cifra.init(Cipher.ENCRYPT_MODE, new SecretKeySpec (chaveS,"AES"),ivspec);
             return cifra.doFinal(textoP);
         } 
         catch (Exception ex){
           System.out.println("Erro de encriptação simétrica, Verifique! " + ex.getMessage());
              return null;
         }
     }
   

        
    
    
}
