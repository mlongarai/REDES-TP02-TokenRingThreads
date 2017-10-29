/**
 * Created by longarai on 28/10/17.
 */
import java.time.LocalDateTime;

public class Rota {
    private String ipDestino;
    private String ipSaida;
    private int metrica;
    private String status = null;
    private LocalDateTime validade = LocalDateTime.now().withNano(0);

    public Rota(String ipDestino, int metrica, String ipSaida) {
        this.ipDestino = ipDestino;
        this.ipSaida = ipSaida;
        this.metrica = metrica;
        this.status = "Ativo";
        this.atualizaValidade();
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public String getIpSaida() {
        return ipSaida;
    }

    public void setIpSaida(String ipSaida) {
        this.ipSaida = ipSaida;
    }

    public int getMetrica() {
        return metrica;
    }

    public void setMetrica(int metrica) {
        this.metrica = metrica;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getValidade() {
        return this.validade;
    }

    public LocalDateTime atualizaValidade() {
        return this.atualizaValidade(30);
    }

    public LocalDateTime atualizaValidade(int valor) {
        return this.validade = this.validade.plusSeconds(valor);
    }
}
