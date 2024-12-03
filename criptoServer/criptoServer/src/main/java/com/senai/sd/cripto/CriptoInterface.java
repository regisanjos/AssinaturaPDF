
package com.senai.sd.cripto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.cert.Certificate;
import javax.crypto.SecretKey;


public interface CriptoInterface extends Remote {
    
    public SecretKey getChaveSimetrica() throws RemoteException;
    public Certificate[] getCertificado() throws RemoteException;
    public byte[] decriptaAES(byte[] dados) throws RemoteException;
    public byte[] encriptaAES(byte[] dados) throws RemoteException;    
    public byte[] decriptaRSA(byte[] dados) throws RemoteException;
    
}
