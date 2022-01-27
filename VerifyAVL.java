package datastructures.dictionaries;

public class VerifyAVL {
    public static boolean verifyAVL(AVLTree.AVLNode root) {
        return checkHeight(root) && checkBST(root) && checkAVL(root);
    }

    private static int getHeight(AVLTree.AVLNode root) {
        if (root == null) {
            return -1;
        } else {
            return Math.max(getHeight((AVLTree.AVLNode) root.children[0]),getHeight((AVLTree.AVLNode) root.children[1])) + 1;
        }
    }

    public static boolean checkHeight(AVLTree.AVLNode root) {
        if (root == null) {
            return true;
        } else {
            return checkHeight((AVLTree.AVLNode) root.children[0]) && checkHeight((AVLTree.AVLNode) root.children[1]) &&
                    root.height == Math.max(
                            root.children[0] == null ? -1 : ((AVLTree.AVLNode) root.children[0]).height,
                            root.children[1] == null ? -1 : ((AVLTree.AVLNode) root.children[1]).height
                    ) + 1;
        }
    }

    public static boolean checkBST(AVLTree.AVLNode root) {
        return root == null || checkBSTPrivate(root)[2] != null;
    }

    private static Object[] checkBSTPrivate(AVLTree.AVLNode root) {
        // [min, max, valid]
        if (root.children[0] == null && root.children[1] == null) {
            return new Object[] {root.key,root.key,root.key};
        } else if (root.children[0] == null) {
            Object[] tupleRight = checkBSTPrivate((AVLTree.AVLNode) root.children[1]);
            if (tupleRight[2] == null || ((Comparable) tupleRight[0]).compareTo(root.key) < 0) {
                return new Object[] {null, null, null};
            } else {
                return new Object[] {root.key, tupleRight[1], 1};
            }
        } else if (root.children[1] == null) {
            Object[] tupleLeft = checkBSTPrivate((AVLTree.AVLNode) root.children[0]);
            if (tupleLeft[2] == null || ((Comparable) tupleLeft[1]).compareTo(root.key) > 0) {
                return new Object[] {null, null, null};
            } else {
                return new Object[] {tupleLeft[0], root.key, 1};
            }
        } else {
            Object[] tupleLeft = checkBSTPrivate((AVLTree.AVLNode) root.children[0]);
            Object[] tupleRight = checkBSTPrivate((AVLTree.AVLNode) root.children[1]);
            if (tupleLeft[2] == null || tupleRight[2] == null ||
                    ((Comparable) tupleLeft[1]).compareTo(root.key) > 0 ||
                    ((Comparable) tupleRight[0]).compareTo(root.key) < 0) {
                return new Object[] {null, null, null};
            } else {
                return new Object[] {tupleLeft[0], tupleRight[1], 1};
            }
        }
    }

    public static boolean checkAVL(AVLTree.AVLNode root) {
        if (root == null) {
            return true;
        } else {
            int heightLeft = root.children[0] == null ? -1 : ((AVLTree.AVLNode) root.children[0]).height;
            int heightRight = root.children[1] == null ? -1 : ((AVLTree.AVLNode) root.children[1]).height;
            return (Math.abs(heightLeft - heightRight) <= 1 && checkAVL((AVLTree.AVLNode) root.children[0]) && checkAVL((AVLTree.AVLNode) root.children[1]));
        }
    }
}