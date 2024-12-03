

package com.senai.sd.clienteasspdf;

import com.senai.sd.criptoass.AssinadorInterface;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;


public class ClienteAssinaPDF {
    
    private static final ArrayList<String> servidores = new ArrayList();
    private static String endIpSrv;
    private static int portaSrv;
    public static Random random = new Random();
    

    public static void main(String[] args) {
        //Array com endereços de porta dos servidores de Assinatura
        servidores.add("1098:189.8.205.53");
        servidores.add("1098:189.8.205.53");
        servidores.add("1098:189.8.205.53");
        servidores.add("1098:189.8.205.53");
        //Sorteia indice para escolha de servidor aleatório para Assinatura
        int idx = random.nextInt(servidores.size()-1);
        portaSrv = Integer.parseInt(servidores.get(idx).substring(0,4));
        endIpSrv = servidores.get(idx).substring(5);
        System.out.println("Acessando o servidor de Assinatura[" + idx + "] em: " + endIpSrv + ":" + portaSrv);
        //Atributo chaveSecreta usado para construir Chaves Simétricas com AES
        byte[] palavraSecreta = "BebaCocaCola2024".getBytes(); 
        
        /*
        Criação dos Objetos da Interface CriptoServer para uso então
        pela classe para Assinar PDF
        */        
        try {
                AssinadorInterface assinadorInterface = ( AssinadorInterface ) Naming.lookup( "rmi://" + endIpSrv + ":" + portaSrv + "/Assinador_Server" );
                String servicos[] = Naming.list("rmi://" + endIpSrv + ":" + portaSrv);
                System.out.println("Lista de serviços disponíveis:");
            for (String servico : servicos) {
                System.out.println(servico);
            }
                //Utiliza Interface gráfica do Swing para escolha do arquivo PDF a ser assinado
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Selecione um arquivo PDF");
        
                // Limita a seleção a arquivos PDF
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos PDF", "pdf"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    
                   // Obtém o arquivo PDF selecionado
                   File arqPDF = fileChooser.getSelectedFile();
                   String nomePDF = arqPDF.getName();
                   String caminhoPDF = arqPDF.getParent();
                   System.out.println("Arquivo escolhido: " + caminhoPDF + "/" + nomePDF);
                   
                   // Lê o arquivo e converte para um array de bytes
                   FileInputStream fis = new FileInputStream(arqPDF);
                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                   byte[] buffer = new byte[1024];
                   int bytesRead;
                   while ((bytesRead = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }                                      
                   byte[] dataPDF = baos.toByteArray();
                   
                   //Encripta o arquivo PDF agora em formato de Array de Bytes
                   byte[] arqPDFcripto = encriptaAES(dataPDF,palavraSecreta); 
                   
                   //Envia o arquivo ArqPDFCritp encriptado em AES para método Remoto assinarPDF
                   //e já recebe retorno com o PDF já assinado
                   byte[] arqPDFass = assinadorInterface.assinarPDF(arqPDFcripto);                   
                   System.out.println("PDF foi assinado! Tamanho cripto: " + arqPDFass.length);
                   
                   //Após assinado, decripta o arquivo PDF arqPDFass e obtém o arquivo PDF assinado
                   byte[] arqPDFok = decriptaAES(arqPDFass,palavraSecreta);                   
                   
                   //Pega o arquivo PDF arqPDFok e grava no mesmo diretório do arquivo original
                   //agora com o nome do arquivo mais "_ass" para indicar que o arquivo está assinado.
                   String nomePDFass = nomePDF.substring(0,nomePDF.indexOf("."));                   
                   FileOutputStream fos = new FileOutputStream(caminhoPDF + "/" + nomePDFass + "_ass.pdf");
                   fos.write(arqPDFok);
                   System.out.println("\nArquivo PDF Assinado gravado em: " + caminhoPDF + "/" + nomePDFass);
                   System.exit(0);
                   
                } else {
                    System.out.println("Nenhum arquivo selecionado, saindo...");
                    System.exit(1);
                }
	}
            catch( MalformedURLException | RemoteException | NotBoundException  e ) {
                System.out.println( "URL RMI incorreta ou servidor fora do Ar, verifique...\n" + e.toString() );
          } catch (FileNotFoundException ex) {
                System.out.println("Arquivo não encontrado....\n" + ex.toString());
          } catch (IOException ex) {
                System.out.println("Erro de leitura/gravação do arquivo... \n" + ex.toString());
        }
        
    }
    /*
    Médodo para encriptação com algorítimo Simétrico AES, utilizando um chave secreta
    previamente definida em formato de byte array.
    */
     
    public static byte[] encriptaAES(byte[] arqPDF,byte[] palavraSecreta) {
    
        System.out.println("Encriptando arquivo PDF... tamanho em bytes: " + arqPDF.length);
        Cipher cifra;
        random = new SecureRandom();
        
        //Cria um vetor de inicialização de 16 bytes, aleatórios, para uso na criação da chave AES
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        try {
            // Inicializa o Cipher no modo de encriptação com AES/CBC/PKCS5Padding
            cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            //Cria a chave secreta AES usando a variável palavraSecreta
            SecretKeySpec secretKey = new SecretKeySpec(palavraSecreta, "AES");
            
            //Inicia a cifra no modo Encrypt usando a chave secreta criada e o vetor ivSpec
            cifra.init(Cipher.ENCRYPT_MODE, secretKey,ivSpec);
            
            //Realiza a encriptação e armazena o resultado em arqPDFcript
            byte[] arqPDFcript = cifra.doFinal(arqPDF);
            
            //Cria um array de bytes do tamanho do objeto iv + o tamanho do arquivo encriptado
            byte[] arqPDFiv = new byte[iv.length + arqPDFcript.length];
            
            //Copia o vetor iv para o ínicio do vetor de bytes arqPDFiv e
            //copia o vetor arqPDFcript para o vetor de bytes arqPDFiv logo após o vetor iv
            //Esses dois arraycopy copiam o vetor iv + arqPDFcrip para o vetor de bytes arqPDFiv
            System.arraycopy(iv,0,arqPDFiv,0,iv.length);
            System.arraycopy(arqPDFcript,0,arqPDFiv, iv.length,arqPDFcript.length);
            
            //Agora retornam um array de bytes criptografados e codificados em Base64
            return Base64.getEncoder().encode(arqPDFiv);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException  ex) {
            System.out.println("EncriptaPDF: Erro de encriptação simétrica, Verifique! " + ex.getMessage());
        }  catch (IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Erro no tamanho de bloco e/ou Padding... " + ex.toString());
        }
        return null;
    }    
    
    public static byte[] decriptaAES(byte[] arqPDFb64, byte[] palavraSecreta){
        
       //Decodifica o vetor de bytes encriptado, pois ele vem codificado em Base64
        byte[] arqPDFenc = Base64.getDecoder().decode(arqPDFb64);
        System.out.println("DEcriptando arquivo PDF... tamanho em bytes: " + arqPDFenc.length);

        //Cria um vetor de bytes de tamanho 16 para armazenar o vetor de inicialização que está contido
        //no início do vetor de bytes criptografados - veja o processo de encriptação anterior...
        byte[] iv = new byte[16];
        
        //Cria um vetor de bytes do tamanho do vetor de bytes encriptado menos 16 bytes que era o 
        //conteúdo do vetor de bytes iv.
        byte[] arqPDF = new byte[arqPDFenc.length - 16];
        
        //Recria o vetor de bytes iv a partir do vetor de bytes arqPDFenc
        System.arraycopy(arqPDFenc,0,iv,0,iv.length);
        //Recria o vetor de bytes encriptados do PDF, a partir do vetor de bytes arqPDF enc
        System.arraycopy(arqPDFenc, iv.length, arqPDF, 0, arqPDF.length);
        
        //Cria um novo ivSpec a partir do array de bytes iv, que foi recriado a partir dos dados criptografados.
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cifra;
        try {
            //Inicia a cifra do tipo AES
            cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            //Cria uma nova chave secreta, usando a palavra palavraSecreta como parâmetro.
            SecretKeySpec secretKey = new SecretKeySpec(palavraSecreta,"AES");
            
            //Inicia a cifra em modo de Decriptação, usando a nova chave secreta e o vetor ivSpec
            cifra.init(Cipher.DECRYPT_MODE,secretKey,ivSpec);
            
            //Retorna os dados decriptados originais, sem codificação e descriptografados.
            return cifra.doFinal(arqPDF);            
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException ex) {
            System.out.println("decriptaAES: Erro na decriptação: " + ex.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Erro no tamanho de bloco e/ou Padding... " + ex.toString());
        }        
        return null;
    }    
    
}
