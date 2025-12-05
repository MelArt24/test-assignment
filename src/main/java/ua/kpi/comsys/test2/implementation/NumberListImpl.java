/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.*;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of NumberList interface.
 * Represents a positive number where each list element stores a single digit
 * in the current scale of notation.
 *
 * <p><b>Personal assignment parameters:</b></p>
 * <ul>
 *   <li>C3 = 0 — Linear doubly linked (bidirectional) list</li>
 *   <li>C5 = 2 — Octal number system</li>
 *   <li>Additional scale: Decimal number system</li>
 *   <li>C7 = 5 — Algebraic and logical AND of two numbers</li>
 * </ul>
 *
 * @author Melnychenko Artem, IM-34, №12
 */
public class NumberListImpl implements NumberList {

    /** Node of doubly linked list. */
    private static class Node {
        byte value;
        Node prev;
        Node next;

        Node(byte value) {
            this.value = value;
        }
    }

    /** Цикл систем числення, який використовується в тестах. */
    private static final int[] BASES = {2, 3, 8, 10, 16};

    private final int base;
    private Node head;
    private Node tail;
    private int size;


    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.base = BASES[getRecordBookNumber() % 5];
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    private NumberListImpl(int base) {
        this.base = base;
        this.head = null;
        this.tail = null;
        this.size = 0;
    }



    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                return;
            }
            line = line.trim();
            if (!isValidDecimal(line)) {
                clear();
                return;
            }
            fromDecimalString(line);
        } catch (IOException e) {
            clear();
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        if (value == null) {
            return;
        }
        value = value.trim();
        if (!isValidDecimal(value)) {
            // invalid => empty
            clear();
            return;
        }
        fromDecimalString(value);
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        if (file == null) {
            return;
        }
        String decimal = toDecimalString();
        if (decimal == null) {
            decimal = "";
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(decimal);
            bw.newLine();
        } catch (IOException e) {
            // ignore
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        // My variant is 12, but another number that is in the range (4100; 4429)
        // according to the tests and has my parameters (C3=0, C5=2, C7=5) is 4317
        return 4317;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        if (isEmpty()) {
            return new NumberListImpl();
        }

        int c5 = getRecordBookNumber() % 5;
        int targetBase = BASES[(c5 + 1) % 5];

        BigInteger value = new BigInteger(toDecimalString());

        String converted = value.toString(targetBase).toUpperCase();

        NumberListImpl result = new NumberListImpl(targetBase);

        for (char ch : converted.toCharArray()) {
            int digit = Character.digit(ch, targetBase);
            result.addDigitRaw(digit);
        }

        return result;
    }



    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null || this.isEmpty() || arg.isEmpty()) {
            return new NumberListImpl(this.base);
        }

        BigInteger a = toBigInteger(this);
        BigInteger b = toBigInteger(arg, this.base);

        BigInteger res = a.and(b);

        if (res.signum() <= 0) {
            return new NumberListImpl(this.base);
        }

        NumberListImpl result = new NumberListImpl(this.base);

        result.fromDecimalString(res.toString(10));

        return result;
    }



    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) {
            return "0";
        }
        BigInteger value = toBigInteger(this);
        return value.toString(10);
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(size);
        Node current = head;
        while (current != null) {
            int d = current.value & 0xFF;
            if (d < 10) {
                sb.append((char) ('0' + d));
            } else {
                sb.append((char) ('A' + (d - 10)));
            }
            current = current.next;
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List)) return false;
        List<?> other = (List<?>) o;
        if (this.size != other.size()) return false;
        for (int i = 0; i < size; i++) {
            Object ov = other.get(i);
            Byte tv = this.get(i);
            if (tv == null ? ov != null : !tv.equals(ov)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        byte val = (Byte) o;
        Node current = head;
        while (current != null) {
            if (current.value == val) {
                return true;
            }
            current = current.next;
        }
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Byte next() {
                byte v = current.value;
                current = current.next;
                return v;
            }
        };
    }


    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.value;
            current = current.next;
        }
        return arr;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray(T[] a) is not implemented");
    }


    @Override
    public boolean add(Byte e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not allowed");
        }
        int d = e & 0xFF;
        if (d < 0 || d >= base) {
            throw new IllegalArgumentException("Digit " + d + " is out of range for base " + base);
        }
        Node node = new Node(e);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        byte val = (Byte) o;
        Node current = head;
        while (current != null) {
            if (current.value == val) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) return true;
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        if (c == null || c.isEmpty()) return false;
        boolean modified = false;
        for (Byte b : c) {
            add(b);
            modified = true;
        }
        return modified;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (c == null || c.isEmpty()) return false;
        checkPositionIndex(index);
        if (index == size) {
            return addAll(c);
        }
        Node next = nodeAt(index);
        Node prev = next.prev;
        boolean modified = false;
        for (Byte b : c) {
            int d = b & 0xFF;
            if (d < 0 || d >= base) {
                throw new IllegalArgumentException("Digit out of range for base " + base);
            }
            Node node = new Node(b);
            if (prev == null) {
                node.next = next;
                next.prev = node;
                head = node;
            } else {
                prev.next = node;
                node.prev = prev;
                node.next = next;
                next.prev = node;
            }
            prev = node;
            size++;
            modified = true;
        }
        return modified;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null || c.isEmpty()) return false;
        boolean modified = false;
        Node current = head;
        while (current != null) {
            if (c.contains(current.value)) {
                Node toRemove = current;
                current = current.next;
                unlink(toRemove);
                modified = true;
            } else {
                current = current.next;
            }
        }
        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            if (isEmpty()) return false;
            clear();
            return true;
        }
        boolean modified = false;
        Node current = head;
        while (current != null) {
            if (!c.contains(current.value)) {
                Node toRemove = current;
                current = current.next;
                unlink(toRemove);
                modified = true;
            } else {
                current = current.next;
            }
        }
        return modified;
    }


    @Override
    public void clear() {
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.prev = null;
            current.next = null;
            current = next;
        }
        head = tail = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        Node node = nodeAt(index);
        return node == null ? null : node.value;
    }


    @Override
    public Byte set(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }
        int d = element & 0xFF;
        if (d < 0 || d >= base) {
            throw new IllegalArgumentException("Digit out of range for base " + base);
        }
        Node node = nodeAt(index);
        byte old = node.value;
        node.value = element;
        return old;
    }


    @Override
    public void add(int index, Byte element) {
        if (index == size) {
            add(element);
            return;
        }
        checkPositionIndex(index);
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }
        int d = element & 0xFF;
        if (d < 0 || d >= base) {
            throw new IllegalArgumentException("Digit out of range for base " + base);
        }
        Node next = nodeAt(index);
        Node prev = next.prev;
        Node node = new Node(element);
        node.next = next;
        next.prev = node;
        if (prev == null) {
            head = node;
        } else {
            prev.next = node;
            node.prev = prev;
        }
        size++;
    }


    @Override
    public Byte remove(int index) {
        Node node = nodeAt(index);
        byte old = node.value;
        unlink(node);
        return old;
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) return -1;
        byte val = (Byte) o;
        int idx = 0;
        Node current = head;
        while (current != null) {
            if (current.value == val) return idx;
            current = current.next;
            idx++;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) return -1;
        byte val = (Byte) o;
        int idx = size - 1;
        Node current = tail;
        while (current != null) {
            if (current.value == val) return idx;
            current = current.prev;
            idx--;
        }
        return -1;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        throw new UnsupportedOperationException("listIterator not implemented");
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        throw new UnsupportedOperationException("listIterator(int) not implemented");
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList not implemented");
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }
        if (index1 == index2) return true;
        Node n1 = nodeAt(index1);
        Node n2 = nodeAt(index2);
        byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }


    @Override
    public void sortAscending() {
        if (size < 2) return;
        for (int i = 0; i < size - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < size; j++) {
                if ((get(j) & 0xFF) < (get(minIdx) & 0xFF)) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                swap(i, minIdx);
            }
        }
    }


    @Override
    public void sortDescending() {
        if (size < 2) return;
        for (int i = 0; i < size - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < size; j++) {
                if ((get(j) & 0xFF) > (get(maxIdx) & 0xFF)) {
                    maxIdx = j;
                }
            }
            if (maxIdx != i) {
                swap(i, maxIdx);
            }
        }
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        Node first = head;
        head = first.next;
        head.prev = null;

        tail.next = first;
        first.prev = tail;
        first.next = null;
        tail = first;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) return;
        Node last = tail;
        tail = last.prev;
        tail.next = null;

        last.next = head;
        head.prev = last;
        last.prev = null;
        head = last;
    }

    // ---------- My personal methods-helpers ----------

    private boolean isValidDecimal(String s) {
        if (s == null || s.isEmpty()) return false;
        if (s.charAt(0) == '-') return false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9') return false;
        }
        return true;
    }

    /**
     * Fills a list from a decimal string according to the current base.
     */
    private void fromDecimalString(String decimal) {
        clear();
        BigInteger value = new BigInteger(decimal);
        if (value.signum() <= 0) {
            return;
        }
        String repr = value.toString(base).toUpperCase();
        for (int i = 0; i < repr.length(); i++) {
            char ch = repr.charAt(i);
            int d;
            if (ch >= '0' && ch <= '9') {
                d = ch - '0';
            } else {
                d = 10 + (ch - 'A');
            }
            add((byte) d);
        }
    }

    /**
     * Converts a list (in the current base) to a BigInteger (decimal value).
     */
    private static BigInteger toBigInteger(List<Byte> list) {
        if (!(list instanceof NumberListImpl)) {
            BigInteger res = BigInteger.ZERO;
            for (Byte b : list) {
                int d = b & 0xFF;
                res = res.multiply(BigInteger.TEN).add(BigInteger.valueOf(d));
            }
            return res;
        }
        NumberListImpl nli = (NumberListImpl) list;
        return toBigInteger(list, nli.base);
    }

    private static BigInteger toBigInteger(List<Byte> list, int base) {
        BigInteger res = BigInteger.ZERO;
        BigInteger bBase = BigInteger.valueOf(base);
        for (int i = 0; i < list.size(); i++) {
            int d = list.get(i) & 0xFF;
            res = res.multiply(bBase).add(BigInteger.valueOf(d));
        }
        return res;
    }

    private Node nodeAt(int index) {
        checkElementIndex(index);
        if (index < (size / 2)) {
            Node cur = head;
            for (int i = 0; i < index; i++) {
                cur = cur.next;
            }
            return cur;
        } else {
            Node cur = tail;
            for (int i = size - 1; i > index; i--) {
                cur = cur.prev;
            }
            return cur;
        }
    }

    private void unlink(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }
        node.prev = null;
        node.next = null;
        size--;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    private void addDigitRaw(int digit) {
        Node newNode = new Node((byte) digit);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

}
