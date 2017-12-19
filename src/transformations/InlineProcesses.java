package transformations;

import java.util.HashMap;
import java.util.Map;

import ast.CSPFile;
import ast.Constant;
import ast.Expression;
import ast.Process;
import ast.visitors.DefaultingExpressionVisitor;
import ast.visitors.DefaultingFileExpressionVisitor;
import transformations.helpers.ChangeNotifier;
import transformations.helpers.replacers.ProcessByExpressionReplacer;

public final class InlineProcesses extends Transformation {

	public InlineProcesses(ChangeNotifier c) {
		super(c);
	}
	
	@Override
	protected String getName() {
		return "InlineProcesses";
	}
	/* ********************************************************************* */
	
	private final Map<Process, Integer> useCount = new HashMap<>();
	private final Map<Process, Expression> rep = new HashMap<>();

	private final void calculateProcessUseCount(CSPFile file) {
		final class CountVisitor extends DefaultingFileExpressionVisitor<Error> {
			@Override
			public void visitExpression(Expression expression) {
				// do nothing
			}
			
			@Override
			public void visitConstant(Constant constant) throws Error {
				useCount.put(constant.process, useCount.get(constant.process) + 1);
			}
		}
		
		new CountVisitor().visit(file);
	}

	private final void inline(final CSPFile file) {
		final class InlineVisitor extends DefaultingExpressionVisitor<Error> {
			public Process current;

			@Override
			public void visitExpression(Expression expression) {
				// do nothing
			}

			@Override
			public void visitConstant(Constant constant) throws Error {
				final Process inQuestion = constant.process;
				if (inQuestion != current && useCount.get(constant.process) == 1) {
					rep.put(inQuestion, inQuestion.definition);
					ch();
				}
			}
		}

		final InlineVisitor v = new InlineVisitor();
		
		for (Process p : file.liveProcesses) {
			v.current = p;
			v.visitAny(p.definition);
		}
		
		new ProcessByExpressionReplacer(c, rep).transform(file);
	}
	
	@Override
	protected void transform(CSPFile file) {
		file.updateLiveProcesses();
		
		for (Process p : file.liveProcesses) 
			useCount.put(p, 0);
		
		calculateProcessUseCount(file);
		inline(file);
	}

}
