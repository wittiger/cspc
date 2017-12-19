package ast;

import java.util.HashSet;
import java.util.Set;

public final class Choice extends Operator {
	public Set<Expression> elements;

	public Choice(Expression a, Expression b) {
		elements = new HashSet<>();
		elements.add(a);
		elements.add(b);
	}
}
