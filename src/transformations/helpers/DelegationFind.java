package transformations.helpers;

import java.util.HashMap;

/**
 * A Variation of Union Find with Values on representatives.
 */
public final class DelegationFind<Key, Value> {
	
	private final Value defaultValue;
	private final HashMap<Key, Key> delegates = new HashMap<>();
	private final HashMap<Key, Value> values = new HashMap<>();

	public DelegationFind(Value defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	private void ensure(Key k) {
		if (!delegates.containsKey(k))
			delegates.put(k, k);
	}
	
	private Key find(Key k) {
		assert(delegates.containsKey(k));
		
		Key parent = delegates.get(k);
		
		if (parent == k) return k;
		else parent = find(parent);
		
		delegates.put(k,parent);
		
		return parent;
	}
	
	/*
	 * Invariant: if a group has a value, its representative has this value.
	 */
	public void unify(Key a, Key b) {
		ensure(a);
		ensure(b);
		final Key af = find(a);
		final Key bf = find(b);
		
		if (af == bf) return;

		if (values.containsKey(af))
			delegates.put(bf, af);
		else
			delegates.put(af, bf);
	}
	
	public void defineValue(Key k, Value v) {
		ensure(k);
		values.put(find(k), v);
	}
	
	public Value getValue(Key x) {
		ensure(x);
		final Key r = find(x);
		if (values.containsKey(r)) 
			return values.get(r);
		
		return defaultValue;
	}
}
