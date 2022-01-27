package datastructures.worklists;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.WorkList;

import java.util.NoSuchElementException;
import java.util.Random;

public class RandomizedWorkList<E> extends WorkList<E> {

    int capacity = 0;
    int len = 0;
    E[] array = null;
    Random rand = null;
    boolean addsDone = false;

    public RandomizedWorkList(int capacity) {
        this.capacity = capacity;
        this.len = 0;
        this.array = (E[]) new Object[this.capacity];
        this.rand = new Random();
    }

    @Override
    public void add(E work) {
        if (addsDone) {
            throw new IllegalStateException();
        }
        if (this.len != this.capacity) {
            this.array[this.len++] = work;
        } else {
            this.array[rand.nextInt(this.len)] = work;
        }
    }

    @Override
    public E peek() {
        if (!this.hasWork()) {
            throw new NoSuchElementException();
        }
        return this.array[rand.nextInt(this.len)];
    }

    @Override
    public E next() {
        if (!this.hasWork()) {
            throw new NoSuchElementException();
        }
        int index = rand.nextInt(this.len);
        E forReturn = this.array[index];
        for (int i = index + 1; i < len; i++) {
            this.array[i-1] = this.array[i];
        }
        len--;
        addsDone = true;
        return forReturn;
    }

    @Override
    public int size() {
        return this.len;
    }

    @Override
    public void clear() {
        this.len = 0;
        this.array = (E[]) new Object[this.capacity];
        this.rand = new Random();
        this.addsDone = false;
    }
}
