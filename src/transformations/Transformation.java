package transformations;

import ast.CSPFile;
import transformations.helpers.ChangeNotifier;

public abstract class Transformation {
	final ChangeNotifier c;
	
	public Transformation(ChangeNotifier c) {
		this.c = c;
	}
	
	protected final void ch() {
		c.set();
	}
	
	protected abstract String getName();
	
	protected abstract void transform(CSPFile file);
	
	private void err() {
		if (c.isSet()) 
			System.err.println(System.currentTimeMillis() + ": " + getName() + " *");
		else
			System.err.println(System.currentTimeMillis() + ": " + getName());
		
		System.err.flush();
	}
	
	public final void performTransformation(CSPFile file) {
		err();
		
		transform(file);
	}
}