package lexer;

public class Token {
	
	int id;
	String lexema;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

		
	public Token(int id, String lexema) {
		this.id = id;
		this.lexema = lexema;
	}
	
}
