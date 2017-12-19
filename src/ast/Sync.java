package ast;

import java.util.HashSet;
import java.util.Set;

public final class Sync extends Operator {
	public Expression left;
	public Expression right;
	public Set<Signal> cs = new HashSet<>();
}
