package transformations.helpers.replacers;

import java.util.Map;

import ast.Process;
import transformations.helpers.ChangeNotifier;
import ast.Constant;
import ast.Expression;

/**
 * Replaces Constant (P) with expressions. Does not check avoiding bloating
 * recursive definitions.
 */

public final class ProcessByExpressionReplacer extends ExpressionReplacer {
	private Map<Process, Expression> map;
	
	public ProcessByExpressionReplacer(ChangeNotifier c, Map<Process, Expression> map) {
		super(c);
		this.map = map;
	}

	@Override
	protected Expression rep(Expression e) {
		if (e instanceof Constant) {
			final Constant c = (Constant) e;
			final Process p = c.process;
			
			if (map.containsKey(p)) 
				return map.get(p);
		}
		
		return e;
	}
}
