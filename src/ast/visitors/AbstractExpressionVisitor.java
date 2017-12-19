package ast.visitors;

import ast.Arrow;
import ast.Choice;
import ast.Constant;
import ast.Expression;
import ast.Hide;
import ast.Sequence;
import ast.Skip;
import ast.Stop;
import ast.Sync;

public abstract class AbstractExpressionVisitor<E extends Throwable> implements AbstractExpressionVisitorInterface<E> {
	public final void visitAny(Expression e) throws E{
		final Class<? extends Expression> c = e.getClass();
		
		if (c == Arrow.class)
			visit((Arrow) e);
		else if (c == Choice.class)
			visit((Choice) e);
		else if (c == Constant.class)
			visit((Constant) e);
		else if (c == Hide.class)
			visit((Hide) e);
		else if (c == Sequence.class)
			visit((Sequence) e);
		else if (c == Skip.class)
			visit((Skip) e);
		else if (c == Stop.class)
			visit((Stop) e);
		else if (c == Sync.class)
			visit((Sync) e);
		else
			throw new Error("A new Class should be added to this list.");
	}
	
	@Override
	public void visit(Arrow arrow) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Choice choice) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Constant constant) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Hide hide) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Sequence sequence) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Skip skip) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Stop stop) throws E {
		throw new Error("Unimplemented");
	}

	@Override
	public void visit(Sync sync) throws E {
		throw new Error("Unimplemented");
	}
}
