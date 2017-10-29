/**
 * Created by longarai on 19/10/17.
 */

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
        System.out.println(s + conteudo);
    }
    public String info() { return  ( conteudo); }
}