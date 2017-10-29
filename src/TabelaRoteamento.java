/**
 * Created by longarai on 28/10/17.
 */
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;

public class TabelaRoteamento {
	/*
	 * Implemente uma estrutura de dados para manter a tabela de roteamento. A
	 * tabela deve possuir: IP Destino, Métrica e IP de Saída.
	 */

    private HashMap<String, Rota> tabelaRoteamento;
    private int tempo = 0;
    public static String ipLocal = "127.0.0.1";

    public TabelaRoteamento() {
        this.tabelaRoteamento = new HashMap<String, Rota>();
    }

    /**
     * Atualize a tabela de rotamento a partir da string recebida.
     *
     * @param tabela_s
     * @param sender
     */
    public void atualizaTabela(String tabelas, DatagramPacket dp) {
        String ip_saida = dp.getAddress().getHostAddress().trim();
        System.out.println("IP: " + ip_saida);
        System.out.println("Mensagem: " + tabelas);

        if (tabelas.equals("!")) {
            if (this.tabelaRoteamento.containsKey(ip_saida)) {
                this.tabelaRoteamento.get(ip_saida).atualizaValidade();
            } else {
                this.tabelaRoteamento.put(ip_saida, new Rota(ip_saida, 1, ip_saida));
            }

            System.out.println("Primeira Inserção");
        } else {
            // Adiciona saida caso nâo tem
            if (this.tabelaRoteamento.containsKey(ip_saida)) {
                this.tabelaRoteamento.get(ip_saida).atualizaValidade();
            } else {
                this.tabelaRoteamento.put(ip_saida, new Rota(ip_saida, 1, ip_saida));
            }

            // quebra mensagem
            for (String linha : tabelas.split("\\*")) {
                if (!linha.isEmpty() && linha != null) {
                    String linhaSplit[] = linha.split(";");
                    String ip_destino = linhaSplit[0];
                    Integer metrica = Integer.parseInt(linhaSplit[1]);
                    //Integer metricaDestino = (this.tabelaRoteamento.get(ip_saida) != null ? this.tabelaRoteamento.get(ip_saida).getMetrica() : 0);

                    if (!ip_destino.equalsIgnoreCase(ipLocal)) {
                        if (!this.tabelaRoteamento.containsKey(ip_destino)) {
                            this.tabelaRoteamento.put(ip_destino, new Rota(ip_destino, metrica + 1, ip_saida));
                        } else {
                            Rota rotaDestino = this.tabelaRoteamento.get(ip_destino);

                            // atualiza metricas
                            if (rotaDestino.getMetrica() > metrica - 1) {
                                rotaDestino.setIpSaida(ip_saida);
                                rotaDestino.setMetrica(metrica + 1);
                                rotaDestino.atualizaValidade();
                            }
                        }
                    }
                }
            }
        }

        this.tempo += 10;
        if (tempo == 30) {
            this.tempo = 0;
            if (this.limparTabela()) {
                System.out.println("ROTA REMOVIDA");
            }
        }

        System.out.println(get_table());
    }

    /**
     * Verifica se precisa remover algo
     * @return
     */
    public boolean limparTabela() {
        boolean removido = false;
        String rotaID;
        Rota rotaDados;

        synchronized (this.tabelaRoteamento) {
            for (HashMap.Entry<String, Rota> rota : this.tabelaRoteamento.entrySet()) {
                rotaID = rota.getKey();
                rotaDados = rota.getValue();

                if (rotaDados.getStatus().equalsIgnoreCase("Inativo")
                        || LocalDateTime.now().withNano(0).isBefore(rotaDados.getValidade()))
                    if (this.tabelaRoteamento.remove(rotaID) != null)
                        removido = true;

                rota.setValue(this.tabelaRoteamento.remove (rotaID));
            }
        }

        return removido;
    }

    /**
     * Tabela de roteamento vazia conforme especificado no protocolo Converta a
     * tabela de rotamento para string, conforme formato definido no protocolo
     *
     * @return
     */
    public String get_tabela_string() {
        if (this.tabelaRoteamento.isEmpty()) {
            return "!";
        }

        StringBuilder sb = new StringBuilder();
        for (Rota host : this.tabelaRoteamento.values()) {
            sb.append("*" + host.getIpDestino() + ";" + host.getMetrica());
        }

        return sb.toString().trim();
    }


    public String get_table() {
        StringBuilder sb = new StringBuilder();
        String fort = "%-20s | %-7s | %-20s | %-7s | %-3s \n";

        sb.append("\n");
        sb.append(String.format(fort, "Origem", "Métrica", "Destino", "Status", "Validade"));
        sb.append("---------------------|---------|----------------------|---------|------------------ \n");

        for (Rota host : tabelaRoteamento.values())
            sb.append(String.format(fort, host.getIpDestino(), host.getMetrica(), host.getIpSaida(), host.getStatus(),
                    host.getValidade()));

        sb.append("---------------------|---------|----------------------|---------|------------------ \n");

        return sb.toString();
    }
    
}
