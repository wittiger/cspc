package ast.visitors;

import ast.CSPFile;

public interface AbstractFileVisitor<E extends Throwable> extends AbstractExpressionVisitorInterface<E> {
	void visit(CSPFile file) throws E;
}
