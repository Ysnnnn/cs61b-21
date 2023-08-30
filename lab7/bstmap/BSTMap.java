package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private int size = 0;
    private BSTNode root;
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (find(root, key) == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        BSTNode keyNode = find(root, key);
        if (keyNode == null) {
            return null;
        } else {
            return keyNode.val;
        }
    }
    private BSTNode find(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return find(node.leftSon, key);
        } else {
            return find(node.rightSon, key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
        size += 1;
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return  new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            node.rightSon = put(node.rightSon, key, value);
        } else if (cmp < 0 ) {
            node.leftSon = put(node.leftSon, key,value);
        }
        return node;
    }

    /** prints out your BSTMap in order of increasing Key.
     * 自己没写出来，在网上找的实现方法*/
    public void printInOrder() {
        printInOrder(root);
    }
    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.leftSon);
        System.out.println(node.key.toString() + "-->" + node.val.toString());
        printInOrder(node.rightSon);
    }
    private class BSTNode {
        K key;
        V val;
        BSTNode leftSon;
        BSTNode rightSon;
        BSTNode(K k, V v) {
            key = k;
            val = v;
        }
    }
    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        addKey(set, root);
        return set;
    }
    /** add all key to keySet */
    private void addKey(Set<K> set, BSTNode node) {
        if (node == null) {
            return;
        }
        set.add(node.key);
        addKey(set, node.leftSon);
        addKey(set, node.rightSon);
    }

    private BSTNode remove(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = node.key.compareTo(key);
        if (cmp > 0) {
            node.leftSon = remove(node.leftSon, key);
        } else if (cmp < 0) {
            node.rightSon = remove(node.rightSon, key);
        } else {
            BSTNode targetNode = node;
            node = findMin(targetNode);
            node.leftSon = targetNode.leftSon;
            node.rightSon = remove(targetNode.rightSon, node.key);
        }
        return node;
    }
    private BSTNode findMin(BSTNode node) {
        if (node.leftSon == null) {
            return node;
        }
        return findMin(node.leftSon);
    }
    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V val = get(key);
            root = remove(root, key);
            size -= 1;
            return val;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V val = get(key);
            if (val.equals(key)) {
                root = remove(root ,key);
                size -= 1;
                return val;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
