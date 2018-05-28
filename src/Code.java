
public class Code {

	
	public int classe;
	public String lexema;
	private int operador;
	
	public Code(int classe, String lexema){
		this.classe = classe;
		this.lexema = lexema;
		this.setOperador(0);
	}

	public int getClasse() {
		return classe;
	}

	public void setClasse(int classe) {
		this.classe = classe;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public int getOperador() {
		return operador;
	}

	public void setOperador(int operador) {
		this.operador = operador;
	}
	
}
