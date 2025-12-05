/*
 * Copyright (c) 2014-2025, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package ua.kpi.comsys.test2;

import java.util.List;

/**
 * List interface for software engineering professional practice
 * module 2 assignment.<p>
 *
 * Represents single positive number. Each list element contains single digit.<p>
 *
 * NumberList does not permit null elements, but can be empty.
 *
 * @author Alexander Podrubailo
 *
 */
public interface NumberList extends List<Byte> {

    //Additional list operations

    /**
     * Exchanges two list elements, specified by indexes.
     * Indexes starts from 0.
     *
     * @param index1 - index of first element
     * @param index2 - index of second element
     * @return {@code true} if operation was successful, otherwise {@code false}.
     */
    boolean swap(int index1, int index2);


    /**
     * Sorts elements of the list in ascending order.
     */
    void sortAscending();


    /**
     * Sorts elements of the list in descending order.
     */
    void sortDescending();


    /**
     * Performs left cyclic shift in current list.
     */
    void shiftLeft();


    /**
     * Performs right cyclic shift in current list.
     */
    void shiftRight();
}

