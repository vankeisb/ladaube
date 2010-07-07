package com.ladaube.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionIterator<T> implements IteratorWithLength<T> {

    private Iterator<T> iterator;
    private Collection collection;
    private long totalLength = -1;

    public CollectionIterator(Collection<T> c) {
        this.collection = c;
        this.iterator = c.iterator();
    }

    public CollectionIterator(Collection collection, long totalLength) {
        this(collection);
        this.totalLength = totalLength;
    }

    public long totalLength() {
        if (totalLength==-1) {
            return length();
        } else {
            return totalLength;
        }
    }

    public long length() {
        return collection.size();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        return iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
