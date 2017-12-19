package transformations.helpers.replacers;

import java.util.HashSet;
import java.util.Set;

import ast.Arrow;
import ast.CSPFile;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Hide;
import ast.Sequence;
import ast.Sync;
import ast.visitors.DefaultingFileExpressionVisitor;
import transformations.Transformation;
import transformations.helpers.ChangeNotifier;


public abstract class ExpressionReplacer extends Transformation {

	public ExpressionReplacer(ChangeNotifier c) {
		super(c);
	}

	@Override
	protected final String getName() {
		return "(int)";
	}
	
	protected abstract Expression rep(Expression e);

	private final class ReplacingVisitor extends DefaultingFileExpressionVisitor<Error> {
		@Override
		public void visitArrow(Arrow arrow) throws Error {
			arrow.e = rep(arrow.e);
		}
		
		private Set<Expression> recycle = new HashSet<>();
		
		@Override
		public void visitChoice(Choice choice) throws Error {
			final Set<Expression> old = choice.elements;
			
			recycle.clear();
			choice.elements = recycle;
			
			for (Expression cur : old) 
				choice.elements.add(rep(cur));
			
			recycle = old;
		}
		
		@Override
		public void visitConstant(Constant constant) throws Error {
			constant.process.definition = rep(constant.process.definition);
		}
		
		@Override
		public void visitHide(Hide hide) throws Error {
			hide.target = rep(hide.target);
		}
		
		@Override
		public void visitSequence(Sequence sequence) throws Error {
			sequence.first = rep(sequence.first);
			sequence.second = rep(sequence.second);
		}
		
		@Override
		public void visitSync(Sync sync) throws Error {
			sync.left = rep(sync.left);
			sync.right = rep(sync.right);
		}
		
		@Override
		public void visitExpression(Expression expression) {
			// Do nothing
		}
	}
	
	public final void transform(CSPFile file) {
		new ReplacingVisitor().visit(file);
		file.leftAssertion = rep(file.leftAssertion);
		file.rightAssertion = rep(file.rightAssertion);
	}
}
