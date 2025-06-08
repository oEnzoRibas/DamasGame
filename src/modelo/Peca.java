package modelo;

public class Peca {

    public enum Cor {
        BRANCA,
        PRETA
    }

    private Cor cor;
    private boolean isDama;

    public Peca(Cor cor) {
        this.cor = cor;
        this.isDama = false;
    }

    public Cor getCor() {
        return cor;
    }

    public boolean isDama() {
        return isDama;
    }

    public void promover() {
        this.isDama = true;
    }
}
