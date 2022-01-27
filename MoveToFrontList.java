package datastructures.dictionaries;

import cse332.datastructures.containers.Item;
import cse332.interfaces.misc.DeletelessDictionary;
import cse332.interfaces.misc.SimpleIterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 1. The list is typically not sorted.
 * 2. Add new items to the front of the list.
 * 3. Whenever find is called on an item, move it to the front of the
 * list. This means you remove the node from its current position
 * and make it the first node in the list.
 * 4. You need to implement an iterator. The iterator SHOULD NOT move
 * elements to the front.  The iterator should return elements in
 * the order they are stored in the list, starting with the first
 * element in the list. When implementing your iterator, you should
 * NOT copy every item to another dictionary/list and return that
 * dictionary/list's iterator.
 */
public class MoveToFrontList<K, V> extends DeletelessDictionary<K, V> {
    public FrontNode head;


    private class FrontNode {
        K key;
        V value;
        FrontNode next;
        FrontNode prev;
        private FrontNode(K key, V value) {
            this.key = key;
            this.value = value;
            next = null;
            prev = null;

        }
        public V getValue() {
            return this.value;
        }
        public K getKey() {
            return this.key;
        }
    }
    public MoveToFrontList() {
        size = 0;
        head = null;
    }
    @Override
    public V insert(K key, V value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }
        if(head == null) {
            head = new FrontNode(key, value);
            size++;
            return null;
        } else {
            V val = find(key);
            if(val == null) {
                FrontNode temp = new FrontNode(key,value);
                temp.next = head;
                head.prev = temp;
                head = temp;
                size++;

                return null;
            } else {

                head.value = value;
                return val;
            }
        }

    }

    @Override
    public V find(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        if(head == null) {
            return null;
        }
        FrontNode cur = head;
        if(cur.key.equals(key)) {
            return cur.value;
        }
        cur = cur.next;
        while(cur != null) {
            if(cur.key.equals(key) && cur.next != null) {
                V res = cur.value;
                cur.prev.next = cur.next;
                cur.next.prev = cur.prev;
                cur.prev = null;
                cur.next = head;
                head.prev = cur;
                head = head.prev;
                return res;
            } else if(cur.key.equals(key) && cur.next == null){
                V res = cur.value;
                cur.prev.next = null;
                cur.prev = null;
                cur.next = head;
                head.prev = cur;
                head = cur;
                return res;
            }
            cur = cur.next;
        }
        return null;
    }

    public boolean hasWork() {
        return this.size > 0;
    }



    @Override
    public Iterator<Item<K, V>> iterator() {
        return new MTFLIterator();
    }

    private class MTFLIterator extends SimpleIterator<Item<K, V>> {

        private FrontNode current;


        public MTFLIterator() {
            if(head != null) {
                current = head;
            }

        }

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public Item<K,V> next() {
            if(hasNext()) {
                K theKey = current.key;
                V theVal = current.value;
                current = current.next;
                return new Item<>(theKey, theVal);
            }
            return null;
        }


    }

    @Override
    public String toString() {
        Item<K, V>[] dcs = (Item<K, V>[]) new Item[this.size()];
        int i = 0;
        for (Item<K, V> item : this) {
            dcs[i++] = item;
        }
        return Arrays.toString(dcs);
    }

}
