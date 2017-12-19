package ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ast.visitors.DefaultingFileExpressionVisitor;

public final class CSPFile {
	/**
	 * Only valid after calling updateLiveProcesses 
	 */
	public final Set<Process> liveProcesses = new HashSet<>();
	
	public void updateLiveProcesses() {
		final class LiveVisitor extends DefaultingFileExpressionVisitor<Error> {
			@Override
			public void visitExpression(Expression expression) {
				// do nothing
			}
			
			@Override
			public void visitConstant(Constant constant) throws Error {
				liveProcesses.add(constant.process);
			}
		}
		
		liveProcesses.clear();
		new LiveVisitor().visit(this);
	}
	
	private final Map<String, Process> processNames = new HashMap<>();
	public final Map<Process, String> originalProcessNames = new HashMap<>();
	private final Map<String, Signal> signalNames = new HashMap<>();
	public final Map<Signal,String> originalSignalNames = new HashMap<>();
	
	public Process getProcessByName(String name) {
		if (processNames.containsKey(name))
			return processNames.get(name);
		
		final Process ret = new Process();
		processNames.put(name, ret);
		originalProcessNames.put(ret, name);
		return ret;
	}	
	
	public Signal getSignalByName(String name) {
		if (signalNames.containsKey(name))
			return signalNames.get(name);
		
		final Signal ret = new Signal();
		originalSignalNames.put(ret, name);
		signalNames.put(name, ret);
		return ret;
	}
	
	public Set<Signal> declaredSignals = new HashSet<>();
	
	public Expression leftAssertion;
	public Expression rightAssertion;	
}
