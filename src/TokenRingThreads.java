/**
 * Created by longarai on 19/10/17.
 */

public class TokenRingThreads {

    private final static String leitura = "parametros.txt";
    public static void main(String s[]) {

        String linhaArquivo;
        /*
        // Le parametros do arquivo
        ArrayList<String> parametros = new ArrayList<String>();

        // Le arquivo de entrada com lista de IPs dos roteadores vizinhos
        try (BufferedReader inputFile = new BufferedReader(new FileReader (leitura))) {
            System.out.println("Lendo arquivo: " + leitura);

            while ((linhaArquivo = inputFile.readLine()) != null) {
                // Adiciona no Arraylist o IP:PORTA
                // Adiciona no Arraylist o APELIDO
                // Adiciona no Arraylist o TEMPO do TOKEN
                parametros.add(linhaArquivo);
            }
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(TokenRingThreads.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException e) {
            e.printStackTrace ();
        }
        */

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
