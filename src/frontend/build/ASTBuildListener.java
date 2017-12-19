package frontend.build;

import java.util.Stack;

import org.antlr.v4.runtime.tree.TerminalNode;

import ast.*;
import ast.Process;
import frontend.antlr.CspBaseListener;
import frontend.antlr.CspParser.AssertionContext;
import frontend.antlr.CspParser.DeclarationContext;
import frontend.antlr.CspParser.DefinitionContext;
import frontend.antlr.CspParser.EarrowContext;
import frontend.antlr.CspParser.EchoiceContext;
import frontend.antlr.CspParser.EhideContext;
import frontend.antlr.CspParser.EidContext;
import frontend.antlr.CspParser.EinterContext;
import frontend.antlr.CspParser.EseqContext;
import frontend.antlr.CspParser.EskipContext;
import frontend.antlr.CspParser.EstopContext;
import frontend.antlr.CspParser.EsyncContext;
import frontend.antlr.CspParser.FileContext;
import frontend.antlr.CspParser.SetContext;

public final class ASTBuildListener extends CspBaseListener {
	private Stack<Expression> expressions = new Stack<>();
	private Stack<Signal> signals = new Stack<>();
	private CSPFile file;
	
	public CSPFile getFile() {
		return file;
	}
	
	@Override
	public void enterFile(FileContext ctx) {
		file = new CSPFile();
	}
	
	
	/*
	 * assertion
	 *    : ASSERT expression MOREREF expression
	 *    ;
	 */
	
	@Override
	public void exitAssertion(AssertionContext ctx) {
		assert (expressions.size() == 2);
		
		file.rightAssertion = expressions.pop();
		file.leftAssertion = expressions.pop();
	}
	
	
	/* 
	 * declaration
	 *    : CHANNEL ID (COMMA ID)*
	 *    ;
	 */
	
	@Override
	public void exitDeclaration(DeclarationContext ctx) {
		assert (ctx.ID().size() > 0);
		
		for (TerminalNode n : ctx.ID()) {
			Signal s = file.getSignalByName(n.getSymbol().getText());
			file.declaredSignals.add(s);
		}
	}
	

	/*
	 * definition
	 *    : ID ASSIGN expression
	 *    ;
	 */
	
	@Override
	public void exitDefinition(DefinitionContext ctx) {
		assert (expressions.size() == 1);
		
		Process p = file.getProcessByName(ctx.ID().getText());
		p.definition = expressions.pop();
	}
	
	
	/* 
	 * expression
	 *    : ID                                                    # eid
	 *    | SKIPP                                                 # eskip
	 *    | STOPP                                                 # estop
	 *    | LPAREN expression RPAREN                              # enop
	 *    | ID ARROW expression                                   # earrow
	 *    | expression SEQ expression                             # eseq
	 *    | expression CHOICE expression                          # echoice
	 *    | expression HIDE set                                   # ehide
	 *    | expression INTER expression                           # einter
	 *    | LPAREN expression LSYNC set RSYNC expression RPAREN   # esync
	 *    ;
	 */
	
	@Override
	public void exitEid(EidContext ctx) {
		expressions.push(new Constant(file.getProcessByName(ctx.ID().getText())));
	}
	
	@Override
	public void exitEskip(EskipContext ctx) {
		expressions.push(Skip.instance);
	}	
	
	@Override
	public void exitEstop(EstopContext ctx) {
		expressions.push(Stop.instance);
	}
	
	@Override
	public void exitEarrow(EarrowContext ctx) {
		Signal c = file.getSignalByName(ctx.ID().getText());
		Expression p = expressions.pop();
		
		expressions.push(new Arrow(c, p));
	}
	
	@Override
	public void exitEseq(EseqContext ctx) {
		Expression second = expressions.pop();
		Expression first = expressions.pop();
		 
		expressions.push(new Sequence(first, second));
	}
	
	@Override
	public void exitEchoice(EchoiceContext ctx) {
		Expression a = expressions.pop();
		Expression b = expressions.pop();
		 
		expressions.push(new Choice(a, b)); 
	}
	
	@Override
	public void exitEhide(EhideContext ctx) {
		Expression e = expressions.pop();
		
		expressions.push(new Hide(e, signals));
		
		signals.clear();
	}
	
	@Override
	public void exitEinter(EinterContext ctx) {
		assert (signals.isEmpty());
		
		Sync e = new Sync();
		
		e.left = expressions.pop();
		e.right = expressions.pop();
		
		expressions.push(e);
	}
	
	@Override
	public void exitEsync(EsyncContext ctx) {
		assert (!signals.isEmpty());
		
		Sync e = new Sync();
		
		e.left = expressions.pop();
		e.right = expressions.pop();
		e.cs.addAll(signals);
		
		signals.clear();
		
		expressions.push(e);
	}
	
	
	/* 
	 * set
	 *    : LCURLYBRACKET (ID (COMMA ID)*)? RCURLYBRACKET
	 *    ;
	 */
	
	@Override
	public void exitSet(SetContext ctx) {
		assert (signals.isEmpty());
		
		for (TerminalNode n : ctx.ID()) {
			signals.push(file.getSignalByName(n.getText()));
		}
	}
}
