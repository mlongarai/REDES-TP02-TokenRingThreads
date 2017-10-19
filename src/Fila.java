/**
 * Created by longarai on 19/10/17.
 */
class Fila {

    msg [] buffer;
    int fim;
    int tamMax;

    public Fila(int tam) {
        buffer = new msg[tam];
        fim = 0;
        tamMax = tam;
    }

    public synchronized boolean temAlgo() {
        return fim>0;
    }

    public synchronized void
    enfilera(String destino, String conteudo) throws FilaException {

        if (fim<tamMax)
        {
            buffer[fim]=new msg("",destino,conteudo);
            fim++;
        }
        else throw new FilaException("Fila Cheia");
    }

    public synchronized msg
    desenfilera() throws FilaException {

        if (fim>0)
        {
            msg m = buffer[0];
            for(int i=0;i<fim;i++)
                buffer[i]=buffer[i+1];
            fim--;
            return m;
        }
        else throw new FilaException("Fila vazia");
    }

}  // fim Fila