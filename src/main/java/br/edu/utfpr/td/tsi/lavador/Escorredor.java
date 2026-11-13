package br.edu.utfpr.td.tsi.lavador;

import java.util.logging.Logger;

public class Escorredor {

    private static Logger logger = Logger.getLogger(Escorredor.class.getSimpleName());

    private final Prato[] pratos;
    private final int capacidade;
    private int inicio;
    private int fim;
    private int quantidade;
    private boolean encerrado;

    private final Object travaLavadores = new Object();
    private final Object travaEnxugadores = new Object();

    public Escorredor(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade do escorredor deve ser maior que zero");
        }
        this.capacidade = capacidade;
        this.pratos = new Prato[capacidade];
        this.inicio = 0;
        this.fim = 0;
        this.quantidade = 0;
        this.encerrado = false;
    }

    public void colocar(Prato prato) {
        String nomeThread = Thread.currentThread().getName();

        synchronized (travaLavadores) {
            synchronized (this) {

                while (quantidade == capacidade) {
                    logger.fine(nomeThread + " encontrou o escorredor CHEIO (" + quantidade + " pratos) e vai aguardar");
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        logger.warning(nomeThread + " foi interrompido enquanto aguardava");
                        Thread.currentThread().interrupt();
                        return;
                    }
                    logger.fine(nomeThread + " acordou");
                }

                pratos[fim] = prato;
                fim = fim + 1;
                if (fim == capacidade) {
                    fim = 0;
                }
                quantidade = quantidade + 1;
                logger.fine(nomeThread + " colocou " + prato + " no escorredor (" + quantidade + "/" + capacidade + ")");

                if (quantidade == capacidade) {
                    logger.info("Escorredor CHEIO: " + quantidade + " pratos");
                    notify();
                }
            }
        }
    }

    public Prato retirar() {
        String nomeThread = Thread.currentThread().getName();

        synchronized (travaEnxugadores) {
            synchronized (this) {

                while (quantidade == 0) {
                    if (encerrado) {
                        return null;
                    }
                    logger.fine(nomeThread + " encontrou o escorredor VAZIO (" + quantidade + " pratos) e vai aguardar");
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        logger.warning(nomeThread + " foi interrompido enquanto aguardava");
                        Thread.currentThread().interrupt();
                        return null;
                    }
                    logger.fine(nomeThread + " acordou");
                }

                Prato prato = pratos[inicio];
                pratos[inicio] = null;
                inicio = inicio + 1;
                if (inicio == capacidade) {
                    inicio = 0;
                }
                quantidade = quantidade - 1;
                logger.fine(nomeThread + " retirou " + prato + " do escorredor (" + quantidade + "/" + capacidade + ")");

                if (quantidade == 0) {
                    logger.info("Escorredor VAZIO: " + quantidade + " pratos");
                    notify();
                }

                return prato;
            }
        }
    }

    public synchronized void encerrar() {
        encerrado = true;
        logger.fine("Escorredor encerrado: não chegarao mais pratos");
        notify();
    }

    public synchronized int getQuantidade() {
        return quantidade;
    }

    public int getCapacidade() {
        return capacidade;
    }
}