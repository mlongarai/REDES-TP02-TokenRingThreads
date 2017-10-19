/**
 * Created by longarai on 19/10/17.
 */
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