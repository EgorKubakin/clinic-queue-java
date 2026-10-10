package lab2.heap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class BinHeap<T> {
    private Object[] arr;
    private int size;
    private int capacity;
    private Comparator<T> comparator;
    private int[] visited;
    private int lastIndex;
    private int sizeVisited;

    public int[] getVisited() {
        return visited;
    }


    public int getLastIndex() {
        return lastIndex;
    }

    public void clearTrace() {
        visited = null;
        sizeVisited = 0;
        lastIndex = -1;
    }

    public BinHeap(Comparator<T> comparator) {
        this.comparator = comparator;
        this.visited = null;
        this.sizeVisited = 0;
        this.lastIndex = -1;
        this.capacity = 4;
        this.size = 0;
        arr = new Object[capacity];
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public void add(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (size >= capacity) {
            expand();
        }
        arr[size] = value;
        int currentIndex = size;
        size++;
        siftUp(currentIndex);
    }

    public T getMin() {
        checkHeap();
        return (T) arr[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getLeft(int currentIndex) {
        return 2 * currentIndex + 1;
    }

    public int getRight(int currentIndex) {
        return 2 * currentIndex + 2;
    }

    public int getParent(int currentIndex) {
        return (currentIndex - 1) / 2;
    }

    public T pop() {
        clearTrace();
        checkHeap();
        T element = (T) arr[0];
        arr[0] = arr[size - 1];
        arr[--size] = null;
        int currentIndex = 0;
        siftDown(currentIndex);
        return element;
    }

    public void updatePosition(int index) {
        int parent = getParent(index);
        clearTrace();
        if (index > 0 && comparator.compare((T) arr[index], (T) arr[parent]) < 0) {
            siftUp(index);
        } else {
            siftDown(index);
        }
    }

    public T get(int index) {
        checkIndex(index);
        return (T) arr[index];
    }

    public int search(T element) {
        checkHeap();
        for (int i = 0; i < size; i++) {
            if (arr[i].equals(element)) {
                return i;
            }
        }
        throw new NoSuchElementException("Element not found");
    }

    public BinHeap<T> copy() {
        BinHeap<T> copy = new BinHeap<>(comparator);
        copy.capacity = this.capacity;
        copy.size = this.size;
        copy.arr = Arrays.copyOf(this.arr, this.capacity);
        return copy;
    }

    public int search(Predicate<T> test) {
        int size = getSize();
        clearTrace();
        visited = new int[4];
        for (int i = 0; i < size; i++) {
            checkVisited();
            visited[sizeVisited++] = i;
            if (test.test((T) arr[i])) {
                lastIndex = i;
                break;
            }
        }
        return lastIndex;

    }

    public void remove(T element) {
        clearTrace();
        int index = search(element);
        arr[index] = arr[size - 1];
        arr[--size] = null;
        if (index == size) {
            return;
        }
        int parent = getParent(index);
        if (parent != index && comparator.compare((T) arr[index], (T) arr[parent]) < 0) {
            siftUp(index);
        } else {
            siftDown(index);
        }
        clearTrace();

    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Incorrect index: " + index);
        }
    }

    private void siftDown(int currentIndex) {
        int left = getLeft(currentIndex);
        int right = getRight(currentIndex);
        if (left >= size) {
            return;
        }
        int minimum = min(left, right);
        while (comparator.compare((T) arr[currentIndex], (T) arr[minimum]) > 0) {
            swap(currentIndex, minimum);
            currentIndex = minimum;
            left = getLeft(currentIndex);
            right = getRight(currentIndex);
            if (left >= size) {
                break;
            }
            minimum = min(left, right);
        }

    }

    public BinHeap<T> merge(BinHeap<T> other) {
        if (other == null) {
            throw new IllegalArgumentException("Incorrect heap");
        }
        int cap = Math.max(4, this.size + other.size);
        BinHeap<T> newHeap = new BinHeap<>(comparator);
        newHeap.arr = new Object[cap];
        newHeap.size = this.size + other.size;
        newHeap.capacity = cap;
        System.arraycopy(arr, 0, newHeap.arr, 0, getSize());
        System.arraycopy(other.arr, 0, newHeap.arr, getSize(), other.getSize());
        for (int i = this.size; i < newHeap.size; i++) {
            newHeap.siftUp(i);
        }
        return newHeap;
    }

    private void expand() {
        arr = Arrays.copyOf(arr, capacity * 2);
        capacity *= 2;
    }

    private void siftUp(int currentIndex) {
        clearTrace();
        visited = new int[4];
        visited[sizeVisited++] = currentIndex;
        while (currentIndex > 0) {
            int parent = getParent(currentIndex);
            checkVisited();
            visited[sizeVisited++] = parent;

            if (comparator.compare((T) arr[currentIndex], (T) arr[parent]) >= 0) {
                break;
            }
            swap(currentIndex, parent);
            currentIndex = parent;
        }
        lastIndex = currentIndex;
    }

    public int getSizeVisited() {
        return sizeVisited;
    }

    private void checkVisited() {
        if (sizeVisited >= visited.length) {
            visited = Arrays.copyOf(visited, sizeVisited * 2);
        }

    }

    private void swap(int index1, int index2) {
        T temp = (T) arr[index2];
        arr[index2] = arr[index1];
        arr[index1] = temp;
    }

    private void checkHeap() {
        if (size < 1) {
            throw new NoSuchElementException("Heap is empty");
        }
    }

    private int min(int index1, int index2) {
        if (index2 >= size) {
            return index1;
        }
        if (comparator.compare((T) arr[index1], (T) arr[index2]) > 0) {
            return index2;
        }
        return index1;
    }
}
