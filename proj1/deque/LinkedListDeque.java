package deque;

import java.util.Iterator;

/** First part of project1.Deque implemented by Linked List
 * @author Ysnnnn
 */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    /** inner class Node*/
    public class Node {
        private Node prev;
        public T item;
        private Node next;
        public Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private final Node Sentinel;
    private  int size;
    public  LinkedListDeque() {
        Sentinel = new Node(null, null, null);
        Sentinel.prev = Sentinel;
        Sentinel.next = Sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        Node n = new Node(Sentinel, item, Sentinel.next);
        Sentinel.next.prev = n;
        Sentinel.next = n;
        size += 1;
    }
    @Override
    public void addLast(T item) {
        Node n = new Node(Sentinel.prev, item, Sentinel);
        Sentinel.prev.next = n;
        Sentinel.prev = n;
        size += 1;
    }
    @Override
    public int size() {
        return size;
    }

    /**Prints the items in the deque from first to last,
     * separated by a space. Once all the items have been printed, print out a new line.
      */
    @Override
    public void printDeque() {
        Node p = Sentinel.next;
        if(isEmpty()) {
            return;
        }
        while(p.next != Sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println(p.item);
    }
    @Override
    public T removeFirst() {
        if(!isEmpty()){
            T item = Sentinel.next.item;
            Sentinel.next = Sentinel.next.next;
            Sentinel.prev = Sentinel.prev.prev.next;
            size -= 1;
            return item;
        } else {
            return null;
        }
    }
    @Override
    public T removeLast() {
        if (!isEmpty()) {
            T item = Sentinel.prev.item;
            Sentinel.prev = Sentinel.prev.prev;
            Sentinel.next = Sentinel.next.next.prev;
            size -= 1;
            return item;
        } else {
            return null;
        }
    }
    @Override
    public T get(int index) {
        Node p = Sentinel.next;
        for(int i = 0; i < index; i += 1) {
            p = p.next;
        }
        return p.item;
    }
    /** same as get but use recursion,need a helper function */
    private T getRecursiveHelp(int index, Node current) {
        if(index == 0) {
            return current.item;
        }
        return getRecursiveHelp(index - 1, current.next);
    }

    public T getRecursive(int index) {
        return getRecursiveHelp(index, Sentinel.next);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    public class LinkedListDequeIterator implements Iterator<T> {
        private int current;
        public LinkedListDequeIterator() {
            current = 0;
        }
        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public T next() {
            T returnItem = get(current);
            current += 1;
            return returnItem;
        }
    }
    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        if(o.getClass() != this.getClass()) {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if(other.size != this.size) {
            return false;
        }
        for(int i = 0; i < size; size += 1) {
            if(this.get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }

}