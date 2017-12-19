package ast;

public final class Stop extends Expression {
	public static final Stop instance = new Stop();
	public static final Process process = new Process(instance); 
	
	private Stop() {}
}
