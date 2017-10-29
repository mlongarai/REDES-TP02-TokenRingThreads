package TokenRing;

/**
 * Created by longarai on 29/10/17.
 */

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Esta classe deve implementar uma fila de mensagens. Observe que esta fila será
 * acessada por um consumidor (MessageSender) e um produtor (Classe principal, TokenRing).
 * Portanto, implemente controle de acesso (sincronização), para acesso a fila.
 */
public class MessageQueue {

    /*Implemente uma estrutura de dados para manter uma lista de mensagens em formato string.
     * Você pode, por exemplo, usar um ArrayList().
     * Não se esqueça que em uma fila, o primeiro elemente a entrar será o primeiro
     * a ser removido.
     */
    LinkedList<String> localQueue;
    LinkedList<String> netWorkQueue;

    Semaphore mutex = new Semaphore(1);
    Semaphore mutex2 = new Semaphore(1);

    public MessageQueue() {
        this.localQueue = new LinkedList<>();
        this.netWorkQueue = new LinkedList<>();
    }

    public void addLocalMessage(String message) {
        try {
            mutex.acquire();
            localQueue.addLast(message);
            mutex.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String removeMessageLocal() {
        try {
            mutex.acquire();
            String msg = localQueue.removeFirst();
            //System.out.println("Tamanho da fila Local: " + localQueue.size());
            mutex.release();
            return msg;
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public boolean isLocalQueueEmpty() {
        return localQueue.isEmpty();
    }

    public void addNetWorkMessage(String message) {
        try {
            mutex2.acquire();
            netWorkQueue.addLast(message);
            mutex2.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String removeNetWorkMessage() {
        try {
            mutex2.acquire();
            String msg = netWorkQueue.removeFirst();
            mutex2.release();
            return msg;
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public boolean isNetWorkQueueEmpty() {
        return netWorkQueue.isEmpty();
    }
}
