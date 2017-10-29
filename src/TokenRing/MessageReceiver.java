package TokenRing;

/**
 * Created by longarai on 29/10/17.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Recebe mensagens do vizinho da esquerda e repassa para a classe MessageController.
 * Provavelmente você não precisará modificar esta classe.
 */

public class MessageReceiver implements Runnable{
    private final int port;
    private final MessageController controller;

    public MessageReceiver(int p, MessageController c){
        this.port = p;
        this.controller = c;
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;

        try {

            /* Inicializa o servidor para aguardar datagramas na porta especificada */
            serverSocket = new DatagramSocket(5000);
        } catch (SocketException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }


        while(true){
            byte[] receiveData = new byte[1024];

            /* Cria um DatagramPacket */
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                /* Aguarda o recebimento de uma mensagem. Esta thread ficará bloqueada neste ponto
                até receber uma mensagem. */
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }

            /* Converte o conteúdo do datagrama em string.
             * Lembre-se, isso apenas funciona porque sabemos que a mensagem recebida tem formato string.
             */
            String msg = new String( receivePacket.getData());

            try {
                /* Neste ponto você possui uma mensagem do seu vizinho da esquerda.
                * Passe a mensagem para a classe MessageController, ela deverá decidir
                * o que fazer.
                */
                controller.ReceivedMessage(msg);
            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
