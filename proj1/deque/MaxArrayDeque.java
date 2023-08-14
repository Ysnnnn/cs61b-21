package deque;
import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> c;
    public MaxArrayDeque(Comparator<T> c) {
        this.c = c;
    }
    public T max() {
        int maxIdex = 0;
        if(isEmpty()) {
            return null;
        }
        for(int i = 0; i < size(); i += 1) {
            if(c.compare(get(maxIdex), get(i)) < 0) {
                maxIdex = i;
            }
        }
        return get(maxIdex);
    }
    public T max(Comparator<T> c) {
        Comparator c2 = c;
        int maxIdex = 0;
        if(isEmpty()) {
            return null;
        }
        for(int i = 0; i < size(); i += 1) {
            if(c2.compare(get(maxIdex), get(i)) < 0) {
                maxIdex = i;
            }
        }
        return get(maxIdex);
    }

}
