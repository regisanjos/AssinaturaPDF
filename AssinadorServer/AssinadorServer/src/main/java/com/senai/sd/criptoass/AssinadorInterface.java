
package com.senai.sd.criptoass;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface AssinadorInterface extends Remote {    
    
    public byte[] assinarPDF(byte[] arquivoPDF) throws RemoteException;
    
}
/*
Criar Jar (estando no diretório target/classes  do CriptoServer
jar --create --file criptoInterface.jar com/senai/sd/cripto/CriptoInterface.class

após copiar o arquivo criptoInterface.jar para o diretório src/lib desse projeto AssinadorServer
e adicionar a dependência no projeto pelo Netbeans, fazendo:
1-Clique na pasta Dependencies com o botão direito
2-Escolha Add Dependency
3- Preencha os campos:
    Campo Query coloque: com.senai.sd.CriptoInterface  
    E depois preencha os campos abaixo, nesta ordem:
    Type: jar
    Version: 1.0.0
    Artifact ID: CriptoInterface
    Group ID: CriptoInterface
    Scope: compile
    Classifier: 
    Depois clique em Add (só ativa o botão de os dados acima forma preenchidos..)
 4-Depolis veja que na pasta Dependency apareceu um arquivo CriptoInterface-1.0.0.jar, porém
   ele não existe, então clique neste arquivo com o botão direito e escolha
   "Manually install artifact"
 5- Depois navege até a pasta src/libs desse projeto e escolha o arquivo criptoInterface.jar que
    você já deve ter colocado lá, após fazer o processo inicial desta descrição.    
    
*/