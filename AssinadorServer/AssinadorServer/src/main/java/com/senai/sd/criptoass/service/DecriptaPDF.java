
package com.senai.sd.criptoass.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class DecriptaPDF extends Thread {
    
    private byte[] chaveSecreta;
    private byte[] arqPDF;
    private byte[] arqPDFdec = null;
    
    public DecriptaPDF(byte[] arqPDF,byte[] chaveSecreta){

        this.arqPDF = arqPDF;
        this.chaveSecreta = chaveSecreta;        
        
    }
    
    public synchronized void decriptaPDF() {
        
        byte[] arqPDFenc = Base64.getDecoder().decode(arqPDF);  // Decodifica o Base64
        Cipher cifra;
        byte[] iv = new byte[16];
        byte[] arqPDFcript = new byte[arqPDFenc.length -16];
        System.arraycopy(arqPDFenc,0,iv,0,iv.length);
        System.arraycopy(arqPDFenc,iv.length,arqPDFcript,0,arqPDFcript.length);
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(chaveSecreta,"AES");
            cifra.init(Cipher.DECRYPT_MODE,secretKey,ivSpec);
            arqPDFdec = cifra.doFinal(arqPDFcript);
            //System.out.println("Arquivo PDF decriptado, tamanho: " + arqPDFdec.length);
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("decriptaPDF: Erro ao decriptar PDF: " + ex.getMessage());
        }        
}
    
    @Override
    public void run(){
        this.decriptaPDF();
    }
    
    public byte[] getArqPDFdec(){
        return this.arqPDFdec;
    }
    
}
