package br.edu.utfpr.td.tsi.lavador;

import java.util.logging.Logger;

public class Lavador implements Runnable {

    private static Logger logger = Logger.getLogger(Lavador.class.getSimpleName());

    private static final long CONTAGEM_BAIXO = 30000000L;
    private static final long CONTAGEM_MEDIO = 50000000L;
    private static final long CONTAGEM_ENGORDURADO = 100000000L;

    private final Escorredor escorredor;

    private volatile boolean done;

    private volatile long contador;

    private int pratosLavados;

    public Lavador(Escorredor escorredor) {
        this.escorredor = escorredor;
        this.done = false;
        this.contador = 0;
        this.pratosLavados = 0;
    }

    @Override
    public void run() {
        String nomeThread = Thread.currentThread().getName();
        logger.info(nomeThread + " começou a trabalhar");

        while (!done) {
            Prato prato = new Prato();
            logger.fine(nomeThread + " pegou " + prato + " da pilha de pratos sujos");

            lavar(prato);
            logger.fine(nomeThread + " terminou de lavar " + prato);

            escorredor.colocar(prato);
            pratosLavados = pratosLavados + 1;
        }

        logger.info(nomeThread + " terminou, pratos lavados: " + pratosLavados);
    }

    private void lavar(Prato prato) {
        long limite;
        if (prato.getNivelSujeira() == NivelSujeira.BAIXO) {
            limite = CONTAGEM_BAIXO;
        } else if (prato.getNivelSujeira() == NivelSujeira.MEDIO) {
            limite = CONTAGEM_MEDIO;
        } else {
            limite = CONTAGEM_ENGORDURADO;
        }

        contador = 0;
        for (long i = 0; i < limite; i++) {
            contador++;
        }
    }

    public void parar() {
        done = true;
    }

    public int getPratosLavados() {
        return pratosLavados;
    }
}