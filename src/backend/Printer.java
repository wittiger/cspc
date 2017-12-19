package backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ast.Arrow;
import ast.CSPFile;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Hide;
import ast.Process;
import ast.Sequence;
import ast.Signal;
import ast.Skip;
import ast.Stop;
import ast.Sync;
import ast.visitors.AbstractExpressionVisitor;
import ast.visitors.AbstractFileVisitor;

public class Printer extends AbstractExpressionVisitor<Error> implements AbstractFileVisitor<Error> {
	private CSPFile file;
	private final Map<Process, Integer> processNames = new HashMap<>();
	private int nextName = 1;
	private Writer writer;
	
	public Printer(File outfile) {
		try {
			writer = new BufferedWriter(new FileWriter(outfile));
		} catch (IOException e) {
			throw new Error(e.getMessage());
		} 
	}

	private void print(String s) {
		try {
			writer.write(s);
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
	}

	private void printSignalName(Signal s) {
		print(file.originalSignalNames.get(s));
	}
	
	private void printSignalSet(Collection<Signal> cs) {
		print("{");
		
		Boolean first = true;
		for (Signal s : cs) {
			if (!first) print(", ");
			
			first = false;
			printSignalName(s);
		}
		
		print("}");
	}

	private void printProcess(Process p) {
		if (file.originalProcessNames.containsKey(p)) {
			print(file.originalProcessNames.get(p));
			return;
		}
		int name;
		
		if (processNames.containsKey(p)) {
			name = processNames.get(p);
		}
		else {
			name = nextName;
			nextName++;
			processNames.put(p, name);
		}
		
		print("P" + name);
	}
	

	private void open() {
		print("(");
	}
	
	private void close() {
		print(")");
	}
	


	@Override
	public void visit(Arrow arrow) {
		open();
		printSignalName(arrow.c);
		print(" -> ");
		visitAny(arrow.e);
		close();
	}

	@Override
	public void visit(Choice choice) {
		if (choice.elements.size() == 0) {
			print("STOP");
			return;
		}
		
		open();
		Boolean first = true;
		for (Expression e : choice.elements) {
			if (!first) print(" |~| ");
			first = false;
			visitAny(e);
		}	
		close();
	}

	@Override
	public void visit(Constant constant) {
		printProcess(constant.process);
		
	}

	@Override
	public void visit(Hide hide) {
		open();
		visitAny(hide.target);
		print(" \\ ");
		printSignalSet(hide.signals);
		close();
	}

	@Override
	public void visit(Sequence sequence) {
		open();
		visitAny(sequence.first);
		print(" ; ");
		visitAny(sequence.second);
		close();
	}

	@Override
	public void visit(Skip skip) {
		print("SKIP");
	}

	@Override
	public void visit(Stop stop) {
		print("STOP");
	}

	@Override
	public void visit(Sync sync) {
		open();
		visitAny(sync.left);
		print(" [| ");
		printSignalSet(sync.cs);
		print(" |] ");
		visitAny(sync.right);
		close();
	}

	@Override
	public void visit(CSPFile file) {
		this.file = file;
		
		/*
		 * Declarations
		 */		
		
		print("\n--  Declarations:\n\n");
		if (file.declaredSignals.size() > 0) {
			print("channel ");
			Boolean first = true;
			
			for (Signal s : file.declaredSignals) {
				if (!first) print(", ");
				first = false;
				printSignalName(s);
			}
		}
		
		/*
		 * Definitions
		 */
		
		file.updateLiveProcesses();
		
		print("\n--  Process Definitions:\n\n");
		
		for (Process p : file.liveProcesses) {
			printProcess(p);
			print(" = ");
			visitAny(p.definition);
			print("\n");
		}
		
		/*
		 * Assertions
		 */
		
		print("\n--  Assertion:\n\nassert ");
		
		visitAny(file.leftAssertion);
		print(" [T= ");
		visitAny(file.rightAssertion);
		
		print("\n\n");
	}
	
	public void printString() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
	}
}
