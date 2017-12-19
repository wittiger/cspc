package transformations;

import ast.CSPFile;
import ast.Constant;
import ast.Expression;
import ast.Stop;
import transformations.helpers.ChangeNotifier;

public final class FindNullassertions extends Transformation {

	public FindNullassertions(ChangeNotifier c) {
		super(c);
	}
	
	@Override
	protected String getName() {
		return "FindNullassertions";
	}
	/* ********************************************************************* */

	@Override
	protected void transform(CSPFile file) {
		final Expression l = file.leftAssertion;
		final Expression r = file.rightAssertion;

		if (
				l == r 
				|| r == Stop.instance
				|| (r instanceof Constant && ((Constant) r).process.definition == Stop.instance)) {
			file.leftAssertion = Stop.instance;
			file.rightAssertion = Stop.instance;
			ch();
		}
	}
}
