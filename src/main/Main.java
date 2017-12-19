package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import ast.CSPFile;
import backend.Printer;
import frontend.antlr.CspLexer;
import frontend.antlr.CspParser;
import frontend.build.ASTBuildListener;
import transformations.Clean;
import transformations.ConsolidateChoice;
import transformations.FindNullassertions;
import transformations.InlineProcesses;
import transformations.InlineSimple;
import transformations.InlineSpecialChoice;
import transformations.RemoveSelfChoice;
import transformations.ShortenAccesses;
import transformations.helpers.ChangeNotifier;

public final class Main {
	private static final CspParser buildParser(File in)
			throws FileNotFoundException, IOException {
		
		InputStream is = new FileInputStream(in);
		ANTLRInputStream input = new ANTLRInputStream(is);

		CspLexer lexer = new CspLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CspParser parser = new CspParser(tokens);

		return parser;
	}
	
	
	public static void main(String[] args) throws Throwable {
		if (args.length != 2) {
			System.err.println("Usage: cspc infile outfile");
			System.err.println("");
			return;
		}
		
		System.err.println("Infile: "  + args[0]);
		System.err.println("Outfile: " + args[1]);
		
		final File infile = new File(args[0]);;
		final File outfile = new File(args[1]);;
	 
		CspParser parser = buildParser(infile);
		
		ParseTree t = parser.file();
		
		ParseTreeWalker walker = new ParseTreeWalker();
		ASTBuildListener astb = new ASTBuildListener();

		walker.walk(astb, t);
		
		final CSPFile cspfile = astb.getFile();
		
		// No Assertion in file!
		if (cspfile.leftAssertion == null) {
			try {
				final Writer writer = new FileWriter(outfile);
				writer.write("--  No Assertion was present in the input file\n\n");
				writer.close();
			} catch (IOException e) {
				throw new Error(e.getMessage());
			} 
			return;
		}
		
		final ChangeNotifier c = new ChangeNotifier();
		
		c.set();
		//while(c.isSet()) {
		
		for (int i = 0; i < 50; i++) {
			c.reset();
			new ShortenAccesses(c).performTransformation(cspfile);
			new ShortenAccesses(c).performTransformation(cspfile);
			new RemoveSelfChoice(c).performTransformation(cspfile);
			new ConsolidateChoice(c).performTransformation(cspfile);
			new InlineSimple(c).performTransformation(cspfile);
			new Clean(c).performTransformation(cspfile);
			new InlineSpecialChoice(c).performTransformation(cspfile);
			new InlineProcesses(c).performTransformation(cspfile);
			new FindNullassertions(c).performTransformation(cspfile);
		}
		
		final Printer printer = new Printer(outfile);
		
		printer.visit(cspfile);
		printer.printString();
		
		System.err.println("Succesful. ");
	}
}
