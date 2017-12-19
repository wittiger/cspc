package transformations.helpers.replacers;

import java.util.Map;

import ast.Expression;
import transformations.helpers.ChangeNotifier;

/**
 * Replaces expressions with expressions. Does not check avoiding bloating
 * recursive definitions.
 */
public final class ExpressionByExpressionReplacer extends ExpressionReplacer {
	private Map<Expression, Expression> map;

	public ExpressionByExpressionReplacer(ChangeNotifier c, Map<Expression, Expression> map) {
		super(c);
		this.map = map;
	}

	@Override
	protected Expression rep(Expression e) {
		assert (e != null);

		if (map.containsKey(e))
			return map.get(e);

		return e;
	}
}
