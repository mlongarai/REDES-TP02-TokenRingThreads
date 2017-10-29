/**
 * Created by longarai on 19/10/17.
 */
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
            /*
            if (p instanceof msg)
                ((msg)p).info("     Monitora: ");
            //else System.out.println("     Monitora: token");
            */
            out.poe(p);
        }
    }
}