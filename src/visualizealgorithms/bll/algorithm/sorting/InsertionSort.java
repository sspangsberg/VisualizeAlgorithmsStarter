package visualizealgorithms.bll.algorithm.sorting;

import visualizealgorithms.bll.algorithm.AlgorithmType;
import visualizealgorithms.bll.algorithm.GenericAlgorithm;

public class InsertionSort extends GenericAlgorithm  {

    public InsertionSort() {
        super("InsertionSort", "O(N^2) sorting algorithm", AlgorithmType.SORTING);
    }

    @Override
    public void doWork() {

        Comparable[]n = (Comparable[]) super.getData();

        //algorithm implementation...
        int i, j;
        Comparable key;

        for (i = 1; i < n.length - 1; i++)
        {
            key = n[i];
            j = i - 1;

        /* Move elements of arr[0..i-1], that are
        greater than key, to one position ahead
        of their current position */
            while (j >= 0 && n[j].compareTo(key) > 0)
            {
                n[j + 1] = n[j];
                j = j - 1;
            }
            n[j + 1] = key;
        }
    }
}
