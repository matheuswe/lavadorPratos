package br.edu.utfpr.td.tsi;

import br.edu.utfpr.td.tsi.lavador.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {

    static {
        Path caminhoAtual = Paths.get("");
        String pastaAtual = caminhoAtual.toAbsolutePath().toString();
        String arquivoLog = pastaAtual + "/logging.properties";
        try {
            InputStream is = new FileInputStream(arquivoLog);
            LogManager.getLogManager().readConfiguration(is);
            is.close();
        } catch (SecurityException | IOException e) {
            Logger.getLogger(App.class.getSimpleName())
                    .warning("Não foi possível abrir o " + arquivoLog);
        }
    }

    private static Logger logger = Logger.getLogger(App.class.getSimpleName());

    //Escolher a quantidade de lavadores e enxugadores
    private static final int QUANTIDADE_LAVADORES_PADRAO = 1;
    private static final int QUANTIDADE_ENXUGADORES_PADRAO = 1;
    private static final int CAPACIDADE_ESCORREDOR = 5;
    //1 minuto = 60000
    private static final long TEMPO_DE_TRABALHO = 20000;

    private final int quantidadeLavadores;
    private final int quantidadeEnxugadores;
    private final Escorredor escorredor;

    private final List<Lavador> lavadores = new ArrayList<>();
    private final List<Enxugador> enxugadores = new ArrayList<>();
    private final List<Thread> threadsLavadores = new ArrayList<>();
    private final List<Thread> threadsEnxugadores = new ArrayList<>();

    public App(int quantidadeLavadores, int quantidadeEnxugadores, int capacidadeEscorredor) {
        this.quantidadeLavadores = quantidadeLavadores;
        this.quantidadeEnxugadores = quantidadeEnxugadores;
        this.escorredor = new Escorredor(capacidadeEscorredor);
    }

    public void iniciar() {
        logger.info("Iniciando os trabalhos: " + quantidadeLavadores + " lavadores, "
                + quantidadeEnxugadores + " enxugadores, escorredor com "
                + escorredor.getCapacidade() + " lugares");

        for (int i = 1; i <= quantidadeLavadores; i++) {
            Lavador lavador = new Lavador(escorredor);
            Thread thread = new Thread(lavador);
            thread.setName("Lavador_" + i);
            lavadores.add(lavador);
            threadsLavadores.add(thread);
        }

        for (int i = 1; i <= quantidadeEnxugadores; i++) {
            Enxugador enxugador = new Enxugador(escorredor);
            Thread thread = new Thread(enxugador);
            thread.setName("Enxugador_" + i);
            enxugadores.add(enxugador);
            threadsEnxugadores.add(thread);
        }

        for (Thread thread : threadsLavadores) {
            thread.start();
        }
        for (Thread thread : threadsEnxugadores) {
            thread.start();
        }
    }

    public void parar() throws InterruptedException {
        logger.info("Pedindo para os trabalhadores pararem");

        for (Lavador lavador : lavadores) {
            lavador.parar();
        }
        for (Thread thread : threadsLavadores) {
            thread.join();
        }
        logger.info("Todos os lavadores terminaram, pratos no escorredor: " + escorredor.getQuantidade());

        escorredor.encerrar();

        for (Thread thread : threadsEnxugadores) {
            thread.join();
        }

        int totalLavados = 0;
        for (Lavador lavador : lavadores) {
            totalLavados = totalLavados + lavador.getPratosLavados();
        }
        int totalEnxugados = 0;
        for (Enxugador enxugador : enxugadores) {
            totalEnxugados = totalEnxugados + enxugador.getPratosEnxugados();
        }
        logger.info("Trabalhos encerrados, pratos lavados: " + totalLavados
                + ", pratos enxugados: " + totalEnxugados
                + ", pratos no escorredor: " + escorredor.getQuantidade());
    }

    public static void main(String[] args) throws InterruptedException {
        int quantidadeLavadores = QUANTIDADE_LAVADORES_PADRAO;
        int quantidadeEnxugadores = QUANTIDADE_ENXUGADORES_PADRAO;

        if (quantidadeLavadores < 1 || quantidadeEnxugadores < 1) {
            logger.severe("É preciso pelo menos 1 lavador e 1 enxugador");
            return;
        }

        App app = new App(quantidadeLavadores, quantidadeEnxugadores, CAPACIDADE_ESCORREDOR);
        app.iniciar();

        Thread.sleep(TEMPO_DE_TRABALHO);

        app.parar();
    }
}