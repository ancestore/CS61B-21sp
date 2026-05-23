package deque;

public interface Deque<T> {
    void addFirst(T item);
    void addLast(T item);
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);

    // 官方文档要求的 default 实现，这样你之前的子类就不用重复写 isEmpty 了
    default boolean isEmpty() {
        return size() == 0;
    }
}