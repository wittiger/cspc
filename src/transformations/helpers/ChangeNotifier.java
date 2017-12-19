package transformations.helpers;

public final class ChangeNotifier {
	private boolean set = false;
	
	public void set() {
		set = true;
	}
	
	public void reset() {
		set = false;
	}
	
	public boolean isSet() {
		return set;
	}
}
