

public class MainParser {

	public static void main(String[] args) {
		
		Parser parser = new Parser(args[0]); // cria o analisador lexico
		
		while(true) {			
					
			parser.execute();
			break;	
		}
		
	}
}
