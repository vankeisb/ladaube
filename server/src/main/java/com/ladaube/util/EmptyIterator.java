package com.ladaube.util;

public class EmptyIterator<T> implements IteratorWithLength<T> {

    public long length() {
        return 0;
    }

    public boolean hasNext() {
        return false;
    }

    public T next() {
        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public long totalLength() {
        return 0;
    }
}
