
package visualizealgorithms.bll;

//Project imports
import visualizealgorithms.bll.algorithm.IAlgorithm;

/**
 * @author Søren Spangsberg
 */
public class TaskRunner {
    /**
     * Runs an algorithm with a set of Comparable data.
     * @param algorithm
     * @param data
     * @return
     */
    public long runTask(IAlgorithm algorithm, Comparable[] data) {


        algorithm.setData(data);

        long startTime = System.currentTimeMillis();
        algorithm.doWork();
        long durationInMillis = (System.currentTimeMillis() - startTime);

        return durationInMillis;
    }
}
