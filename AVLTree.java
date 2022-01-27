package datastructures.dictionaries;

import cse332.datastructures.trees.BinarySearchTree;
import cse332.interfaces.worklists.WorkList;
import datastructures.worklists.ArrayStack;
import datastructures.worklists.ListFIFOQueue;

/**
 * AVLTree must be a subclass of BinarySearchTree<E> and must use
 * inheritance and calls to superclass methods to avoid unnecessary
 * duplication or copying of functionality.
 * <p>
 * 1. Create a subclass of BSTNode, perhaps named AVLNode.
 * 2. Override the insert method such that it creates AVLNode instances
 * instead of BSTNode instances.
 * 3. Do NOT "replace" the children array in BSTNode with a new
 * children array or left and right fields in AVLNode.  This will
 * instead mask the super-class fields (i.e., the resulting node
 * would actually have multiple copies of the node fields, with
 * code accessing one pair or the other depending on the type of
 * the references used to access the instance).  Such masking will
 * lead to highly perplexing and erroneous behavior. Instead,
 * continue using the existing BSTNode children array.
 * 4. If this class has redundant methods, your score will be heavily
 * penalized.
 * 5. Cast children array to AVLNode whenever necessary in your
 * AVLTree. This will result a lot of casts, so we recommend you make
 * private methods that encapsulate those casts.
 * 6. Do NOT override the toString method. It is used for grading.
 */

public class AVLTree<K extends Comparable<? super K>, V> extends BinarySearchTree<K, V> {
    // TODO: Implement me!

    public class AVLNode extends BSTNode {

        int height;

//        @SuppressWarnings("unchecked")
        public AVLNode(K key, V value) {
            super(key, value);
            this.height = 0;
        }

        public AVLNode(K key, V value, int height) {
            super(key, value);
            this.height = height;
        }
    }

    public AVLTree() {
        super();
    }

    @Override
    public V insert(K key, V value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }

        WorkList<Object[]> path = findPath(key, value);
        AVLNode target = (AVLNode) path.peek()[0];
        V returnValue = target.value;
        target.value = value;
//        assert(VerifyAVL.checkHeight(root) && VerifyAVL.checkBST(root));

        if (path instanceof ListFIFOQueue) {
            this.root = reBalance((ListFIFOQueue<Object[]>) path);
        }

//        assert(VerifyAVL.verifyAVL((AVLNode) this.root));
        return returnValue;
    }

    private AVLNode reBalance(ListFIFOQueue<Object[]> path) {
        AVLNode current = (AVLNode) path.next()[0], desiredChild;
        int child = -1, pastChild;
        int[] childrenHeights;
        while (path.hasWork()) {
            pastChild = child;
            child = (Integer) path.peek()[1];
            current = (AVLNode) path.next()[0];
            childrenHeights = getChildrenHeights(current.children);
            current.height = currentHeight(current);

            if (Math.abs(childrenHeights[1] - childrenHeights[0]) == 2) {
                desiredChild = (AVLNode) current.children[pastChild];
                boolean secondDirection = childrenHeights[1] > childrenHeights[0];
                childrenHeights = getChildrenHeights(desiredChild.children);
                boolean firstDirection = childrenHeights[1] > childrenHeights[0];
                current = rotate(current, firstDirection, secondDirection);
            }
//            assert(VerifyAVL.verifyAVL(current));
//            assert(VerifyAVL.checkHeight(current));
//            assert(VerifyAVL.checkBST(current));
//            assert(VerifyAVL.checkAVL(current));
            if (path.hasWork()) {
                ((AVLNode) path.peek()[0]).children[child] = current;
            }
        }
        return current;
    }

    private AVLNode rotate(AVLNode current, boolean firstDirection, boolean secondDirection) {
        if (firstDirection && secondDirection) {
            return rRRotate(current);
        } else if (firstDirection) {
            return rLRotate(current);
        } else if (secondDirection) {
            return lRRotate(current);
        } else {
            return lLRotate(current);
        }
    }

    private AVLNode lLRotate(AVLNode current) {
        AVLNode right = current;
        AVLNode middle = (AVLNode) current.children[0];
//        AVLNode left = (AVLNode) middle.children[0];
        right.children[0] = middle.children[1];
        middle.children[1] = right;
        right.height = currentHeight(right);
        middle.height = currentHeight(middle);

//        assert(VerifyAVL.checkHeight(current));
        return middle;
    }

    private AVLNode rRRotate(AVLNode current) {
        AVLNode left = current;
        AVLNode middle = (AVLNode) current.children[1];
//        AVLNode right = (AVLNode) middle.children[1];
        left.children[1] = middle.children[0];
        middle.children[0] = left;
        left.height = currentHeight(left);
        middle.height = currentHeight(middle);

//        assert(VerifyAVL.checkHeight(current));
        return middle;
    }

    private AVLNode rLRotate(AVLNode current) {
        return contraRotate((AVLNode) current.children[0], (AVLNode) current.children[0].children[1], current);
    }

    private AVLNode lRRotate(AVLNode current) {
        return contraRotate(current, (AVLNode) current.children[1].children[0], (AVLNode) current.children[1]);
    }

    private AVLNode contraRotate(AVLNode left, AVLNode middle, AVLNode right) {
        left.children[1] = middle.children[0];
        right.children[0] = middle.children[1];
        middle.children[0] = left;
        middle.children[1] = right;

        left.height = currentHeight(left);
        right.height = currentHeight(right);
        middle.height = currentHeight(middle);

//        assert(VerifyAVL.checkHeight(current));
        return middle;
    }

    private int currentHeight(AVLNode node) {
        int[] childrenHeights = getChildrenHeights(node.children);
        return Math.max(childrenHeights[0],childrenHeights[1]) + 1;
    }

    private int[] getChildrenHeights(BSTNode[] node) {
        return new int[] {
                node[0] == null ? -1 : ((AVLNode) node[0]).height,
                node[1] == null ? -1 : ((AVLNode) node[1]).height
        };
    }

    private WorkList<Object[]> findPath(K key, V value) {
        // [node, -1], [node, direction], ..., [node, direction]
        ArrayStack<Object[]> path = new ArrayStack<>();
        int child = -1;
        AVLNode current = (AVLNode) this.root;
        path.add(new Object[] {current, child});

        while (current != null) {
            int direction = Integer.signum(key.compareTo(current.key));
            if (direction == 0) {
                return path;
            } else {
                // direction + 1 = {0, 2} -> {0, 1}
                child = (direction + 1) / 2;
                current = (AVLNode) current.children[child];
                path.add(new Object[] {current, child});
            }
        }

        Object[] temp = path.next();
//        assert(temp[0] == null);
        current = new AVLNode(key, value);
        if (this.root == null) {
            this.root = current;
        } else {
            assert(child >= 0);
            ((AVLNode) path.peek()[0]).children[child] = current;
        }
        this.size++;

        int height = 0;
        ListFIFOQueue<Object[]> newPath = new ListFIFOQueue<>();
        newPath.add(new Object[] {current, child});
        while (path.hasWork()) {
            child = (Integer) path.peek()[1];
            current = ((AVLNode) path.next()[0]);
            height++;
            if (current.height < height) {
                current.height = height;
            }
            newPath.add(new Object[] {current, child});
        }

//        assert(VerifyAVL.checkHeight((AVLNode) this.root));
        return newPath;
    }
}
