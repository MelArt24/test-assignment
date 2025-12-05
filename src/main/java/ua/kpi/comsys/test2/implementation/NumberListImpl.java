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
 *   <li>C3 = 0 - Linear doubly linked (bidirectional) list</li>
 *   <li>C5 = 2 - Octal number system</li>
 *   <li>Additional scale: Decimal number system</li>
 *   <li>C7 = 5 - Algebraic and logical AND of two numbers</li>
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

    /** Number system cycle used in tests. */
    private static final int[] BASES = {2, 3, 8, 10, 16};

    private final int base;
    private Node head;
    private Node tail;
    private int size;


    /**
     * Default constructor. Returns empty {@code NumberListImpl}
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
     * Constructs new {@code NumberListImpl} by <b>decimal</b> number
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
     * Constructs new {@code NumberListImpl} by <b>decimal</b> number
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
     * Returns new {@code NumberListImpl} which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return {@code NumberListImpl} in other scale of notation.
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
     * Returns new {@code NumberListImpl} which represents the result of
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


    /**
     * Returns a string representation of the number stored in this list.
     * Each node contributes one digit, formatted according to the current base.
     * Digits 0–9 are represented as characters '0'–'9', while digits more than equals 10
     * are represented as uppercase letters ('A', 'B', ...).
     *
     * @return a string representation of the stored number, or an empty string if the list is empty
     */
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


    /**
     * Compares this list with another object for equality.
     * Two lists are considered equal if:
     * <ul>
     *     <li>the other object is a {@code List}</li>
     *     <li>both lists have the same size</li>
     *     <li>all corresponding elements are equal by value</li>
     * </ul>
     *
     * @param o the object to compare with
     * @return {@code true} if both lists contain the same digits in the same order,
     *         {@code false} otherwise
     */
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

    /**
     * Returns the number of digits stored in the list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Checks whether the list contains no elements.
     *
     * @return {@code true} if the list is empty, {@code false} otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks whether the list contains the specified digit.
     * Only {@code Byte} values are supported; all other types return {@code false}.
     *
     * @param o the object to check for
     * @return {@code true} if the list contains the digit, {@code false} otherwise
     */
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


    /**
     * Returns an iterator over the digits stored in this list.
     * The iterator traverses the list from head to tail.
     *
     * @return an iterator over the elements in this list
     */
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


    /**
     * Returns an array containing all elements of this list.
     * The returned array is independent of the internal list structure.
     *
     * @return an array containing all digits of this list
     */
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


    /**
     * Not supported in this implementation.
     *
     * @throws UnsupportedOperationException always thrown because this method is not implemented
     */
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray(T[] a) is not implemented");
    }


    /**
     * Appends a new digit to the end of the list.
     * The digit must be within the valid range for the current base.
     *
     * @param e the digit to add
     * @return {@code true} (as the list is always modified)
     * @throws NullPointerException if {@code e} is {@code null}
     * @throws IllegalArgumentException if the digit is outside the current base range
     */
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


    /**
     * Removes the first occurrence of the specified digit from the list.
     *
     * @param o the digit to remove
     * @return {@code true} if an element was removed, {@code false} otherwise
     */
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

    /**
     * Checks whether the list contains all elements of the specified collection.
     *
     * @param c the collection to check
     * @return {@code true} if all elements are present, {@code false} otherwise
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) return true;
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }


    /**
     * Adds all digits from the specified collection to the end of this list.
     *
     * @param c the collection of digits to add
     * @return {@code true} if at least one element was added, {@code false} otherwise
     */
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


    /**
     * Inserts all digits from the given collection starting at the specified index.
     *
     * @param index the position at which to insert
     * @param c the digits to insert
     * @return {@code true} if the list was modified, {@code false} otherwise
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws IllegalArgumentException if any digit exceeds the base limits
     */
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


    /**
     * Removes all digits from this list that are present in the given collection.
     *
     * @param c the collection containing elements to remove
     * @return {@code true} if at least one element was removed, {@code false} otherwise
     */
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


    /**
     * Retains only those digits that are present in the given collection.
     * All other digits are removed.
     *
     * @param c the allowed elements; if {@code null}, the list is cleared
     * @return {@code true} if the list was modified, {@code false} otherwise
     */
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


    /**
     * Removes all elements from the list.
     * After this operation, the list becomes empty.
     */
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


    /**
     * Returns the digit stored at the specified position.
     *
     * @param index the index of the element to fetch
     * @return the digit at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public Byte get(int index) {
        Node node = nodeAt(index);
        return node == null ? null : node.value;
    }


    /**
     * Replaces the digit at the specified position.
     *
     * @param index the index of the element to replace
     * @param element the new digit
     * @return the previous value at the given position
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws NullPointerException if {@code element} is {@code null}
     * @throws IllegalArgumentException if the digit exceeds base constraints
     */
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


    /**
     * Inserts a digit at the specified position in the list.
     *
     * @param index the index at which to insert
     * @param element the digit to insert
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws NullPointerException if {@code element} is {@code null}
     * @throws IllegalArgumentException if the digit exceeds base constraints
     */
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


    /**
     * Removes the digit at the specified index.
     *
     * @param index the position of the digit to remove
     * @return the removed digit
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public Byte remove(int index) {
        Node node = nodeAt(index);
        byte old = node.value;
        unlink(node);
        return old;
    }


    /**
     * Returns the first index where the specified digit occurs.
     *
     * @param o the digit to search for
     * @return the index of the first occurrence, or {@code -1} if not found
     */
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


    /**
     * Returns the last index where the specified digit occurs.
     *
     * @param o the digit to search for
     * @return the index of the last occurrence, or {@code -1} if not found
     */
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


    /**
     * Swaps the digits at the two specified indices.
     *
     * @param index1 the first index
     * @param index2 the second index
     * @return {@code true} if the swap occurred, {@code false} if indices are invalid
     */
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


    /**
     * Sorts the digits of the list in ascending order using selection sort.
     */
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


    /**
     * Sorts the digits of the list in descending order using selection sort.
     */
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


    /**
     * Performs a cyclic left shift:
     * the first element becomes the last.
     * Does nothing if the list contains fewer than two elements.
     */
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


    /**
     * Performs a cyclic right shift:
     * the last element becomes the first.
     * Does nothing if the list contains fewer than two elements.
     */
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

    /**
     * Checks whether the given string is a valid non-negative decimal number.
     * A valid decimal number:
     * <ul>
     *   <li>is not {@code null} and not empty,</li>
     *   <li>does not start with a minus sign,</li>
     *   <li>contains only characters '0'–'9'.</li>
     * </ul>
     *
     * @param s the string to validate
     * @return {@code true} if the string is a valid decimal number, {@code false} otherwise
     */
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

    /**
     * Converts the given list of digits into a {@link BigInteger} value
     * using the specified base.
     * Each element of the list is treated as a single digit in that base.
     *
     * @param list the list of digits to convert
     * @param base the numeric base of the digits
     * @return the decimal {@link BigInteger} representation of the list
     */
    private static BigInteger toBigInteger(List<Byte> list, int base) {
        BigInteger res = BigInteger.ZERO;
        BigInteger bBase = BigInteger.valueOf(base);
        for (int i = 0; i < list.size(); i++) {
            int d = list.get(i) & 0xFF;
            res = res.multiply(bBase).add(BigInteger.valueOf(d));
        }
        return res;
    }

    /**
     * Returns the node located at the specified index.
     * The search is optimized: traversal starts from the head or tail
     * depending on which side is closer to the target index.
     *
     * @param index the index of the node to retrieve
     * @return the node at the specified position
     * @throws IndexOutOfBoundsException if the index is invalid
     */
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

    /**
     * Removes the given node from the doubly linked list.
     * Adjusts all surrounding {@code prev} and {@code next} references
     * and decreases the list size.
     *
     * @param node the node to remove
     */
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

    /**
     * Validates that the given index refers to an existing element.
     * Used for operations such as {@code get}, {@code set}, and {@code remove}.
     *
     * @param index the index to check
     * @throws IndexOutOfBoundsException if the index is not within {@code [0, size-1]}
     */
    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    /**
     * Validates that the index refers to a valid insertion position.
     * Used for operations such as {@code add(index, element)}.
     *
     * A valid position index is within {@code [0, size]}.
     *
     * @param index the index to validate
     * @throws IndexOutOfBoundsException if the index is outside allowed bounds
     */
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    /**
     * Appends a digit to the end of the internal list without performing
     * any validation on ranges or base compatibility.
     * This method is intended for internal use when digits are already known to be valid.
     *
     * @param digit the digit to append
     */
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
