package ast.visitors;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import ast.Process;
import ast.CSPFile;
import ast.Constant;

/*
 * Override visitXXX method to get called for all XXX in the visited CSPFile.
 * 
 * Traversal-logic is implemented in the final methods.
 * 
 * (Like DefaultingExpressionVisitor but for files)
 */
public class DefaultingFileExpressionVisitor<E extends Throwable> extends DefaultingExpressionVisitor<E> implements AbstractFileVisitor<E> {
	private final Stack<Process> todo = new Stack<>();
	private final Set<Process> listed = new HashSet<>();
	
	private void enqueue(Process p) {
		if (!listed.contains(p)) {
			listed.add(p);
			todo.push(p);
		}
	}
	
	@Override
	public final void visit(Constant constant) throws E {
		enqueue(constant.process);
		
		visitConstant(constant);
	}
	
	@Override
	public void visit(CSPFile file) throws E {
		listed.clear();
		todo.clear();
		
		visitAny(file.leftAssertion);
		visitAny(file.rightAssertion);
		
		while(!todo.isEmpty()) 
			visitAny(todo.pop().definition);
	}
}
