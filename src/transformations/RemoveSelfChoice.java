package transformations;

import java.util.Iterator;

import ast.CSPFile;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Process;
import ast.Stop;
import transformations.helpers.ChangeNotifier;

public final class RemoveSelfChoice extends Transformation {

	public RemoveSelfChoice(ChangeNotifier c) {
		super(c);
	}
	
	@Override
	protected String getName() {
		return "RemoveSelfChoice";
	}
	/* ********************************************************************* */

	@Override
	protected void transform(CSPFile file) {
		file.updateLiveProcesses();
		
		for (Process p : file.liveProcesses) {
			final Expression e = p.definition;
			
			if (e instanceof Constant) {
				// e is of form P = Q
				final Constant c = (Constant) e;
				
				if (c.process == p)
					p.definition = Stop.instance;
			}
			
			if (e instanceof Choice) {
				final Choice c = (Choice) e;
				
				final Iterator<Expression> iter = c.elements.iterator();
				
				while(iter.hasNext()) {
					final Expression sube = iter.next();
					if (sube instanceof Constant) {
						final Constant q = (Constant) sube;
						if (q.process == p) {
							ch();
							iter.remove();
						}
					}
				}
			}
		}
	}
}
