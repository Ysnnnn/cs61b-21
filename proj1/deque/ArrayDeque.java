package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLsat;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLsat = 1;
    }
    public void resize(int capacity) {
       T[] newArray = (T []) new Object[capacity];
       System.arraycopy(items, 0, newArray, 5, size);
       items = newArray;
    }
    public void addFirst(T item) {
        if(size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        if(nextFirst == 0) {
            nextFirst = items.length;
        } else {
            nextFirst -= 1;
        }
    }
    public void addLast(T item) {
        if(size == items.length) {
            resize(size * 2);
        }
        items[nextLsat] = item;
        size += 1;
        if(nextLsat == items.length) {
            nextLsat = 0;
        } else  {
            nextLsat += 1;
        }
    }
    public boolean isEmpty() {
        return (size == 0);
    }
    public int size() {
        return size;
    }
    public void printDeque() {
        int p;
        if(nextFirst == items.length) {
            p = 0;
        } else {
            p = nextFirst + 1;
        }
        for(; p == nextLsat; p += 1) {
            System.out.print(p + " ");
            if(p == items.length) {
                p = 0;
            } else {
                p += 1;
            }
        }
    }
}
