package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>{
    T[] items;
    int size;
    int head;
    int tail;

    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        head = 4;
        tail = 3;
    }

    public int size(){return size;}

    public boolean isEmpty(){return size == 0;}

    private void resize(int capacity){
        if(head <= tail){
            int bias = capacity/4;
            int i = 0;
            T[] a = (T[]) new Object[capacity];
            for(;head+i<=tail;++i){
                a[i+bias] = items[head+i];
            }
            head = bias;
            tail = bias + i - 1;
            items = a;
        }
        else{
            int bias = capacity/4;
            int i=0;
            T[] a = (T[]) new Object[capacity];
            for(;head+i<items.length;++i){
                a[i+bias] = items[i+head];
            }
            int j=0;
            for(;j<=tail;j++){
                a[i+bias+j] = items[j];
            }
            head = bias;
            tail = i+bias+j-1;
            items = a;
        }
    }

    public void addFirst(T item){
        if(size == items.length){
            resize(size * 2);
        }
        head--;
        if(head<0)head = items.length-1;
        items[head] = item;
        size++;
    }

    public void addLast(T item){
        if(size == items.length){
            resize(size * 2);
        }
        tail++;
        if(tail>=items.length)tail = 0;
        items[tail] = item;
        size++;
    }

    public T removeFirst(){
        if(!isEmpty()){
            if(size>=16&&size<=items.length/4){
                resize(items.length/2);
            }
            T ret = items[head];
            items[head] = null;
            head++;
            if(head>=items.length)head = 0;
            size--;
            return ret;
        }
        else return null;
    }

    public T removeLast(){
        if(!isEmpty()){
            if(size>=16&&size<=items.length/4){
                resize(items.length/2);
            }
            T ret = items[tail];
            items[tail] = null;
            tail--;
            if(tail<0)tail = items.length-1;
            size--;
            return ret;
        }
        else return null;
    }

    public T get(int index){
        if(isEmpty())return null;
        if(head<=tail){
            if(head+index<=tail)return items[head+index];
            else return null;
        }
        else{
            if(head+index<items.length)return items[head+index];
            else if(head+index- items.length<=tail)return items[head+index-items.length];
            else return null;
        }
    }

    private class Myiterator implements Iterator<T>{
        int index;
        public Myiterator(){
            index = 0;
        }

        public T next(){
            T ret;
            if(isEmpty())ret = null;
            if(head<=tail){
                if(head+index<=tail)ret = items[head+index];
                else ret = null;
            }
            else{
                if(head+index<items.length)ret = items[head+index];
                else if(head+index- items.length<=tail)ret = items[head+index-items.length];
                else ret = null;
            }
            index++;
            return ret;
        }

        public boolean hasNext(){
            if(head<=tail){
                return head+index <= tail;
            }
            else{
                if(head+index< items.length)return true;
                else return head+index-items.length<=tail;
            }
        }
    }

    public Iterator<T> iterator(){
        return new Myiterator();
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
        return false;
    }

    public void printDeque(){
        Iterator<T> a = this.iterator();
        while(a.hasNext()){
            System.out.print(a.next()+" ");
        }
        System.out.print('\n');
    }
}
