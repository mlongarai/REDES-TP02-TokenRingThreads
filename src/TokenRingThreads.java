import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by longarai on 19/10/17.
 */

class Pacote {}
// tudo que pode trafegar

class Token extends Pacote {}

class msg extends Pacote {
    public String origem;
    public String destino;
    public String conteudo;

    public msg (String o, String d, String cont) {
        origem=o;
        destino=d;
        conteudo=cont;
    }
    public void info(String s) {
        System.out.println(s+" ("+origem+" , "+destino+" , "+conteudo+" )");
    }
    public String info() { return  (" ( "+origem+" , "+destino+" , "+conteudo+" )" ); }
}

class FilaException extends Exception {
    // a excecao que pode ser gerada se a fila de mensagens da estacao esta
    // cheia ou vazia
    String razao;
    public FilaException(String r) { razao = r; }
}

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


class MeioFisico {
    Pacote pac = null;

    public synchronized void poe(Pacote p) {
        try{
            if ( temPacote() ) this.wait();  // ou notify(); wait();
            pac = p;
            this.notify();
        }	 catch (InterruptedException ie) {
            System.out.println("MeioFisico interrompida em wait de poe "+ie);
        }
    }

    public synchronized Pacote pega() {
        Pacote p = null;
        try{
            if (!temPacote()) this.wait();
            p = pac;
            pac =  null;
            this.notify();
        }	 catch (InterruptedException ie) {
            System.out.println("MeioFisico interrompida em wait de poe "+ie);
        }
        return p;
    }

    public synchronized boolean temPacote() {
        return (pac!=null);
    }
}

class Estacao implements Runnable {
    String nome;
    Fila fila;
    MeioFisico entrada;
    MeioFisico saida;

    public Estacao(String n, Fila f, MeioFisico e, MeioFisico s) {
        nome = n;
        fila = f;
        entrada = e;
        saida = s;
    }

    public void run () {
        Pacote p = null;

        while (true) {
            p = entrada.pega();    // espera ate pacote existir na entrada
            if (p instanceof msg)
            {
                // tratar mensagem
                msg m = (msg)p;
                if ( m.origem.equals(this.nome) )
                    System.out.println(nome+":> mensagem "+m.info()+" deu a volta");
                    // se a originadora e ela, retira mensagem do meio
                else {
                    if ( m.destino.equals(this.nome) )
                        System.out.println(nome+":> mensagem "+m.info() +" chegou");
                    saida.poe(p);
                    // se o destino e ela ou e´ para outra estacao, manda a mensagem adiante
                }
            }
            else {
                // e um token
                try{
                    msg m = fila.desenfilera();
                    m.origem = this.nome;
                    // passa a mensagem para a proxima
                    saida.poe(m);
                    // passa o token para a proxima
                    saida.poe(p);
                } catch (FilaException e) {
                    // fila vazia
                    saida.poe(p);
                }
            }

        } // while true
    } // run

} // Estacao


class Leitora implements Runnable	{
    // pode ser colocada no final de uma sequencia de estacoes
    // nao recoloca os dados no anel - ou seja, abre o anel

    MeioFisico meio;

    public Leitora(MeioFisico m) {
        meio = m;
    }

    public void run() {
        Pacote p;
        while (true) {
            p = meio.pega();
            if (p instanceof msg)
                ((msg)p).info("     Leitora tirou mensagem ");
            else System.out.println("     Leitora tirou token");
            // teste   System.out.println("Leitora: passou do wait do pega");

        }
    }
}


class Monitora implements Runnable	{
    // monitora o anel dizendo o que passou naquele ponto
    // recoloca os dados no anel como se nao existisse

    MeioFisico in, out;

    public Monitora(MeioFisico i, MeioFisico o) {
        in = i; out = o;
    }

    public void run() {
        Pacote p;
        while (true) {
            p = in.pega();

            if (p instanceof msg)
                ((msg)p).info("     Monitora: ");
            else System.out.println("     Monitora: token");

            out.poe(p);
        }
    }
}

public class TokenRingThreads {

    private final static String leitura = "parametros.txt";
    public static void main(String s[]) {

        String linhaArquivo;

        // Le parametros do arquivo
        ArrayList<String> parametros = new ArrayList<String>();

        // Le arquivo de entrada com lista de IPs dos roteadores vizinhos
        try (BufferedReader inputFile = new BufferedReader(new FileReader (leitura))) {
            System.out.println("Lendo arquivo: " + leitura);

            while ((linhaArquivo = inputFile.readLine()) != null) {
                // Adiciona no Arraylist o IP:PORTA
                parametros.add(linhaArquivo);
                // Adiciona no Arraylist o APELIDO
                parametros.add(linhaArquivo);
                // Adiciona no Arraylist o TEMPO do TOKEN
                parametros.add(linhaArquivo);
            }
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(TokenRingThreads.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException e) {
            e.printStackTrace ();
        }

        // criacao e encadeamento de threads representando estacoes
        MeioFisico inE1 = new MeioFisico();
        MeioFisico outE1 = new MeioFisico();

        Fila filaE1 = new Fila(3);
        Runnable e1 = new Estacao("Estacao1",filaE1,inE1,outE1);
        Thread te1 = new Thread(e1);

        MeioFisico outE2 = new MeioFisico();
        Fila filaE2 = new Fila(3);
        Runnable e2 = new Estacao("Estacao2",filaE2,outE1,outE2);
        Thread te2 = new Thread(e2);

        MeioFisico outE3 = new MeioFisico();
        Fila filaE3 = new Fila(3);
        Runnable e3 = new Estacao("Estacao3",filaE3,outE2,outE3);
        Thread te3 = new Thread(e3);

        // fecha o anel com monitora
        Runnable mo = new Monitora(outE3, inE1);
        Thread tmo = new Thread(mo);

        te1.start();
        te2.start();
        te3.start();
        tmo.start();


        try{
            // coloca algumas mensagens na fila de mesnagens das estacoes

            filaE1.enfilera("Estacao1","mensagem1");
            filaE1.enfilera("Estacao2","mensagem2");

            filaE2.enfilera("Estacao1","mensagem3");
            filaE2.enfilera("Estacao3","mensagem4");

            filaE3.enfilera("Estacao1","mensagem5");
            filaE3.enfilera("Estacao2","mensagem6");

        } catch (Exception e) {
            System.out.println("Excecao "+e);
        }

        // inicio da transmissao colocando token livre na rede
        outE3.poe(new Token());

        try{
            // main espera 10 segundos para o anel rodar
            // aqui a thread main poderia ficar em loop colocando
            // mensagens na fila de entrada das estacoes

            Thread.sleep(10000);
        } catch(InterruptedException ie){}

        // acaba as estacoes e a monitora
        te1.interrupt ();
        te2.interrupt ();
        te3.interrupt ();
        tmo.interrupt ();

    }
}
