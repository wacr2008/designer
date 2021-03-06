package com.fr.design.gui.ilist;

import com.fr.general.ComparatorUtils;
import com.fr.stable.Nameable;


public class ListModelElement {

    public Nameable wrapper;

    public ListModelElement(Nameable nameable) {
        this.wrapper = nameable;
    }

    public boolean equals(Object o) {
        return o instanceof ListModelElement
                && ComparatorUtils.equals(((ListModelElement) o).wrapper, wrapper);
    }
}