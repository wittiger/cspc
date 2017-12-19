package ast;


public final class Process {
	public Expression definition;
	
	public Process() {}
	
	public Process(Expression def) {
		this.definition = def;
	}
}
