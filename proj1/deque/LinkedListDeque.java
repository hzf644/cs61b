package deque;

import java.util.Iterator;

public class LinkedListDeque <T> implements Deque<T> , Iterable<T>{

    private static class Node<Item>{
        Item item;
        Node<Item> prev;
        Node<Item> next;
        public Node(){
            item = null;
            prev = null;
            next = null;
        }
        public Node(Item x){
            item = x;
            prev = null;
            next = null;
        }
    }

    private Node<T> sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node<>();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }


    public void addFirst(T x){
        Node<T> p = sentinel.next;
        Node<T> y = new Node<>(x);
        sentinel.next = y;
        y.prev = sentinel;
        y.next = p;
        p.prev = y;
        size++;
    }

    public void addLast(T x){
        Node<T> p = sentinel.prev;
        Node<T> y = new Node<>(x);
        sentinel.prev = y;
        y.next = sentinel;
        y.prev = p;
        p.next = y;
        size++;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public int size(){return size;}

    public T removeFirst(){
        if(!this.isEmpty()){
            size--;
            Node<T> ret = sentinel.next;
            Node<T> p = sentinel.next.next;
            sentinel.next = p;
            p.prev = sentinel;
            return ret.item;
        }
        else return null;
    }

    public T removeLast(){
        if(!this.isEmpty()){
            size--;
            Node<T> ret = sentinel.prev;
            Node<T> p = sentinel.prev.prev;
            sentinel.prev = p;
            p.next = sentinel;
            return ret.item;
        }
        else return null;
    }

    public T get(int index){
        Node<T> p = sentinel.next;
        while(index>0 && p != sentinel){
            p = p.next;
            index--;
        }
        if(p!=sentinel&&index==0)return p.item;
        else return null;
    }

    private class MyIterator implements Iterator<T> {
        private  Node<T> pos;

        public MyIterator(){
            pos = sentinel.next;
        }

        public boolean hasNext() {
            return pos != sentinel;
        }

        public T next(){
            T ret = pos.item;
            pos = pos.next;
            return ret;
        }
    }

    public Iterator<T> iterator(){
        MyIterator ret = new MyIterator();
        return ret;
    }

    public boolean equals(Object o){
        if(o instanceof ArrayDeque){
            ArrayDeque<T> sample = (ArrayDeque<T>) o;
            if(sample.size() == this.size()){
                Iterator<T> a = sample.iterator();
                Iterator<T> b = this.iterator();
                while(a.hasNext()){
                    if(!a.next().equals(b.next())){
                        return false;
                    }
                }
                return true;
            }
        }
        if(o instanceof LinkedListDeque){
            LinkedListDeque<T> sample = (LinkedListDeque<T>) o;
            if(sample.size() == this.size()){
                Iterator<T> a = sample.iterator();
                Iterator<T> b = this.iterator();
                while(a.hasNext()){
                    if(!a.next().equals(b.next())){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private T getHelper(Node<T> p, int index){
        if(index > 0&&p.next == sentinel)return null;
        if(index == 0&&p != sentinel)return p.item;
        if(index == 0)return null;
        else{
            return getHelper(p.next, index-1);
        }
    }

    public T getRecursive(int index){
        return getHelper(sentinel.next, index);
    }

    public void printDeque(){
        Node<T> p = sentinel.next;
        while(p != sentinel){
            System.out.print(p.item+" ");
            p = p.next;
        }
        System.out.print('\n');
    }
}
