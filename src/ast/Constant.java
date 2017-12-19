package ast;

public final class Constant extends Expression {
	public Process process;
	
	public Constant(Process p) {
		process = p;
	}
}
