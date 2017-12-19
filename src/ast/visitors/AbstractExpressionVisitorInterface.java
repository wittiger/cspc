package ast.visitors;

import ast.Arrow;
import ast.Choice;
import ast.Constant;
import ast.Hide;
import ast.Sequence;
import ast.Skip;
import ast.Stop;
import ast.Sync;

public interface AbstractExpressionVisitorInterface<E extends Throwable> {
	void visit(Arrow arrow) throws E;
	void visit(Choice choice) throws E;
	void visit(Constant constant) throws E;
	void visit(Hide hide) throws E;
	void visit(Sequence sequence) throws E;
	void visit(Skip skip) throws E;
	void visit(Stop stop) throws E;
	void visit(Sync sync) throws E;
}
