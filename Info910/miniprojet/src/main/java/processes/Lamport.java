package processes;

public interface Lamport {
    /**
     * Acquire lock and increment clock
     * @return clock
     */
    int clock();

    /**
     * Acquire lock and set clock to 1 + max(actual, stamp)
     * @param stamp
     * @return
     */
    int clock(int stamp);
}
