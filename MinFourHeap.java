package datastructures.worklists;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.PriorityWorkList;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/PriorityWorkList.java
 * for method specifications.
 */
public class MinFourHeap<E> extends PriorityWorkList<E> {
    /* Do not change the name of this field; the tests rely on it to work correctly. */
    private E[] data;
    private int size;
    private Comparator<E> comp;
    public MinFourHeap(Comparator<E> c) {
        data = (E[])new Object[21];
        size = 0;
        comp = c;

    }

    @Override
    public boolean hasWork() {
        return size > 0;
    }

    @Override
    public void add(E work) {
        if(size == data.length) {
            E[] newArr = (E[])new Object[size * 4 + 1];
            int i = 0;
            for(E element : this.data) {
                newArr[i] = element;
                i++;
            }
            this.data = newArr;
        }
        this.size++;
        data[size-1] = work;
        switchUp(size - 1, work);
    }

    @Override
    public E peek() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return data[0];
    }

    @Override
    public E next() {
        if(!this.hasWork()) {
            throw new NoSuchElementException();
        }
        E res = data[0];
        size--;
        data[0] = data[size];
        switchDown(data,0, size);
        data[size] = null;
        return res;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        data = (E[]) new Comparable[21];

    }

    private void switchUp(int index, E work) {
        while(comp.compare(work, data[(index - 1) / 4]) < 0 && index > 0) {
            E temp = data[index];
            data[index] = data[(index - 1 )/ 4];
            data[(index - 1 )/ 4] = temp;
            index = (index - 1) / 4;
        }
    }

    private void switchDown(E[] data, int currentIndex, int lastElementIndex) {
        int minIndex = findMinElementIndex(data, (currentIndex + 1) * 4 - 3, (currentIndex + 1) * 4 + 1, size);
        if (minIndex != -1 && comp.compare(data[currentIndex], data[minIndex]) > 0) {
            data[currentIndex] = data[minIndex];
            data[minIndex] = data[lastElementIndex];
            switchDown(data, minIndex, lastElementIndex);
        }
    }

    private int findMinElementIndex(E[] data, int first, int last, int size) {
        if (first >= size) {
            return -1;
        } else {
            int minElementIndex = first;
            for (int i = first + 1; i < Math.min(last, size); i++) {
                if (comp.compare(data[i], data[minElementIndex]) < 0) {
                    minElementIndex = i;
                }
            }
            return minElementIndex;
        }
    }
}
