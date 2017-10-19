/**
 * Created by longarai on 19/10/17.
 */
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
                        System.out.println(nome+":> mensagem "+m.info() +" OK");
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