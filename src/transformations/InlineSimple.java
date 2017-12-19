package transformations;

import java.util.HashMap;
import java.util.Map;

import ast.CSPFile;
import ast.Expression;
import ast.Process;
import ast.Skip;
import ast.Stop;
import transformations.helpers.ChangeNotifier;
import transformations.helpers.replacers.ProcessByExpressionReplacer;

public final class InlineSimple extends Transformation {

	public InlineSimple(ChangeNotifier c) {
		super(c);
	}
	
	@Override
	protected String getName() {
		return "InlineSimple";
	}
	/* ********************************************************************* */

	
	@Override
	protected void transform(CSPFile file) {
		final Map<Process, Expression> map = new HashMap<>();
		file.updateLiveProcesses();
		
		for (Process p : file.liveProcesses) {
			final Expression t = p.definition;
			
			if (t == Skip.instance)  
				map.put(p, Skip.instance);

			if (t == Stop.instance) 
				map.put(p, Stop.instance);
		}
		
		new ProcessByExpressionReplacer(c, map).transform(file);
	}
}
