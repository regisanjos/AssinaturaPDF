
package com.senai.sd.criptoass.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncriptaPDF extends Thread {
    
    private byte[] chaveSecreta;
    private byte[] arqPDF;
    private byte[] arqPDFenc;


    
    public EncriptaPDF(byte[] arqPDF,byte[] chaveSecreta){
        
        this.arqPDF = arqPDF;   
        this.chaveSecreta = chaveSecreta;
    }
    
    public synchronized void encriptaPDF(){
        
        System.out.println("Encriptando arquivo PDF... tamanho em bytes: " + arqPDF.length);
        Cipher cifra;
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);        

        try {
            // Inicializa o Cipher no modo de encriptação com AES/CBC/PKCS5Padding
            cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(chaveSecreta, "AES");
            cifra.init(Cipher.ENCRYPT_MODE, secretKey,ivSpec);
            byte[] arqPDFcript = cifra.doFinal(arqPDF);
            byte[] arqPDFiv = new byte[iv.length + arqPDFcript.length];
            System.arraycopy(iv,0,arqPDFiv,0,iv.length);
            System.arraycopy(arqPDFcript,0,arqPDFiv, iv.length,arqPDFcript.length);
            arqPDFenc = Base64.getEncoder().encode(arqPDFiv);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException  ex) {
            System.out.println("EncriptaPDF: Erro de encriptação simétrica, Verifique! " + ex.getMessage());
        }  catch (IllegalBlockSizeException | BadPaddingException ex) {
                System.out.println("Erro ao encriptar PDF, verifique...\n" + ex.getMessage());
        }
    }
    
    @Override
    public void run(){
        this.encriptaPDF();
    }
    public byte[] getArqPDFenc(){
        return this.arqPDFenc;
    }
    
}
