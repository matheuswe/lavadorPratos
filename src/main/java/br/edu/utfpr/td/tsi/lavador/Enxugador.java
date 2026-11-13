package br.edu.utfpr.td.tsi.lavador;

import java.util.Random;
import java.util.logging.Logger;

public class Enxugador implements Runnable {

    private static Logger logger = Logger.getLogger(Enxugador.class.getSimpleName());

    private static final long INTERVALO = 10000000L;
    private static final int MULTIPLO_MINIMO = 3;
    private static final int MULTIPLO_MAXIMO = 10;

    private final Escorredor escorredor;
    private final Random random;

    private volatile boolean done;

    private volatile long contador;

    private int pratosEnxugados;

    public Enxugador(Escorredor escorredor) {
        this.escorredor = escorredor;
        this.random = new Random();
        this.done = false;
        this.contador = 0;
        this.pratosEnxugados = 0;
    }

    @Override
    public void run() {
        String nomeThread = Thread.currentThread().getName();
        logger.info(nomeThread + " começou a trabalhar");

        while (!done) {
            Prato prato = escorredor.retirar();

            if (prato == null) {
                done = true;
            } else {
                logger.fine(nomeThread + " pegou " + prato + " para enxugar");
                enxugar();
                pratosEnxugados = pratosEnxugados + 1;
                logger.fine(nomeThread + " terminou de enxugar " + prato);
            }
        }

        logger.info(nomeThread + " terminou, pratos enxugados: " + pratosEnxugados);
    }

    private void enxugar() {
        int quantidadeDeOpcoes = MULTIPLO_MAXIMO - MULTIPLO_MINIMO + 1;
        int multiplo = MULTIPLO_MINIMO + random.nextInt(quantidadeDeOpcoes);
        long limite = multiplo * INTERVALO;

        contador = 0;
        for (long i = 0; i < limite; i++) {
            contador++;
        }
    }

    public int getPratosEnxugados() {
        return pratosEnxugados;
    }
}