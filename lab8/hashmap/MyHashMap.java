package hashmap;

import org.eclipse.jetty.server.RequestLog;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private double max_load;
    private int table_size;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        size = 0;
        max_load = 0.75;
        table_size = 16;
        buckets = createTable(table_size);
    }

    public MyHashMap(int initialSize) {
        size = 0;
        max_load = 0.75;
        table_size = initialSize;
        buckets = createTable(table_size);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        max_load = maxLoad;
        table_size = initialSize;
        buckets = createTable(table_size);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {return new ArrayList<>();}

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] ret = new Collection[tableSize];
        for(int i=0;i<tableSize;++i){
            ret[i] = createBucket();
        }
        return ret;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    private int Code(K key){
        int ret = key.hashCode() % table_size;
        if(ret<0)ret+=table_size;
        return ret;
    }

    @Override
    public void clear() {
        buckets = createTable(16);
        table_size = 16;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if(size==0)return false;
        int code = Code(key);
        Collection<Node> bucket = buckets[code];
        for (Node node : bucket) {
            if (node.key.equals(key)) return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        if(size==0)return null;
        int code = Code(key);
        Collection<Node> bucket = buckets[code];
        for(Node node : bucket){
            if(node.key.equals(key))return node.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int code = Code(key);
        boolean exist = false;
        for(Node n : buckets[code]){
            if(n.key.equals(key)){
                n.value = value;
                exist = true;
            }
        }
        if(!exist){
            size++;
            buckets[code].add(createNode(key, value));
        }

        if(size * 1.0 / table_size >= max_load){
            table_size *= 2;
            Collection<Node>[] a = createTable(table_size);
            for(Collection<Node> b : buckets){
                for(Node n : b){
                    int newCode = Code(n.key);
                    a[newCode].add(n);
                }
            }
            buckets = a;
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> ret = new HashSet<>();
        for(Collection<Node> bucket : buckets){
            for(Node n : bucket){
                ret.add(n.key);
            }
        }
        return ret;
    }

    @Override
    public V remove(K key) {

    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    private class MyIterator implements Iterator<K>{

        int bucket_index;
        Iterator<Node> iter;

        public MyIterator(){
            bucket_index = 0;
            iter = buckets[bucket_index].iterator();
        }

        public boolean hasNext(){
            return bucket_index != table_size - 1 || iter.hasNext();
        }

        public K next(){
            K ret = null;
            if(iter.hasNext()){
                ret = iter.next().key;
            }
            else{
                bucket_index ++;
                iter = buckets[bucket_index].iterator();
                ret = iter.next().key;
            }
            return ret;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new MyIterator();
    }
}
