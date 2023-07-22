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
       System.arraycopy(items, nextFirst +1 , newArray, 0, items.length - nextFirst - 1);
        System.arraycopy(items, 0 , newArray, items.length - nextFirst - 1, nextLsat);
       items = newArray;
       nextFirst = items.length;
       nextLsat = size;
    }
    public void addFirst(T item) {
        if(size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        size += 1;
        if(nextFirst == 0) {
            nextFirst = items.length - 1;
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
        if(nextLsat == items.length - 1) {
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
    public T removeFirst() {
        if(isEmpty()) {
            return null;
        }
        T first;
        if(nextFirst == items.length) {
            nextFirst = 0;
            first = items[nextFirst];
            items[nextFirst] = null;
        } else {
            nextFirst += 1;
            first = items[nextFirst];
            items[nextFirst] = null;
        }
        size -= 1;
        return first;
    }
    public T removeLast() {
        if(isEmpty()) {
            return null;
        }
        T last;
        if(nextLsat == 0) {
            nextLsat = items.length;
            last = items[nextLsat];
            items[nextLsat] = null;
        } else {
            nextLsat -= 1;
            last = items[nextLsat];
            items[nextLsat] = null;
        }
        size -= 1;
        return  last;
    }

    //  0 1 2 3 4 5 6 7
    //  0     n 0 0 0 0
    public T get(int index) {
        if(index > size - 1) {
            return null;
        }
        if(nextFirst + index + 1 >= items.length) {
            return items[nextFirst + index + 1 - items.length];
        } else {
            return items[nextFirst + index + 1];
        }
    }
}
