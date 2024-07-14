package bstmap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node{
        K key;
        V value;
        Node left;
        Node right;

        Node(K k, V x){
            key = k;
            value = x;
            left = null;
            right = null;
        }

        public boolean containsKey(K x){
            if(x.compareTo(key)==0)return true;
            if(x.compareTo(key)<0 && left != null)return left.containsKey(x);
            if(x.compareTo(key)>0 && right != null)return right.containsKey(x);
            return false;
        }

        public V get(K x){
            if(x.compareTo(key)==0)return value;
            if(x.compareTo(key)<0 && left != null)return left.get(x);
            if(x.compareTo(key)>0 && right != null)return right.get(x);
            return null;
        }

        public void put(K k, V x){
            assert k.compareTo(key) != 0;
            if(k.compareTo(key)<0){
                if(left == null){
                    left = new Node(k, x);
                }
                else left.put(k, x);
            }
            else{
                if(right == null){
                    right = new Node(k, x);
                }
                else right.put(k, x);
            }
        }

        public void getKeySet(Set<K> s){
            s.add(key);
            if(left != null)left.getKeySet(s);
            if(right != null)right.getKeySet(s);
        }

        public void print(int depth){
            String space = "-----";
            space = space.repeat(depth);
            System.out.println(space + key);
            if(left != null)left.print(depth+1);
            if(right != null)right.print(depth+1);
        }

        public V remove(K k){
            V ret = null;
            if(k.compareTo(key)<0){
                if(left != null && k.compareTo(left.key)==0){
                    ret = left.value;
                    deleteLeft(this);
                }
                else if(left != null)ret = left.remove(k);
            }
            else if(k.compareTo(key)>0){
                if(right != null && k.compareTo(right.key)==0) {
                    ret = right.value;
                    deleteRight(this);
                }
                else if(right != null)ret = right.remove(k);
            }
            return ret;
        }

        public V remove(K k, V v){
            V ret = null;
            if(k.compareTo(key)<0){
                if(left != null && k.compareTo(left.key)==0){
                    if(v.equals(left.value)) {
                        ret = left.value;
                        deleteLeft(this);
                    }
                    else ret = null;
                }
                else if(left != null)ret = left.remove(k);
            }
            else if(k.compareTo(key)>0){
                if(right != null && k.compareTo(right.key)==0){
                    if(v.equals(right.value)) {
                        ret = right.value;
                        deleteRight(this);
                    }
                    else ret = null;
                }
                else if(right != null)ret = right.remove(k);
            }
            return ret;
        }

        private void deleteLeft(Node parent){
            Node target = parent.left;
            if(target.left==null&&target.right==null)parent.left = null;
            else if(target.left==null)parent.left = target.right;
            else if(target.right==null)parent.left = target.left;
            else{
                Node p = target.left;
                Node q = p.right;
                if(q == null){
                    parent.left = p;
                    parent.left.right = target.right;
                }
                else{
                    while(q.right != null){
                        p = p.right;
                        q = q.right;
                    }
                    p.right = q.left;
                    parent.left = q;
                    parent.left.left = p;
                    parent.left.right = target.right;
                }
            }
        }

        private  void deleteRight(Node parent){
            Node target = parent.right;
            if(target.left==null&&target.right==null)parent.right = null;
            else if(target.left==null)parent.right = target.right;
            else if(target.right==null)parent.right = target.left;
            else{
                Node p = target.left;
                Node q = p.right;
                if(q == null){
                    parent.right = p;
                    parent.right.right = target.right;
                }
                else{
                    while(q.right != null){
                        p = p.right;
                        q = q.right;
                    }
                    p.right = q.left;
                    parent.right = q;
                    parent.right.left = p;
                    parent.right.right = target.right;
                }
            }
        }
    }

    private Node removeRoot(Node root){
        if(root.left!=null){
            Node p = root.left;
            Node q = p.right;
            if(q == null){
                p.right = root.right;
                return p;
            }
            while(q.right != null){
                q = q.right;
                p = p.right;
            }
            p.right = q.left;
            q.left = root.left;
            q.right = root.right;
            return q;
        }
        else if(root.right!=null){
            Node p = root.right;
            Node q = p.left;
            if(q == null){
                p.left = root.left;
                return p;
            }
            while(q.left != null){
                p = p.left;
                q = q.left;
            }
            p.left = q.right;
            q.left = root.left;
            q.right = root.right;
            return q;
        }
        return null;
    }

    private Node root;
    private int size;

    public BSTMap(){
        root = null;
        size = 0;
    }

    public void printInOrder(){
        if(root != null)root.print(0);
        else System.out.println("Empty BST!");
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if(root == null)return false;
        return root.containsKey(key);
    }

    @Override
    public V get(K key) {
        if(root == null)return null;
        return root.get(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if(root == null)root = new Node(key, value);
        else root.put(key, value);
        size++;
    }

    @Override
    public Set<K> keySet() {
        if(root == null)return Set.of();
        else{
            Set<K> ret = new java.util.HashSet<>(Set.of());
            root.getKeySet(ret);
            return ret;
        }
    }

    @Override
    public V remove(K key) {
        size--;
        V ret = null;
        if(root.key.compareTo(key)==0){
            ret = root.value;
            root = removeRoot(root);
        }
        else{
            ret = root.remove(key);
        }
        return ret;
    }

    @Override
    public V remove(K key, V value) {
        size--;
        V ret;
        if(root.key.compareTo(key)==0 && root.value.equals(value)){
            ret = value;
            root = removeRoot(root);
        }
        else if(root.key.compareTo(key)==0)ret =  null;
        else{
            ret = root.remove(key, value);
        }
        return ret;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
