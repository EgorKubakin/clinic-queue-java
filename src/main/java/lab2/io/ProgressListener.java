package lab2.io;

@FunctionalInterface
public interface ProgressListener {
    boolean update(long current, long total);
}