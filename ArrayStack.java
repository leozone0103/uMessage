package datastructures.worklists;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.LIFOWorkList;
import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/LIFOWorkList.java
 * for method specifications.
 */
public class ArrayStack<E> extends LIFOWorkList<E> {

    int capacity = 0;
    int len = 0;
    E[] array = null;

    public ArrayStack() {
        this.capacity = 10;
        this.len = 0;
        this.array = (E[]) new Object[this.capacity];
    }

    @Override
    public void add(E work) {
        if (this.len != this.capacity) {
            this.array[this.len++] = work;
        } else {
            this.capacity *= 2;
            E[] newArray = (E[]) new Object[this.capacity];
            for (int cnt = 0; cnt < this.capacity / 2; cnt++) {
                newArray[cnt] = this.array[cnt];
            }
            newArray[this.len++] = work;
            this.array = newArray;
        }
    }

    @Override
    public E peek() {
        if (!this.hasWork()) {
            throw new NoSuchElementException();
        }
        return this.array[this.len - 1];
    }

    @Override
    public E next() {
        if (!this.hasWork()) {
            throw new NoSuchElementException();
        }
        E last = this.array[--this.len];
        this.array[this.len] = null;
        return last;
    }

    @Override
    public int size() {
        return this.len;
    }

    @Override
    public void clear() {
        this.capacity = 10;
        this.array = (E[]) new Object[this.capacity];
        this.len = 0;
    }
}
