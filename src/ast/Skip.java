package ast;

public final class Skip extends Expression {
	public static final Skip instance = new Skip();
	public static final Process process = new Process(instance); 
	
	private Skip() { }
}
