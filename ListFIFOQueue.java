package datastructures.worklists;

import cse332.interfaces.worklists.FIFOWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/FIFOWorkList.java
 * for method specifications.
 */
public class ListFIFOQueue<E> extends FIFOWorkList<E> {
    private int size;
    private ListNode head; // new element comes in
    private ListNode tail;  // old element popped out
    private class ListNode<E> {
        E work;
        ListNode next;
        ListNode prev;
        public ListNode(E work) {
            // Check point
            this.work = work;
            this.prev = null;
            this.next = null;
        }

        public ListNode() {
            // Check point
            this.work = null;
            this.prev = null;
            this.next = null;
        }

    }

    public ListFIFOQueue() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public void add(E work) {
        if (work == null) {
            throw new NoSuchElementException();
        }
        ListNode added = new ListNode(work);
        if(this.size == 0) {
            this.head = added;
            this.tail = added;
        } else {
            this.head.next = added;
            added.prev = this.head;
            head = head.next;
        }
        size++;
    }

    @Override
    public E peek() {
        if(this.size != 0) {
            ListNode temo = this.tail;
            return (E)temo.work;
        } else {
            // checkpoint
            throw new NoSuchElementException();
        }
    }

    @Override
    public E next() {
        if(this.size != 0) {
            ListNode temp = this.tail;
            this.tail = this.tail.next;
            if(this.tail != null) {this.tail.prev = null;}
            this.size--;
            return (E)temp.work;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }
}
