package datastructures.worklists;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.FixedSizeFIFOWorkList;
import java.lang.IllegalStateException;
import java.util.NoSuchElementException;
import java.lang.IndexOutOfBoundsException;

/**
 * See cse332/interfaces/worklists/FixedSizeFIFOWorkList.java
 * for method specifications.
 */
public class CircularArrayFIFOQueue<E extends Comparable<E>> extends FixedSizeFIFOWorkList<E> {

    private int first = -1;
    private int last = -1;
    private E[] array = null;

    private static final int hashPrime = 19;

    public CircularArrayFIFOQueue(int capacity) {
        super(capacity);
        this.array = (E[]) new Comparable[capacity];
    }

    @Override
    public void add(E work) {
        if (this.isFull()) {
            throw new IllegalStateException();
        }
        if (first == -1) {
            array[0] = work;
            first = 0;
            last = 1;
        } else {
            array[last] = work;
            last++;
        }
        if (last == capacity()) {
            last = 0;
        }
    }

    @Override
    public E peek() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        return array[first];
    }

    @Override
    public E peek(int i) {
        if (!hasWork()) {
            throw new NoSuchElementException();
        } else if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        if (first + i >= capacity()) {
            return array[first + i - capacity()];
        } else {
            return array[first + i];
        }
    }

    @Override
    public E next() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        E next = array[first];
        first++;
        if (first == capacity()) {
            first = 0;
        }
        if (first == last) {
            first = -1;
            last = -1;
        }
        return next;
    }

    @Override
    public void update(int i, E value) {
        if (!hasWork()) {
            throw new NoSuchElementException();
        } else if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        if (first + i >= capacity()) {
            array[first + i - capacity()] = value;
        } else {
            array[first + i] = value;
        }
    }

    @Override
    public int size() {
        if (first == -1) {
            return 0;
        } else if (last <= first) {
            return last + this.capacity() - first;
        } else {
            return last - first;
        }
    }

    @Override
    public void clear() {
        first = -1;
        last = -1;
    }

    @Override
    public int compareTo(FixedSizeFIFOWorkList<E> other) {
        for (int i = 0; i < Math.min(this.size(), other.size()); i++) {
            if(this.peek(i).compareTo(other.peek(i)) != 0) {
                return this.peek(i).compareTo(other.peek(i));
            }
        }
        if(this.size() > other.size()) {
            return 1;
        } else if(this.size() < other.size()) {
            return -1;
        } else {return 0;}

    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (!(obj instanceof FixedSizeFIFOWorkList<?>)) {
            return false;
        } else {
            FixedSizeFIFOWorkList<E> other = (FixedSizeFIFOWorkList<E>) obj;
            if(this.size() != other.size())
                return false;
            else {
                for(int i = 0; i < this.size(); i++) {
                    if(this.peek(i) != other.peek(i)) {
                        return false;
                    }
                }
                return true;
            }


        }
    }

    @Override
    public int hashCode() {
        // You will implement this method in project 2. Leave this method unchanged for project 1.
//        throw new NotYetImplementedException();
//        return 0;
        int num = 0;
        int coeff = 1;
        if (this.first < this.last) {
            for (int i = this.first; i < this.last; i++) {
                num += coeff * this.array[i].hashCode();
                coeff *= hashPrime;
            }
        } else if (this.first != -1) {
            for (int i = this.first; i < this.array.length; i++) {
                num += coeff * this.array[i].hashCode();
                coeff *= hashPrime;
            }
            for (int i = 0; i < this.last; i++) {
                num += coeff * this.array[i].hashCode();
                coeff *= hashPrime;
            }
        }
        return num;
    }
}
