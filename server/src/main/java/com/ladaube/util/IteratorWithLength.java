package com.ladaube.util;

import java.util.Iterator;

public interface IteratorWithLength<T> extends Iterator<T> {

    long length();

    long totalLength();
}
