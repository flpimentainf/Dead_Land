package DEAD.LAND.core;

public class SistemaEstamina {
    public static final float MAXIMA = 100f;
    public static final float CUSTO_DASH = 20f;
    private static final double CONSUMO_CORRIDA_POR_SEGUNDO = 10.0;
    private static final double RECARGA_POR_SEGUNDO = 20.0;
    private static final double ESPERA_RECARGA_SEGUNDOS = 0.8;

    private double estamina = MAXIMA;
    private double esperaRecarga;
    private boolean esgotada;

    public boolean consumirDash() {
        if (estamina < CUSTO_DASH) {
            return false;
        }
        estamina -= CUSTO_DASH;
        esperaRecarga = ESPERA_RECARGA_SEGUNDOS;
        esgotada = estamina <= 0;
        return true;
    }

    public boolean atualizar(double segundos, boolean tentarCorrer) {
        if (!Double.isFinite(segundos) || segundos <= 0) {
            return false;
        }
        if (tentarCorrer && !esgotada && estamina > 0) {
            estamina = Math.max(0, estamina - CONSUMO_CORRIDA_POR_SEGUNDO * segundos);
            esperaRecarga = ESPERA_RECARGA_SEGUNDOS;
            esgotada = estamina <= 0;
            return !esgotada;
        }

        double tempoRecarga = Math.max(0, segundos - esperaRecarga);
        esperaRecarga = Math.max(0, esperaRecarga - segundos);
        estamina = Math.min(MAXIMA, estamina + RECARGA_POR_SEGUNDO * tempoRecarga);
        if (estamina >= CUSTO_DASH) {
            esgotada = false;
        }
        return false;
    }

    public float getValor() { return (float) estamina; }
    public float getProporcao() { return (float) (estamina / MAXIMA); }

    public void restaurarCompleta() {
        estamina = MAXIMA;
        esperaRecarga = 0;
        esgotada = false;
    }
}
