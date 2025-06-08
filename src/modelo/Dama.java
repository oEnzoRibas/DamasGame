package modelo;

import modelo.Peca.Cor;

public class Dama extends Peca {

    public Dama(Cor cor) {
        super(cor);
        promover();
    }
}
