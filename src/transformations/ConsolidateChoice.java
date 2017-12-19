package transformations;

import ast.CSPFile;
import ast.Choice;
import ast.Expression;
import ast.Stop;
import ast.visitors.DefaultingFileExpressionVisitor;
import transformations.helpers.ChangeNotifier;

public final class ConsolidateChoice extends Transformation {

	public ConsolidateChoice(ChangeNotifier c) {
		super(c);
	}

	@Override
	protected String getName() {
		return "ConsolidateChoice";
	}
	/* ********************************************************************* */

	@Override
	protected void transform(CSPFile file) {
		DefaultingFileExpressionVisitor<Error> v = new DefaultingFileExpressionVisitor<Error>() {
			@Override
			public void visitChoice(Choice choice) throws Error {
				choice.elements.remove(Stop.instance);
				
				Boolean run_one_more_time = true;
				
				while (run_one_more_time) {
					run_one_more_time = false;
					
					for (Expression sube : choice.elements) {
						if (sube instanceof Choice) {
							final Choice sc = (Choice) sube;
							choice.elements.remove(sube);
							choice.elements.addAll(sc.elements);
							
							ch();
							run_one_more_time = true;
							break;
						}
					}
				}
			}
			
			@Override
			public void visitExpression(Expression expression) {
				// Do nothing
			}
		};
		
		v.visit(file);
	}
}
