package flik;

/** An Integer tester created by Flik Enterprises.
 * @author Josh Hug
 * */
public class Flik {
    /** @param a Value 1
     *  @param b Value 2
     *  @return Whether a and b are the same
     *  ==号比较的是指针指向的地址以及数据是否相同，也就是说==号如果比较两个地址不相同的数据的话，
     *  就会返回false，我们可以看看它的源码，当我们new一个新的Integer的时候，后台会先进入valueOf这个方
     *  法进行判断，当这个值大于等于最小值并且小于等于最大值的时候，后台会在常量池中查找是否有，如果有就直
     *  接返回，如果没有那么就会新建一个然后返回.当值不满足这两个条件的时候，也就是当值小于low：-128 或者
     *  值大于 high：127的时候（high可以从上面那张图发现最大值为127），会直接new一个新的对象并且返回，那
     *  么两个128比较就相当于new了两次，也就是说它的地址是完全发生了改变，所以会返回false */
    public static boolean isSameNumber(Integer a, Integer b) {
        return a.equals(b);
    }
}
