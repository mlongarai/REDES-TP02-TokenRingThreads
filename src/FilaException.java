/**
 * Created by longarai on 19/10/17.
 */
class FilaException extends Exception {
    // a excecao que pode ser gerada se a fila de mensagens da estacao esta
    // cheia ou vazia
    String razao;
    public FilaException(String r) { razao = r; }
}