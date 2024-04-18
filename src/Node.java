import java.util.List;

public class Node<K,V> {
	private K key;
	private List<V> value;
	private boolean inTable;

	public Node(K key, List<V> value) {
		this.key = key;
		this.value = value;
		this.inTable = true;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public List<V> getValue() {
		return value;
	}

	public void setValue(List<V> value) {
		this.value = value;
	}

	public boolean isRemoved() {
		return inTable == false;
	}

	public void setToRemoved() {
		inTable = false;
	}

	public void setToIn() {
		inTable = true;
	}

	public boolean isIn() {
		return inTable == true;
	}

}
