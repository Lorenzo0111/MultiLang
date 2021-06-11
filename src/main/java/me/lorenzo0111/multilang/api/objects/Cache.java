package me.lorenzo0111.multilang.api.objects;

import java.io.File;
import java.io.IOException;

public interface Cache<K,V> {
    void reset();
    void add(K key, V value);
    boolean remove(K key, V value);
    V remove(K key);
    V get(K key);
    File save(File file) throws IOException;
}
