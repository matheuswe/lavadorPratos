package br.edu.utfpr.td.tsi.lavador;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class Prato {

    private static final AtomicInteger contadorSerie = new AtomicInteger(0);

    private final int numeroSerie;
    private final NivelSujeira nivelSujeira;

    public Prato() {
        this.numeroSerie = contadorSerie.incrementAndGet();

        Random random = new Random();
        int sorteio = random.nextInt(100);

        if (sorteio < 30) {
            this.nivelSujeira = NivelSujeira.BAIXO;
        } else if (sorteio < 90) {
            this.nivelSujeira = NivelSujeira.MEDIO;
        } else {
            this.nivelSujeira = NivelSujeira.ENGORDURADO;
        }
    }

    public int getNumeroSerie() {
        return numeroSerie;
    }

    public NivelSujeira getNivelSujeira() {
        return nivelSujeira;
    }

    @Override
    public String toString() {
        return "Prato " + numeroSerie + " (" + nivelSujeira + ")";
    }
}