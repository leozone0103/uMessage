package datastructures.worklists;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.PriorityWorkList;
import cse332.interfaces.worklists.WorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/PriorityWorkList.java
 * for method specifications.
 */
public class MinFourHeapComparable<E extends Comparable<E>> extends PriorityWorkList<E> {
    /* Do not change the name of this field; the tests rely on it to work correctly. */
    private E[] data;
    private int size;

    public MinFourHeapComparable() {
        data = (E[]) new Comparable[21];
        size = 0;
    }

    @Override
    public boolean hasWork() {
        return !(this.size == 0);
    }

    @Override
    public void add(E work) {
        if (size == data.length) {
            data = addSpace(data);
        }
        data[size] = work;
        int currentIndex = size;
        size++;
        while (currentIndex != 0 && data[(currentIndex - 1) / 4].compareTo(data[currentIndex]) > 0) {
            data[currentIndex] = data[(currentIndex - 1) / 4];
            data[(currentIndex - 1) / 4] = work;
            currentIndex =  (currentIndex - 1) / 4;
        }
    }

    private E[] addSpace(E[] data) {
        E[] newData = (E[]) new Comparable[data.length * 4 + 1];
        int i = 0;
        for (E element : data) {
            newData[i++] = element;
        }
        return newData;
    }

    private E[] shrinkSpace(E[] data, int size) {
        if ((size * 3 + 1) * 4 == data.length * 3 + 1 && size > 21) {
            E[] newData = (E[]) new Comparable[(data.length - 1) / 4];
            for (int i = 0; i < size; i++) {
                newData[i] = data[i];
            }
            return newData;
        }
        return data;
    }

    @Override
    public E peek() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return data[0];
    }

//    private E nextMistaken() {
//        if (this.size == 0) {
//            throw new NoSuchElementException();
//        }
//        E minElement = data[0];
//        size--;
//        data[0] = data[size];
//        int currentIndex = size;
//
//        WorkList<Integer> path = new ArrayStack<Integer>();
//        path.add(currentIndex);
//        while (currentIndex != 0) {
//            path.add((currentIndex - 1) / 4);
//            currentIndex = (currentIndex - 1) / 4;
//        }
//
//        currentIndex = path.next();
//        while (path.hasWork() && data[currentIndex].compareTo(data[path.peek()]) > 0) {
//            data[currentIndex] = data[path.peek()];
//            currentIndex = path.next();
//            data[currentIndex] = data[size];
//        }
//
//        data[size] = null;
//        return minElement;
//    }

    public E next() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        E minElement = data[0];
        size--;
        data[0] = data[size];
        heapifyPercolateDown(data, 0, size);
        data[size] = null;
        data = shrinkSpace(data,size);
        return minElement;
    }

    private void heapifyPercolateDown(E[] data, int currentIndex, int lastElementIndex) {
        int minIndex = findMinElementIndex(data, (currentIndex + 1) * 4 - 3, (currentIndex + 1) * 4 + 1, size);
        if (minIndex != -1 && data[currentIndex].compareTo(data[minIndex]) > 0) {
            data[currentIndex] = data[minIndex];
            data[minIndex] = data[lastElementIndex];
            heapifyPercolateDown(data, minIndex, lastElementIndex);
        }
    }

    private int findMinElementIndex(E[] data, int first, int last, int size) {
        if (first >= size) {
            return -1;
        } else {
            int minElementIndex = first;
            for (int i = first + 1; i < Math.min(last, size); i++) {
                if (data[i].compareTo(data[minElementIndex]) < 0) {
                    minElementIndex = i;
                }
            }
            return minElementIndex;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        data = (E[]) new Comparable[21];
        size = 0;
    }
}
