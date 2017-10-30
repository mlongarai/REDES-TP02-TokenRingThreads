package TokenRing;

/**
 * Created by longarai on 29/10/17.
 */

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageController implements Runnable {

    private MessageQueue queue;
    /*Tabela de roteamento */
    private InetAddress IPAddress;
    private int port;
    private Semaphore WaitForMessage;
    private String nickname;
    private int time_token;
    private Boolean token;
    private Boolean receivedAck;

    private static final String ACK = "OK";
    private static final String TOKEN = "1234";
    private static final String MSG_DADOS = "2345";

    public MessageController(MessageQueue q, String ip_port, int t_token, Boolean t, String n) throws UnknownHostException {
        queue = q;
        String aux[] = ip_port.split(":");
        IPAddress = InetAddress.getByName(aux[0]);
        port = Integer.parseInt(aux[1]);
        time_token = t_token;
        token = t;
        nickname = n;
        WaitForMessage = new Semaphore(0);
        receivedAck = false;
    }

    /**
     * ReceiveMessage() Nesta função, vc deve decidir o que fazer com a mensagem
     * * recebida do vizinho da esquerda: Se for um token, é a sua chance de
     * enviar uma mensagem de sua fila (queue); Se for uma mensagem de dados e
     * se for para esta estação, apenas a exiba no console, senão, envie para
     * seu vizinho da direita; Se for um ACK e se for para você, sua mensagem
     * foi enviada com sucesso, passe o token para o vizinho da direita, senão,
     * repasse o ACK para o seu vizinho da direita.
     */
    public void ReceivedMessage(String msg) throws IOException {

        if (msg.trim().equalsIgnoreCase(TOKEN)) {
            //System.out.println("\n Token Recebido: " + msg);
            token = true;
            receivedAck = false;
        }


        if (msg.contains(ACK)) {
            //Posição 0 = Identificador de ACK
            //Posição 1 = Apelido Destino
            String[] camposDaMensagem = msg.split(";");

            // a aplicação deve verificar se esse ACK é para ela (olhando o apelido que veio no ACK).
            if (itsForMe(camposDaMensagem[1])) {
                System.out.println(MSG_DADOS + ";" + "OK" + ":" + nickname);
                System.out.println("Mensagem recebida pela estacao!");
                //Caso o ACK seja para ela, um token deve ser enviado para seu vizinho da direita.
                receivedAck = true;
            } else {
                System.out.println("\n Encaminhando mensagem para proxima estacao");
                //Caso não seja, esta mensagem deve ser enviada para seu vizinho da direita
                queue.addNetWorkMessage(msg);
            }
        }


        if (msg.contains(MSG_DADOS)) {
            //Posição 0 = Identificador de msg
            //Posição 1 = Apelido Origem
            //Posição 2 = Apelido Destino
            //Posição 3 = Mensagem
            String[] camposDaMensagem = msg.split(":");

            if (itsForMe(camposDaMensagem[2])) {
                System.out.println(camposDaMensagem[0] + ":" +  camposDaMensagem[1] + ":" +camposDaMensagem[2] + ": " + camposDaMensagem[3]);

                String ackMessage = buildAckMessage(camposDaMensagem[1]);

                System.out.println(ackMessage);
                queue.addNetWorkMessage(ackMessage);
            } else {
                System.out.println("Enviando mensagem para a estacao a direita...");
                queue.addNetWorkMessage(msg);
            }
        }
        /* Libera a thread para execução. */
        WaitForMessage.release();
    }

    @Override
    public void run() {

        DatagramSocket clientSocket = createClientSocket();
        byte[] sendData = null;

        while (true) {

            try {
                Thread.sleep(time_token * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (token) {
                if (!queue.isLocalQueueEmpty()) {
                    try {
                        int retry = 0;
                        String msg = queue.removeMessageLocal();
                        sendData = getMessageBytes(msg);

                        System.out.println(msg);

                        sendPackage(clientSocket, buildDatagramPacket(sendData));

                        Thread.sleep(3000);
                        while (receivedAck == false) {
                            if (retry < 3) {
                                try {
                                    System.out.println("\n Enviando novamente...");
                                    clientSocket.send(buildDatagramPacket(sendData));

                                    retry++;

                                    System.out.println("\n Numero de tentativas: " + retry);
                                    Thread.sleep(3000);
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                sendTokenMsg(clientSocket);
                            }
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    sendTokenMsg(clientSocket);
                }

                if (receivedAck) {
                    sendTokenMsg(clientSocket);
                }

            } else {
                if (!queue.isNetWorkQueueEmpty()) {
                    sendData = getMessageBytes(queue.removeNetWorkMessage());
                    sendPackage(clientSocket, buildDatagramPacket(sendData));
                }
            }
        }
    }

    private boolean itsForMe(String apelidoNaMsg) {
        return apelidoNaMsg.trim().equals(nickname);
    }

    private String buildAckMessage(String apelido) {
        return MSG_DADOS + ";" + ACK + ":" + apelido;
    }

    private void sendPackage(DatagramSocket clientSocket, DatagramPacket sendPacket) {
        try {
            /* Realiza envio da mensagem. */
            clientSocket.send(sendPacket);

            /* A estação fica aguardando a ação gerada pela função ReceivedMessage(). */
            try {
                WaitForMessage.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DatagramPacket buildDatagramPacket(byte[] sendData) {
        return new DatagramPacket(sendData, sendData.length, IPAddress, port);
    }

    private byte[] getMessageBytes(String msg) {
        return msg.getBytes();
    }

    private void sendTokenMsg(DatagramSocket clientSocket) {
        token = false;
        sendPackage(clientSocket, buildDatagramPacket(getMessageBytes(TOKEN)));
    }

    private DatagramSocket createClientSocket() {
        /* Cria socket para envio de mensagem */
        try {
            return new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
