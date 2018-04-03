import java.util.LinkedList;

public class Grau {

	private LinkedList<Variavel> variaveis;
	
	public LinkedList<Variavel> getVarLista() {
		return this.variaveis;
	}
	
	public Grau() {
		this.variaveis = new LinkedList<>();
	}
	
	public void add(Variavel var) {
		this.variaveis.add(var);
	}
	
}
