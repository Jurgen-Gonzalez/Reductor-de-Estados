package code;

public class Delta {
    private char [] q; //Estado
    private String [] Σ; //Alfabeto
    private char [] δ; //Delta - representa la funcion δ(q,σ)   Alt Gr + 235

    public Delta(char [] q, String [] σ, char [] δ) {
        this.q = q;
        this.Σ = Σ;
        this.δ = δ;
    }

    public char [] getQ() {
        return q;
    }

    public String [] getΣ() {
        return Σ;
    }

    public char [] getΔ() {
        return δ;
    }
    
    
}
