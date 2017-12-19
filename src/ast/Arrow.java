package ast;

public final class Arrow extends Operator {
	public final Signal c;
	public Expression e;

	public Arrow(Signal c, Expression e) {
		this.c = c;
		this.e = e;
	}

}
