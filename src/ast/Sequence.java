package ast;

public final class Sequence extends Operator {
	public Expression first;
	public Expression second;
	
	public Sequence(Expression a, Expression b) {
		first = a;
		second = b;
	}
}
