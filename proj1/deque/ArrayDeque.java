package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>,Iterable<T>{
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
    private int addOne(int index) {
        return (index + 1) % items.length;
    }
    private void resize(int capacity) {
       T[] newArray = (T []) new Object[capacity];
        int index = addOne(nextFirst);
        for (int i = 0; i < size; i++) {
            newArray[i] = items[index];
            index = addOne(index);
        }
       nextFirst = capacity - 1;
       nextLsat = size;
       items = newArray;
    }
    @Override
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
    @Override
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
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        int p;
        if(nextFirst == items.length - 1) {
            p = 0;
        } else {
            p = nextFirst + 1;
        }
        while(p != nextLsat) {
            System.out.print(items[p] + " ");
            if(p == items.length) {
                p = 0;
            } else {
                p += 1;
            }
        }
    }
    @Override
    public T removeFirst() {
        if(isEmpty()) {
            return null;
        }
        T first;
        if(nextFirst == items.length - 1) {
            nextFirst = 0;
            first = items[nextFirst];
            items[nextFirst] = null;
        } else {
            nextFirst += 1;
            first = items[nextFirst];
            items[nextFirst] = null;
        }
        size -= 1;
        if (size < items.length / 4 && size > 8) {
            resize(size * 2);
        }
        return first;
    }
    @Override
    public T removeLast() {
        if(isEmpty()) {
            return null;
        }
        T last;
        if(nextLsat == 0) {
            nextLsat = items.length - 1;
            last = items[nextLsat];
            items[nextLsat] = null;
        } else {
            nextLsat -= 1;
            last = items[nextLsat];
            items[nextLsat] = null;
        }
        size -= 1;
        if (size < items.length / 4 && size > 8) {
            resize(size * 2);
        }
        return  last;
    }
    @Override
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
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T>{
        private int current;
        public ArrayDequeIterator() {
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
        if(!(o instanceof Deque)) {
            return false;
        }
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if(other.size != this.size) {
            return false;
        }
        int index = addOne(nextFirst);
        for (int i = 0; i < size; i++) {
            if (!(items[index].equals(other.get(i)))) {
                return false;
            }
            index = addOne(index);
        }
        return true;
    }

}
