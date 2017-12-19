package ast.visitors;

import ast.Arrow;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Hide;
import ast.Operator;
import ast.Sequence;
import ast.Skip;
import ast.Stop;
import ast.Sync;

/*
 * Override visitXXX method to get called for all XXX in the visited expression.
 * 
 * Traversal-logic is implemented in the final methods.
 */
public class DefaultingExpressionVisitor<E extends Throwable> extends AbstractExpressionVisitor<E> {

	public void visitExpression(Expression expression) {
		throw new Error("Unimplemented");
	}
	
	public void visitOperator(Operator operator) {
		visitExpression(operator);
	}

	
	public void visitArrow(Arrow arrow) throws E {
		visitOperator(arrow);	
	}

	
	public void visitChoice(Choice choice) throws E {
		visitOperator(choice);
	}

	
	public void visitConstant(Constant constant) throws E {
		visitExpression(constant);
	}

	
	public void visitHide(Hide hide) throws E {
		visitOperator(hide);
	}

	
	public void visitSequence(Sequence sequence) throws E {
		visitOperator(sequence);
	}

	
	public void visitSkip(Skip skip) throws E {
		visitExpression(skip);
	}

	
	public void visitStop(Stop stop) throws E {
		visitExpression(stop);
	}

	
	public void visitSync(Sync sync) throws E {
		visitOperator(sync);
	}
	
	@Override
	public final void visit(Arrow arrow) throws E {
		visitAny(arrow.e);
		
		visitArrow(arrow);
	}

	@Override
	public final void visit(Choice choice) throws E {
		for (Expression e : choice.elements)
			visitAny(e);
		
		visitChoice(choice);
	}

	/* 
	 * the only non-final visit-method 
	 */
	@Override
	public void visit(Constant constant) throws E {
		// Do nothing
		
		visitConstant(constant);
	}

	@Override
	public final void visit(Hide hide) throws E {
		visitAny(hide.target);
		
		visitHide(hide);
	}

	@Override
	public final void visit(Sequence sequence) throws E {
		visitAny(sequence.first);
		visitAny(sequence.second);
		
		visitSequence(sequence);
	}

	@Override
	public final void visit(Skip skip) throws E {
		// Do nothing
		
		visitSkip(skip);
	}

	@Override
	public final void visit(Stop stop) throws E {
		// Do nothing
		
		visitStop(stop);
	}

	@Override
	public final void visit(Sync sync) throws E {
		visitAny(sync.left);
		visitAny(sync.right);
		
		visitSync(sync);
	}
}
