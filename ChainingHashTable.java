package datastructures.dictionaries;

import cse332.datastructures.containers.Item;
import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.misc.DeletelessDictionary;
import cse332.interfaces.misc.Dictionary;
import cse332.interfaces.misc.SimpleIterator;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * 1. You must implement a generic chaining hashtable. You may not
 * restrict the size of the input domain (i.e., it must accept
 * any key) or the number of inputs (i.e., it must grow as necessary).
 * 3. Your HashTable should rehash as appropriate (use load factor as
 * shown in class!).
 * 5. HashTable should be able to resize its capacity to prime numbers for more
 * than 200,000 elements. After more than 200,000 elements, it should
 * continue to resize using some other mechanism.
 * 6. We suggest you hard code some prime numbers. You can use this
 * list: http://primes.utm.edu/lists/small/100000.txt
 * NOTE: Do NOT copy the whole list!
 * 7. When implementing your iterator, you should NOT copy every item to another
 * dictionary/list and return that dictionary/list's iterator.
 */
public class ChainingHashTable<K, V> extends DeletelessDictionary<K, V> {
    private Supplier<Dictionary<K, V>> newChain;
    private Dictionary<K,V>[] table;
    private int primeNumbersIndex;
    private int tableLength;

    private static final int[] primeNumbers = new int[] {2, 7, 19, 103, 311, 691, 1321, 2309, 3671, 5519, 7919, 10957, 14753, 19403, 24809, 31319, 38873, 47657, 57559, 69031, 81799, 96137, 112291, 130073, 149717, 171529, 195043};
    private static final int largestPrimeNumber = 211403;
    private static final int loadFactorThreshold = 2;

    public ChainingHashTable(Supplier<Dictionary<K, V>> newChain) {
        this.newChain = newChain;
        this.primeNumbersIndex = 0;
        this.size = 0;
        this.tableLength = primeNumbers[this.primeNumbersIndex];
        this.table = new Dictionary[this.tableLength];
        initialize(this.table);
    }

    private void initialize(Dictionary<K,V>[] table) {
        for (int i = 0; i < table.length; i++) {
            table[i] = this.newChain.get();
        }
    }

    private double loadFactor() {
        return this.size / (double) this.table.length;
    }

    private double newLoadFactor() {
        return (this.size + 1.0) / this.table.length;
    }

    private void migrate(Dictionary<K,V>[] oldTable, Dictionary<K,V>[] newTable) {
        for (Dictionary<K,V> dict : oldTable) {
            for (Item<K, V> item : dict) {
                int index = Math.abs(item.key.hashCode() % this.tableLength);
                assert(newTable[index] != null);
                newTable[index].insert(item.key, item.value);
            }
        }
    }

    private int findNextTableLength() {
        if (this.primeNumbersIndex < primeNumbers.length - 1) {
            this.primeNumbersIndex++;
            return primeNumbers[this.primeNumbersIndex];
        } else if (this.primeNumbersIndex == primeNumbers.length - 1) {
            this.primeNumbersIndex++;
            return largestPrimeNumber;
        } else {
            return this.tableLength + largestPrimeNumber;
        }
    }

    @Override
    public V insert(K key, V value) {
//        throw new NotYetImplementedException();
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }
        if (this.newLoadFactor() > loadFactorThreshold) {
            this.tableLength = findNextTableLength();
            Dictionary<K,V>[] newTable = (Dictionary<K,V>[]) new Dictionary[this.tableLength];
            initialize(newTable);
            migrate(this.table, newTable);
            this.table = newTable;
        }
        int index = Math.abs(key.hashCode() % this.tableLength);
        V forReturn = this.table[index].find(key);
        if (forReturn == null) {
            size++;
        }
        this.table[index].insert(key, value);
        assert(this.table.length == this.tableLength);
        return forReturn;
    }

    @Override
    public V find(K key) {
//        throw new NotYetImplementedException();
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int index = Math.abs(key.hashCode() % this.tableLength);
        return this.table[index].find(key);
    }

    @Override
    public Iterator<Item<K, V>> iterator() {
//        throw new NotYetImplementedException();
        return new ChainingHashTableIterator();
    }

    private class ChainingHashTableIterator extends SimpleIterator<Item<K, V>> {
        private int tableIndex;
        private Iterator<Item<K,V>> dictIterator;

        public ChainingHashTableIterator() {
            this.tableIndex = 0;
            dictIterator = ChainingHashTable.this.table[this.tableIndex].iterator();
            setFieldsToNext();
        }

        private void setFieldsToNext() {
            while (this.tableIndex < ChainingHashTable.this.table.length && !dictIterator.hasNext()) {
                this.tableIndex++;
                dictIterator = this.tableIndex < ChainingHashTable.this.table.length ?
                        ChainingHashTable.this.table[this.tableIndex].iterator() : null;
            }
        }

        @Override
        public boolean hasNext() {
            return this.tableIndex < ChainingHashTable.this.table.length;
        }

        @Override
        public Item<K, V> next() {
            Item<K, V> forReturn = dictIterator.next();
            setFieldsToNext();
            return forReturn;
        }
    }
}
