/**
 * Created by longarai on 28/10/17.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MessageReceiver implements Runnable {
    private static final String TabelaRoteamento = null;
    private TabelaRoteamento tabela;
    private DatagramSocket serverSocket;

    public MessageReceiver(TabelaRoteamento t) throws SocketException {
        this.tabela = t;
        // Inicializa o servidor para aguardar datagramas na porta 5000
        serverSocket = new DatagramSocket(5000);
    }

    @Override
    public void run() {
		/*
		 * try { procuraVizinhoOff } catch (SocketException ex) {
		 * Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE,
		 * null, ex); return; }
		 */

        byte[] recebeDados = new byte[1024];

        // Loop para verificar se foi recebido
        while (true) {
            try {
                // Cria um DatagramPacket
                DatagramPacket recebePacote = new DatagramPacket(recebeDados, recebeDados.length);

                // Aguarda o recebimento de uma mensagem
                serverSocket.receive(recebePacote);

                // Atualiza dados na tabela
                tabela.atualizaTabela(new String(recebePacote.getData()).trim(), recebePacote);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
