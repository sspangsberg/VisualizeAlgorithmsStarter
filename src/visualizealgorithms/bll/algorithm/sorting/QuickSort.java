package visualizealgorithms.bll.algorithm.sorting;

import visualizealgorithms.bll.algorithm.AlgorithmType;
import visualizealgorithms.bll.algorithm.GenericAlgorithm;

public class QuickSort extends GenericAlgorithm {


    /**
     */
    public QuickSort() {
        super("QuickSort", "Fast O(N log(N) sorting algorithm", AlgorithmType.SORTING);
    }

    @Override
    public void doWork() {

        Comparable[] arr = (Comparable[]) super.getData();

        sort(arr, 0, arr.length - 1);
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    private void sort(Comparable[] arr, int low, int high) {
        if (low < high) {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    /* This function takes last element as pivot,
       places the pivot element at its correct
       position in sorted array, and places all
       smaller (smaller than pivot) to left of
       pivot and all greater elements to right
       of pivot */
    private int partition(Comparable arr[], int low, int high) {
        Comparable pivot = arr[high];
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (arr[j].compareTo(pivot) > 0) {
                i++;

                // swap arr[i] and arr[j]
                Comparable temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        Comparable temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }



}
