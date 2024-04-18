import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class HashedDictionary<K,V extends Document> implements DictionaryInterface<K,V> {
	private Node<K,V>[] hashTable;
	private int numberOfEntries;
	private int locationsUsed;
	private static final int DEFAULT_SIZE = 2500;
	private static final double MAX_LOAD_FACTOR = 0.5;
	public static int COLLISION_COUNT = 0;

	public HashedDictionary() {
		this(DEFAULT_SIZE);
	}

	@SuppressWarnings("unchecked")
	public HashedDictionary(int tableSize) {
		int primeSize = getNextPrime(tableSize);
		hashTable = new Node[primeSize];
		numberOfEntries = 0;
		locationsUsed = 0;
	}

	public boolean isPrime(int num) {
		boolean prime = true;
		for (int i = 2; i <= num / 2; i++) {
			if ((num % i) == 0) {
				prime = false;
				break;
			}
		}
		return prime;
	}

	public int getNextPrime(int num) {
		if (num <= 1)
			return 2;
		else if (isPrime(num))
			return num;
		boolean found = false;
		while (!found) {
			num++;
			if (isPrime(num))
				found = true;
		}
		return num;
	}

	public void add(K key, V value, String whichHashFunction, String whichCollisionHandling) {
		Integer hashKey = 0;
		if (whichHashFunction.equals("SSF")) {
			hashKey = ssf(key.toString()); // Calculating key by ssf
		} else if (whichHashFunction.equals("PAF")){
			hashKey = paf(key.toString()); // Calculating key by paf
		}

		if (isHashTableTooFull()) {
			rehash(whichHashFunction, whichCollisionHandling);
		}
		int index = doubleHashing(hashKey,0);
		index = probe(index, hashKey, key, whichCollisionHandling);

		if ((hashTable[index] == null) || hashTable[index].isRemoved()) {
			List<V> documentList = new ArrayList<>();
			documentList.add(value);
			hashTable[index] = new Node(key, documentList);
			numberOfEntries++;
			locationsUsed++;
		} else {
			boolean isFound = false;
			for(Document document : hashTable[index].getValue()) {
				if (document.getDocumentName().equals(value.getDocumentName())) {
					document.setWordCount(document.getWordCount()+1);
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				hashTable[index].getValue().add(value);
			}
		}
	}

	public boolean isHashTableTooFull() {
		double load_factor = (double)locationsUsed / (double) hashTable.length;
		if (load_factor >= MAX_LOAD_FACTOR)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public void rehash(String whichHashFunction, String whichCollisionHandling) {
		Node[] oldTable = hashTable;
		int oldSize = hashTable.length;
		int newSize = getNextPrime(2 * oldSize);
		hashTable = new Node[newSize];
		numberOfEntries = 0;
		locationsUsed = 0;
		// rehash dictionary entries from old array to the new and bigger
		// array; skip both null locations and removed entries
		for (int index = 0; index < oldSize; index++) {
			// 2. SECTION ///////////////// ADD DICTIONARY ENTRIES TO THE NEW TABLE
			if ((oldTable[index] != null) && oldTable[index].isIn()) {
				for (Document document : (List<Document>) oldTable[index].getValue()) {
					add((K) oldTable[index].getKey(), (V) document,whichHashFunction, whichCollisionHandling);
				}
			}
		}
	}

	private int probe(int index, int hashKey, K key, String whichCollisionHandling) {
		boolean found = false;
		int removedStateIndex = -1; // Index of first location in removed state
		int increment = 1;
		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].isIn()) {
				if (key.equals(hashTable[index].getKey())) {
					found = true; // Key found
				} else {
					COLLISION_COUNT++;
					if (whichCollisionHandling.equals("LP")) {
						index = (index + 1) % hashTable.length; // Linear probing
					} else if (whichCollisionHandling.equals("DH")){
						// Double Hashing
						index = doubleHashing(hashKey, increment);
						increment++;
					}
				}
			}
		} // end while
			// Assertion: Either key or null is found at hashTable[index]
		if (found || (removedStateIndex == -1))
			return index; // Index of either key or null
		else
			return removedStateIndex; // Index of an available location
	}

	private int locate(int index, int hashKey, K key, String whichCollisionHandling) {
		boolean found = false;
		int increment = 1;
		while (!found && (hashTable[index] != null)) {
			//////// Section 1 - Find the index of the searched key in the probe sequence
			//////// ///////
			if (hashTable[index].isIn() && key.equals(hashTable[index].getKey())) {
				found = true;
			} else {
				if (whichCollisionHandling.equals("LP")) {
					index = (index + 1) % hashTable.length; // Linear probing
				} else if (whichCollisionHandling.equals("DH")){
					// Double Hashing
					index = doubleHashing(hashKey, increment);
					increment++;

				}
				
			}
		}
		int result = -1;
		if (found)
			result = index;
		return result;
	}

	public List<List<Document>> search(String searchWord) {
		if (searchWord == null || searchWord.equals("")) {
			return new ArrayList<>();
		}

		String[] splittedWords = searchWord.split(" ");
		List<List<Document>> resultList = new ArrayList<>();
		for (String splittedWord : splittedWords) {
			Integer hashKey = paf(splittedWord); //Must be same with initialize function (in Management class)

			int index = doubleHashing(hashKey, 0);
			index = locate(index, hashKey, (K) splittedWord, "DH"); //Must be same with initialize function (in Management class)
			if (index != -1) {
				Collections.sort((List<Document>) hashTable[index].getValue());
				resultList.add((List<Document>) hashTable[index].getValue());
				System.out.println("Found : " + hashTable[index].getKey());
			}
		}
		return resultList;
	}

	public boolean isEmpty() {
		return numberOfEntries == 0;
	}

	public int getSize() {
		return numberOfEntries;
	}

	public Iterator<K> getKeyIterator() {
		return new KeyIterator();
	}

	public Iterator<List<V>> getValueIterator() {
		return new ValueIterator();
	}

	private class KeyIterator implements Iterator<K> {
		private int currentIndex;
		private int numberLeft;

		private KeyIterator() {
			currentIndex = 0;
			numberLeft = numberOfEntries;
		}

		public boolean hasNext() {
			return numberLeft > 0;
		}

		public K next() {
			K result = null;
			if (hasNext()) {
				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {
					currentIndex++;
				}
				result = hashTable[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			} else
				throw new NoSuchElementException();
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class ValueIterator implements Iterator<List<V>> {
		private int currentIndex;
		private int numberLeft;

		private ValueIterator() {
			currentIndex = 0;
			numberLeft = numberOfEntries;
		}

		public boolean hasNext() {
			return numberLeft > 0;
		}

		public List<V> next() {
			List<V> result = new ArrayList<>();
			if (hasNext()) {
				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {
					currentIndex++;
				}
				result = hashTable[currentIndex].getValue();
				numberLeft--;
				currentIndex++;
			} else
				throw new NoSuchElementException();
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public  int ssf(String word) {
    	int key =0;
    	for (int i = 0; i < word.length(); i++) {
    		key += word.charAt(i);
        }
    	return key;
	}
	public int paf(String word) {
		int key =0;
		int power = word.length()-1;
		for (int i = 0; i < word.length(); i++) {
			key += word.charAt(i) * Math.pow(31,power);
			power--;
		}
		return key;
	}

	private int doubleHashing(Integer hashKey, int increment) {
		int hashIndex = (getHashIndex(hashKey) + (increment * (31 - hashKey % 31))) % hashTable.length;
		return hashIndex;
	}

	private int getHashIndex(Integer hashKey) {
		return (2477 - hashKey % 2477) % hashTable.length;
	}
}
