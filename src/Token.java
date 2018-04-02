
public class Token {
	
	int classe;
	String lexema;
	
	
	public int getClasse() {
		return this.classe;
	}

	public void setId(int id) {
		this.classe = id;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

		
	public Token(int classe, String lexema) {
		this.classe = classe;
		this.lexema = lexema;
	}
	
}
