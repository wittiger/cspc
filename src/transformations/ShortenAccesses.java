package transformations;

import ast.CSPFile;
import ast.Constant;
import ast.Expression;
import ast.Process;
import ast.Stop;
import ast.visitors.DefaultingFileExpressionVisitor;
import transformations.helpers.ChangeNotifier;
import transformations.helpers.DelegationFind;

public final class ShortenAccesses extends Transformation {

	public ShortenAccesses(ChangeNotifier c) {
		super(c);
	}
	
	@Override
	protected String getName() {
		return "ShortenAccesses";
	}
	/* ********************************************************************* */

	
	@Override
	protected void transform(final CSPFile file) {
		final DelegationFind<Process, Process> df = new DelegationFind<>(Stop.process);

		file.updateLiveProcesses();
		
		// Find all Accesses
		for (Process p : file.liveProcesses) {
			final Expression e = p.definition;
			
			if (e instanceof Constant) {
				// This is an access definition, like P = Q
				final Constant c = (Constant) e;
				//System.err.println("u " + file.originalProcessNames.get(p) + "  " + file.originalProcessNames.get(df.getValue(c.process)));
				df.unify(p, c.process);
			}
			else {
				// This could be a leaf
				//System.err.println("d " + file.originalProcessNames.get(p));
				df.defineValue(p, p);
			}
		}
		
		// Replace constants where applicable
		DefaultingFileExpressionVisitor<Error> v = new DefaultingFileExpressionVisitor<Error>() {
			@Override
			public void visitConstant(Constant constant) {
				//System.err.println("g " + file.originalProcessNames.get(constant.process) + "  " + file.originalProcessNames.get(df.getValue(constant.process)));
				constant.process = df.getValue(constant.process);
			};
			
			@Override
			public void visitExpression(Expression expression) {
				// Do nothing
			}
		};
		
		v.visit(file);
	}
}
