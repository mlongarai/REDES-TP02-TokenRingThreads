/**
 * Created by longarai on 19/10/17.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TokenRingThreads {

    private final static String leitura = "parametros.txt";
    public static void main(String s[]) {

        String linhaArquivo;

        // Le parametros do arquivo
        // <IP, [PORTA, APELIDO, TIME]>
        HashMap<String, List<String>> parametros = new HashMap<> ();
        List<String> valores = new ArrayList<> ();
        String tempIP = null;
        String tempPORTA = null;

        // Le arquivo de entrada com lista de IPs dos roteadores vizinhos
        try (BufferedReader inputFile = new BufferedReader(new FileReader (leitura))) {
            System.out.println("Lendo arquivo: " + leitura + "\n");

            while ((linhaArquivo = inputFile.readLine()) != null) {

                // Só entra a cada IP:PORTA
             if (linhaArquivo.contains ( ":" )) {
                 valores = new ArrayList<> ();
                 tempIP = null;
                 tempPORTA = null;

                 String linhaSplit[] = linhaArquivo.split ( ":" );
                 tempIP = linhaSplit[ 0 ];
                 tempPORTA = linhaSplit[ 1 ].toString ();

                 valores.add (tempPORTA); // AQUI ADD A PORTA EM VALORES


                 continue;
             }

             // Adiciona o resto das linhas, menos a primeira, entao fica linha APELIDO e TIME
             valores.add ( linhaArquivo );


             // vincula o ip com seus valores ficando igual a isto <IP, [PORTA, APELIDO, TIME]>
             if (valores.size () == 3)
                 parametros.put ( tempIP, valores );
            }
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(TokenRingThreads.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException e) {
            e.printStackTrace ();
        }

        // debug
        //System.out.println (valores.size ());
        //System.out.print ( parametros );


        // criacao e encadeamento de threads representando estacoes
        MeioFisico inE1 = new MeioFisico();
        MeioFisico outE1 = new MeioFisico();

        Fila filaE1 = new Fila(3);
        //Runnable e1 = new Estacao(TabelaRoteamento.ipLocal,filaE1,inE1,outE1);
        Runnable e1 = new Estacao("Bob",filaE1,inE1,outE1);
        Thread te1 = new Thread(e1);

        MeioFisico outE2 = new MeioFisico();
        Fila filaE2 = new Fila(3);
        Runnable e2 = new Estacao("Alice",filaE2,outE1,outE2);
        Thread te2 = new Thread(e2);

        MeioFisico outE3 = new MeioFisico();
        Fila filaE3 = new Fila(3);
        Runnable e3 = new Estacao("Lucy",filaE3,outE2,outE3);
        Thread te3 = new Thread(e3);

        // fecha o anel com monitora
        Runnable mo = new Monitora(outE3, inE1);
        Thread tmo = new Thread(mo);

        te1.start();
        te2.start();
        te3.start();
        tmo.start();


        try{
            // coloca algumas mensagens na fila das estacoes

            //Origem Bob (E2)
            //filaE1.enfilera("Alice","Oi Bob!");
            //filaE1.enfilera("Lucy","Oi! Bob!");

            //Origem Alice (E2)
            filaE2.enfilera("Bob","Olá Mundo!");
            //filaE2.enfilera("Lucy","Ola Alice!");

            //Origem Lucy (E2)
            //filaE3.enfilera("Bob","Hello World!");
            //filaE3.enfilera("Alice","Oi! Mundo!");

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
