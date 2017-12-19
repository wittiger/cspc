package transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ast.Process;
import ast.Arrow;
import ast.CSPFile;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Hide;
import ast.Sequence;
import ast.Skip;
import ast.Stop;
import ast.Sync;
import ast.visitors.DefaultingFileExpressionVisitor;
import transformations.helpers.ChangeNotifier;
import transformations.helpers.replacers.ExpressionByExpressionReplacer;

public final class Clean extends Transformation {
	public Clean(ChangeNotifier c) {
		super(c);
	}

	@Override
	protected String getName() {
		return "Clean";
	}
	/* ********************************************************************* */
	
	HashMap<Expression, Expression> map = new HashMap<>();
	
	private void replace(Expression a, Expression b)  {
		map.put(a, b);
	}
	
	private class Cleaner extends DefaultingFileExpressionVisitor<Error> {
		@Override
		public void visitExpression(Expression expression) {
			// Do nothing
		}
		
		/*
		 * Implements this:
		 * 
		let clean =
				   let rec r_exp e =
				      match e with
				            Hide (e, [])            -> ch (); r_exp e           (* P \ {}    = P                   *)   A
				         |  Choice [e]              -> ch (); r_exp e													B
				         |  Hide (Choice [], _)     -> ch (); Choice []         (* STOP \ X  =  STOP               *)	C
				         |  Hide (Skip, _)          -> ch (); Skip              (* SKIP \ X  =  SKIP               *)	D
				         |  Seq (Skip, e)           -> ch (); r_exp e           (* SKIP ; P  =  P                  *)	E
				         |  Seq (e, Skip)           -> ch (); r_exp e           (* P ; SKIP  =  P (s. p. 159 Hoare *)	F
				         |  Seq (Choice [], _)      -> ch (); Choice []         (* STOP ; P  =  STOP               *)	G
				         |  Sync (Id a, _, Id b)    -> if (a = b)               (* P [| X |] P  =  P *)					No! H
				                                          then (ch (); Id a)								
				                                          else e
				         |  Sync (Choice [], [], p) -> ch (); r_exp p           (* STOP ||| P  =  P                *)	I
				         |  Sync (p, [], Choice []) -> ch (); r_exp p           (* P ||| STOP  =  P                *)	J
				         |  Hide (Arrow (c, e), h)  -> ch ();                   (* (a -> P) \ {a] = P \ {a]        *)	Ka
				                                       if (List.mem c h)        (* (a -> P) \ {b] = a -> (P \ {b}) *)	Kb *
				                                          then r_exp (Hide (e, h))
				                                          else Arrow (c, r_exp (Hide (e, h)))
				         |  Seq (Choice [                                       (* A very specific pattern...       *)  L  *
				               (Arrow (a, Choice []));                          (* (a -> STOP |~| SKIP) ; Q  =  a -> STOP |~| Q *)
				               Skip], q)            -> ch (); Choice [Arrow (a, Choice []); r_exp q]
				         |  Seq (Choice [Skip;
				               (Arrow (a, Choice []))
				               ], q)             -> ch (); Choice [Arrow (a, Choice []); r_exp q]
				         |  _                    -> e in              (* Do _not_ recurse if not immediately problematic *)
		*/
		@Override
		public void visitHide(Hide hide) {
			// A
			if (hide.signals.isEmpty())				
				replace(hide, hide.target);
			
			// C
			if (hide.target == Stop.instance) 
				replace(hide, Stop.instance);
			
			// D
			if (hide.target == Skip.instance) 
				replace(hide, Skip.instance);
			
			// K
			if (hide.target instanceof Arrow) {
				Arrow a = (Arrow) hide.target;
				// Ka
				if (hide.signals.contains(a.c)) {
					hide.target = a.e;
				}
				else {
					// Kb not implemented
				}
			}
		}
		
		/* 
		 * Empty after visitChoice is called 
		 */
		private final Set<Process> s = new HashSet<>();
		
		@Override
		public void visitChoice(Choice choice) {
			final int size = choice.elements.size();
			if (size == 0)
				replace(choice, Stop.instance);
			// B
			if (size == 1)
				replace(choice, choice.elements.iterator().next());
			
			Iterator<Expression> iter = choice.elements.iterator();
			while (iter.hasNext()) {
				final Expression e = iter.next();
				if (e instanceof Constant) {
					final Constant c = (Constant) e;
					if (s.contains(c.process))
						iter.remove();
					else
						s.add(c.process);
				}
			}
			s.clear();
		}
		
		@Override
		public void visitSequence(Sequence sequence) {
			// E
			if (sequence.first == Skip.instance)
				replace (sequence, sequence.second);
			
			// G
			if (sequence.first == Stop.instance)
				replace (sequence, Stop.instance);
			
			// F
			if (sequence.second == Skip.instance)
				replace (sequence, sequence.first);
		}
		
		@Override
		public void visitSync(Sync sync) {
			// I
			if (sync.left == Stop.instance && sync.cs.isEmpty()) 
				replace(sync, sync.right);
			// J
			if (sync.right == Stop.instance && sync.cs.isEmpty()) 
				replace(sync, sync.left);
		}	
	}

	@Override
	protected void transform(CSPFile file) {
		final Cleaner v = new Cleaner();
		
		v.visit(file);
		
		final ExpressionByExpressionReplacer rep = new ExpressionByExpressionReplacer(c, map);
				
		rep.transform(file);
	}
}
