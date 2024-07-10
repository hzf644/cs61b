package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{

    Comparator<T> default_comparator = null;

    public MaxArrayDeque(Comparator<T> c){
        super();
        default_comparator = c;
    }

    public T max(){
        Iterator<T> a = this.iterator();
        T ret = this.get(0);
        while(a.hasNext()){
            T cur = a.next();
            if(default_comparator.compare(ret, cur)<0){
                ret = cur;
            }
        }
        return ret;
    }

    public T max(Comparator<T> c){
        Iterator<T> a = this.iterator();
        T ret = this.get(0);
        while(a.hasNext()){
            T cur = a.next();
            if(c.compare(ret, cur)<0){
                ret = cur;
            }
        }
        return ret;
    }
}
