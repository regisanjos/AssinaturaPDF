
package com.senai.sd.cripto.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/*
Criar o Keystore em Java
keytool -genkey -alias assinador -keyalg RSA -keypass assina@ -storepass assina@ 
        -keystore ass_digital.jks -validity 3650
*/
public class KeystoreService {

    private static final String KEYSTORE_PATH = "src/config/ass_digital.jks";
    private static final String KEYSTORE_PASSWORD = "assina@";
    private static final String ALIAS = "asinador"; 
    private KeyStore keystore;
    private PrivateKey privateKey;
    private SecretKey secretKey;
    private Certificate[] certificado;
   
    public KeystoreService() throws KeyStoreException, FileNotFoundException, IOException,
                                    NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException{
        
        keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());
        privateKey = (PrivateKey) keystore.getKey(ALIAS, KEYSTORE_PASSWORD.toCharArray());
        certificado = keystore.getCertificateChain(ALIAS);
              
    }
    
    public PrivateKey getPrivateKey(){
        
        return this.privateKey;
    }
    public Certificate[] getCertificado(){
        return this.certificado;
    }
    
    
   
    
    /**
     * Método para geração de uma chave simétrica de sessão, a qual será usada nas
     * comunicações entre o cliente e o servidor.
     * Este método retorna uma chave simétrica com algoritmo AES de 128 bits.
     * @return
     * @throws NoSuchAlgorithmException 
     */   
    public SecretKey getChaveSimetrica() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            secretKey = keygen.generateKey();
            if (! (secretKey instanceof SecretKey) ){
                return null;
            } 
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeystoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return secretKey;
    }



    
    
    
}
