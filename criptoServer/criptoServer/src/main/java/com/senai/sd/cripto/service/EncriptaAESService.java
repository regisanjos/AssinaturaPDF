
package com.senai.sd.cripto.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


public class EncriptaAESService extends Thread {
    
    private byte[] dados;
    private byte[] dadosEncript;
    private SecretKey secretKey;
    private Base64Utils base64u = new Base64Utils();
    
    public EncriptaAESService(byte[] dados,byte[] dadosEncript, SecretKey secretKey){
        this.dados = dados;
        this.dadosEncript = dadosEncript;
        this.secretKey = secretKey;        
    }
    
    
    /**
     * Método para encriptar dados com a chave Simétrica.
     */
    public synchronized void encriptaAES(){
        dadosEncript = null;
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);
             cifra.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
             //System.out.println("Dados originais bytes..." + Arrays.toString(dados));
             dadosEncript = cifra.doFinal(dados);
             System.out.println("Dados criptografados bytes... " + base64u.toBase64(dadosEncript));
         } 
         catch (Exception ex){
           System.out.println("EncriptaAESService: Erro de encriptação simétrica, Verifique! " + ex.getMessage());
         }
     }
    public byte[] getDadosEncriptados() {
        return this.dadosEncript;  // Retorna os dados encriptados
    }
    
    @Override
    public void run(){
        this.encriptaAES();
    }
    
    
    
    
    
}
