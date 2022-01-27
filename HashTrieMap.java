package datastructures.dictionaries;

import cse332.datastructures.containers.Item;
import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.trie.TrieMap;
import cse332.types.BString;
import java.util.AbstractMap.SimpleEntry;
//import java.util.HashMap;
import java.util.Iterator;
//import java.util.Map;
import java.util.Map.Entry;

/**
 * See cse332/interfaces/trie/TrieMap.java
 * and cse332/interfaces/misc/Dictionary.java
 * for method specifications.
 */
public class HashTrieMap<A extends Comparable<A>, K extends BString<A>, V> extends TrieMap<A, K, V> {
    public class HashTrieNode extends TrieNode<ChainingHashTable<A, HashTrieNode>, HashTrieNode> {
        public HashTrieNode() {
            this(null);
        }

        public HashTrieNode(V value) {
            this.pointers = new ChainingHashTable<>(AVLTree::new);
            this.value = value;
        }

        @Override
        public Iterator<Entry<A, HashTrieNode>> iterator() {
            return new Iterator<>() {
                Iterator<Item<A, HashTrieNode>> chainingHashIterator = pointers.iterator();
                @Override
                public boolean hasNext() {
                    return chainingHashIterator.hasNext();
                }
                @Override
                public Entry<A, HashTrieNode> next() {
                    Item<A, HashTrieNode> returnItem = chainingHashIterator.next();
                    return new SimpleEntry<>(returnItem.key, returnItem.value);
                }
            };
        }
    }

    public HashTrieMap(Class<K> KClass) {
        super(KClass);
        this.root = new HashTrieNode();
        this.size = 0;
    }
    // done

    @Override
    public V insert(K key, V value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode cur = (HashTrieNode) root;

        for (A character : key) {
            if (cur.pointers.find(character) == null) {
                cur.pointers.insert(character, new HashTrieNode());

            }
            cur = cur.pointers.find(character);
        }

        V res = cur.value;
        if (res == null) {
            size++;
        }
        cur.value = value;
        return res;
    }
    //done


    @Override
    public V find(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode cur = (HashTrieNode) root;
        for (A cha : key) {
            if (cur.pointers.find(cha) != null) {
                cur = cur.pointers.find(cha);
            }else {
                return null;
            }
        }
        return cur.value;
    }


    @Override
    public boolean findPrefix(K key) {
        HashTrieNode cur = (HashTrieNode) root;
        if(key == null) {
            throw new IllegalArgumentException();
        }
        if(this.size() == 0) {return false;}
        for(A cha : key) {
            if(cur.pointers.find(cha) == null) {
                return false;
            } else{
                cur = cur.pointers.find(cha);
            }
        }

        return true;
    }

@Override
public void delete(K key) {
    throw new UnsupportedOperationException();
}


    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
