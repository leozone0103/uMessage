package datastructures.dictionaries;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.trie.TrieMap;
import cse332.types.BString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class CompressedHashTrieMap<A extends Comparable<A>, K extends BString<A>, V> extends TrieMap<A, K, V> {
    public class CompressedHashTrieNode extends TrieNode<Map<A, CompressedHashTrieNode>, CompressedHashTrieNode> {

        private List<A> nodeKey = null;

        public CompressedHashTrieNode() {
            this(null);
        }

        public CompressedHashTrieNode(V value) {
            this.nodeKey = new ArrayList<>();
            this.pointers = new HashMap<A, CompressedHashTrieNode>();
            this.value = value;
        }

        @Override
        public Iterator<Map.Entry<A, CompressedHashTrieNode>> iterator() {
            return this.pointers.entrySet().iterator();
        }
    }

    public CompressedHashTrieMap(Class<K> KClass) {
        super(KClass);
        this.root = new CompressedHashTrieNode();
    }

    @Override
    public V insert(K key, V value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }

        List<A> keyList = new ArrayList<>();
        for(A character: key) {
            keyList.add(character);
        }
        V forReturn = insert(keyList, (CompressedHashTrieNode) root, 0, keyList.size(), value);
//        System.out.println(root);
        return forReturn;
    }

    private V insert(List<A> searchKey, CompressedHashTrieNode current, int currentSearchKeyIndex, int searchKeyLength, V value) {
        int sameLength = findSameLength(searchKey, currentSearchKeyIndex, searchKeyLength, current.nodeKey);
        if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength == current.nodeKey.size()) {
            V res = current.value;
            current.value = value;
            if (res == null) {
                this.size++;
            }
            return res;
        } else if (currentSearchKeyIndex + sameLength < searchKeyLength && sameLength == current.nodeKey.size()) {
            if (current.pointers.containsKey(searchKey.get(currentSearchKeyIndex + sameLength))) {
                return insert(searchKey,
                        current.pointers.get(searchKey.get(currentSearchKeyIndex + sameLength)),
                        currentSearchKeyIndex + sameLength, searchKeyLength, value);
            } else {
                CompressedHashTrieNode newNode = new CompressedHashTrieNode();
                newNode.value = value;
                List<A> newNodeKey = new ArrayList<>();
                newNodeKey.addAll(searchKey.subList(currentSearchKeyIndex + sameLength, searchKeyLength));
                newNode.nodeKey = newNodeKey;
                current.pointers.put(searchKey.get(currentSearchKeyIndex + sameLength), newNode);
                this.size++;
                return null;
            }
        } else if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength < current.nodeKey.size()) {
            CompressedHashTrieNode newNode = new CompressedHashTrieNode();
            newNode.value = current.value;
            newNode.pointers = current.pointers;
            newNode.nodeKey = current.nodeKey.subList(sameLength, current.nodeKey.size());
            current.value = value;
            current.pointers = new HashMap<>();
            current.pointers.put(current.nodeKey.get(sameLength),newNode);
            current.nodeKey = current.nodeKey.subList(0,sameLength);
            this.size++;
            return null;
        } else {
            CompressedHashTrieNode newNodeForExistingValues = new CompressedHashTrieNode();
            newNodeForExistingValues.value = current.value;
            newNodeForExistingValues.pointers = current.pointers;
            newNodeForExistingValues.nodeKey = current.nodeKey.subList(sameLength, current.nodeKey.size());
            CompressedHashTrieNode newNodeForNewValue = new CompressedHashTrieNode(value);
            newNodeForNewValue.nodeKey = searchKey.subList(currentSearchKeyIndex + sameLength, searchKeyLength);
            current.pointers = new HashMap<>();
            current.pointers.put(current.nodeKey.get(sameLength),newNodeForExistingValues);
            current.pointers.put(searchKey.get(currentSearchKeyIndex + sameLength),newNodeForNewValue);
            current.value = null;
            current.nodeKey = current.nodeKey.subList(0,sameLength);
            this.size++;
            return null;
        }
    }

    private int findSameLength(List<A> searchKey, int currentSearchKeyIndex, int searchKeyLength, List<A> nodeKey) {
        int currentNodeKeyIndex = 0;
        while (currentNodeKeyIndex < nodeKey.size() && currentSearchKeyIndex < searchKeyLength &&
                searchKey.get(currentSearchKeyIndex) == nodeKey.get(currentNodeKeyIndex)) {
            currentNodeKeyIndex++;
            currentSearchKeyIndex++;
        }
        return currentNodeKeyIndex;
    }

    @Override
    public V find(K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }

        List<A> keyList = new ArrayList<>();
        for(A character: key) {
            keyList.add(character);
        }
//        System.out.println(root);
        return find(keyList, (CompressedHashTrieNode) root, 0, keyList.size());
    }

    private V find(List<A> searchKey, CompressedHashTrieNode current, int currentSearchKeyIndex, int searchKeyLength) {
        int sameLength = findSameLength(searchKey, currentSearchKeyIndex, searchKeyLength, current.nodeKey);
        if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength == current.nodeKey.size()) {
            return current.value;
        } else if (currentSearchKeyIndex + sameLength < searchKeyLength && sameLength == current.nodeKey.size()) {
            if (current.pointers.containsKey(searchKey.get(currentSearchKeyIndex + sameLength))) {
                return find(searchKey,
                        current.pointers.get(searchKey.get(currentSearchKeyIndex + sameLength)),
                        currentSearchKeyIndex + sameLength, searchKeyLength);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean findPrefix(K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }

        List<A> keyList = new ArrayList<>();
        for(A character: key) {
            keyList.add(character);
        }
//        System.out.println(root);
        return findPrefix(keyList, (CompressedHashTrieNode) root, 0, keyList.size());
    }

    private boolean findPrefix(List<A> searchKey, CompressedHashTrieNode current, int currentSearchKeyIndex, int searchKeyLength) {
        int sameLength = findSameLength(searchKey, currentSearchKeyIndex, searchKeyLength, current.nodeKey);
        if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength == current.nodeKey.size()) {
            return true;
        } else if (currentSearchKeyIndex + sameLength < searchKeyLength && sameLength == current.nodeKey.size()) {
            if (current.pointers.containsKey(searchKey.get(currentSearchKeyIndex + sameLength))) {
                return findPrefix(searchKey,
                        current.pointers.get(searchKey.get(currentSearchKeyIndex + sameLength)),
                        currentSearchKeyIndex + sameLength, searchKeyLength);
            } else {
                return false;
            }
        } else if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength < current.nodeKey.size()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void delete(K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        List<A> keyList = new ArrayList<>();
        for(A character: key) {
            keyList.add(character);
        }
        root = delete(keyList, (CompressedHashTrieNode) root, 0, keyList.size());
//        System.out.println(root);
        if (root == null) {
            root = new CompressedHashTrieNode();
        }
    }

    private CompressedHashTrieNode delete(List<A> searchKey, CompressedHashTrieNode current, int currentSearchKeyIndex, int searchKeyLength) {
        int sameLength = findSameLength(searchKey, currentSearchKeyIndex, searchKeyLength, current.nodeKey);
        if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength == current.nodeKey.size()) {
            if (current.value != null) {
                current.value = null;
                this.size--;
            }
//            if (current.pointers.isEmpty()) {
//                return null;
//            } else {
//                return current;
//            }
            if (current.pointers.isEmpty()) {
                return null;
            } else if (current.pointers.size() == 1) {
                CompressedHashTrieNode nextNode = null;
                for (A onlyKey : current.pointers.keySet()) {
                    nextNode = current.pointers.get(onlyKey);
                    current.nodeKey.addAll(nextNode.nodeKey);
                    nextNode.nodeKey = current.nodeKey;
                }
                return nextNode;
            } else {
                return current;
            }
        } else if (currentSearchKeyIndex + sameLength < searchKeyLength && sameLength == current.nodeKey.size()) {
            if (current.pointers.containsKey(searchKey.get(currentSearchKeyIndex + sameLength))) {
                CompressedHashTrieNode next = delete(searchKey,
                        current.pointers.get(searchKey.get(currentSearchKeyIndex + sameLength)),
                        currentSearchKeyIndex + sameLength, searchKeyLength);
                if (next == null) {
                    current.pointers.remove(searchKey.get(currentSearchKeyIndex + sameLength));
//                    if (current.pointers.isEmpty()) {
//                        return null;
//                    } else {
//                        return current;
//                    }
                    if (current.pointers.isEmpty() && current.value == null) {
                        return null;
                    } else if (current.pointers.size() == 1 && current.value == null) {
                        CompressedHashTrieNode nextNode = null;
                        for (A onlyKey : current.pointers.keySet()) {
                            nextNode = current.pointers.get(onlyKey);
                            current.nodeKey.addAll(nextNode.nodeKey);
                            nextNode.nodeKey = current.nodeKey;
                        }
                        return nextNode;
                    } else {
                        return current;
                    }
                } else {
                    current.pointers.put(searchKey.get(currentSearchKeyIndex + sameLength), next);
                    return current;
                }
            } else {
                return current;
            }
        } else if (currentSearchKeyIndex + sameLength == searchKeyLength && sameLength < current.nodeKey.size()) {
            return current;
        } else {
            return current;
        }
    }

    @Override
    public void clear() {
        CompressedHashTrieNode temp = (CompressedHashTrieNode) root;
        this.size = 0;
        temp.pointers.clear();
        temp.value = null;
    }
}
