/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
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

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        // TODO Auto-generated method stub
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        // TODO Auto-generated method stub
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        // TODO Auto-generated method stub
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        // TODO Auto-generated method stub
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        // TODO Auto-generated method stub
        return 0;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean add(Byte e) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }


    @Override
    public Byte get(int index) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Byte set(int index, Byte element) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void add(int index, Byte element) {
        // TODO Auto-generated method stub

    }


    @Override
    public Byte remove(int index) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int indexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean swap(int index1, int index2) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void sortAscending() {
        // TODO Auto-generated method stub
    }


    @Override
    public void sortDescending() {
        // TODO Auto-generated method stub
    }


    @Override
    public void shiftLeft() {
        // TODO Auto-generated method stub

    }


    @Override
    public void shiftRight() {
        // TODO Auto-generated method stub

    }
}
