import java.util.Iterator;
import java.util.List;

public interface DictionaryInterface<K,V extends Document> {

	/**
	 * Adds a new entry to this dictionary. If the given search key already exists
	 * in the dictionary, replaces the corresponding value.
	 * 
	 * @param key   an object search key of the new entry
	 * @param value an object associated with the search key
	 * @param whichHashFunction a string that keep hash function type. SSF->Simple Summation Function, PAF->Polynomial Accumulation Function
	 * @param whichCollisionHandling a string that keep collision handling type. LP-> Linear Probing, DH->Double Hashing
	 * @return either null if the new entry was added to the dictionary or the value
	 *         that was associated with key if that value was replaced
	 */
	public void add(K key, V value, String whichHashFunction,String whichCollisionHandling);

	/**
	 * Sees whether this dictionary is empty.
	 * 
	 * @return true if the dictionary is empty
	 */
	public boolean isEmpty();

	/**
	 * Gets the size of this dictionary.
	 * 
	 * @return the number of entries (key-value pairs) currently in the dictionary
	 */
	public int getSize();

	/**
	 * Creates an iterator that traverses all search keys in this dictionary.
	 *
	 * @return an iterator that provides sequential access to the search keys in the
	 *         dictionary
	 */
	public Iterator<K> getKeyIterator();

	/**
	 * Creates an iterator that traverses all values in this dictionary.
	 *
	 * @return an iterator that provides sequential access to the values in this
	 *         dictionary
	 */
	public Iterator<List<V>> getValueIterator();
}
