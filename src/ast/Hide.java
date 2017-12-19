package ast;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Hide extends Operator {
	public Expression target;
	public final Set<Signal> signals;
	
	public Hide (Expression e, Collection<Signal> cs) {
		assert(cs != null);
		
		target = e;
		signals = new HashSet<>(cs);
	}

}
