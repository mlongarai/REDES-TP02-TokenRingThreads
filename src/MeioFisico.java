/**
 * Created by longarai on 19/10/17.
 */
class MeioFisico {
    Pacote pac = null;

    public synchronized void poe(Pacote p) {
        try{
            if ( temPacote() ) this.wait();  // ou notify(); wait();
            pac = p;
            this.notify();
        }	 catch (InterruptedException ie) {
            System.out.println("\n" + "Monitorando pacotes na rede... ");
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
            System.out.println("\n" + "Não tem pacotes circulando na rede... ");
        }
        return p;
    }

    public synchronized boolean temPacote() {
        return (pac!=null);
    }
}