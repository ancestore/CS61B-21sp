package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T> ,Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4; // 初始设在中间偏后，方便向两边自由扩展
        nextLast = 5;
    }

    // 辅助方法：在循环数组中获取前一个索引
    private int oneMinus(int index) {
        return (index - 1 + items.length) % items.length;
    }

    // 辅助方法：在循环数组中获取后一个索引
    private int onePlus(int index) {
        return (index + 1) % items.length;
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];

        // 从逻辑上的第一个元素开始复制
        int curr = onePlus(nextFirst);
        for (int i = 0; i < size; i++) {
            newItems[i] = items[curr];
            curr = onePlus(curr);
        }

        items = newItems;
        nextFirst = capacity - 1; // 重新指向新数组的尾部（逻辑前驱）
        nextLast = size;          // 重新指向新数组已填充元素的下一个位置
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = oneMinus(nextFirst);
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = onePlus(nextLast);
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int curr = onePlus(nextFirst);
        for (int i = 0; i < size; i++) {
            System.out.print(items[curr] + " ");
            curr = onePlus(curr);
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        // 缩容检查：当利用率低于 25% 且数组长度大于等于 16 时进行缩容（61b 的硬性要求）
        if (items.length >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }

        int targetIndex = onePlus(nextFirst);
        T removedItem = items[targetIndex];
        items[targetIndex] = null; // 释放引用，避免 Loitering（内存泄漏）
        nextFirst = targetIndex;
        size--;
        return removedItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        if (items.length >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }

        int targetIndex = oneMinus(nextLast);
        T removedItem = items[targetIndex];
        items[targetIndex] = null; // 释放引用
        nextLast = targetIndex;
        size--;
        return removedItem;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        // 实际底层的索引需要加上 nextFirst 的偏移量
        int actualIndex = (onePlus(nextFirst) + index) % items.length;
        return items[actualIndex];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos = 0;

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T item = get(wizPos);
            wizPos++;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        // 61b 规范通常要求支持与任何实现了接口的 Deque 比较，这里用简单的类型检查或接口检查
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<?> other = (ArrayDeque<?>) o;
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}