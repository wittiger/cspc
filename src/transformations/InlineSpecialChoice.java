package transformations;

import java.util.HashSet;
import java.util.Set;

import ast.CSPFile;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Process;
import ast.Skip;
import ast.visitors.DefaultingFileExpressionVisitor;
import transformations.helpers.ChangeNotifier;
import transformations.helpers.DelegationFind;

public class InlineSpecialChoice extends Transformation {

	public InlineSpecialChoice(ChangeNotifier c) {
		super(c);
	}

	@Override
	protected String getName() {
		return "InlineSpecialChoice";
	}
	/* ********************************************************************* */
	
	private final DelegationFind<Process, Process> df = new DelegationFind<>(Skip.process);
	
	
	private boolean special(Process p) {
		final Expression e = p.definition;

		if (!(e instanceof Choice))
			return false;

		final Choice c = (Choice) e;

		if (c.elements.size() != 2)
			return false;

		boolean has_skip = false;
		boolean has_const = false;
		Process target = null;

		for (Expression sube : c.elements) {
			if (sube instanceof Constant) {
				has_const = true;
				target = ((Constant) sube).process;
			}
			has_skip |= sube == Skip.instance;
		}

		if (!(has_const && has_skip))
			return false;
		
		df.unify(p, target);
		return true;
	}

	
	@Override
	protected void transform(CSPFile file) {	
		file.updateLiveProcesses();
		
		final Set<Process> specials = new HashSet<>();
		
		for (Process p : file.liveProcesses) {
			if (special(p)) 
				specials.add(p);
			else
				df.defineValue(p, p);
		}

		for (Process p : specials) {
			final Process result = df.getValue(p);
			
			if (result == Skip.process) 
				p.definition = Skip.instance;
			else {
				final Choice c = (Choice) p.definition;
				c.elements.clear();
				c.elements.add(Skip.instance);
				c.elements.add(new Constant(result));
			}
		}
		
		DefaultingFileExpressionVisitor<Error> v = new DefaultingFileExpressionVisitor<Error>() {
			@Override
			public void visitExpression(Expression expression) {
				// Do nothing
			}
			@Override
			public void visitChoice(Choice choice) throws Error {
				if (!choice.elements.contains(Skip.instance)) return;
				
				for (Expression e : choice.elements) {
					if (e instanceof Constant) {
						final Constant c = (Constant) e;
						if (specials.contains(c.process)) 
							c.process = df.getValue(c.process);
					}
				}
			}
		};
		
		v.visit(file);
	}
}
