import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/** Data Structures 2: compare five sorting algorithms on the same inputs. */
public class SortingBenchmark {
    static final int N = 100_000;
    static final String[] ALGORITHMS = {
        "Selection Sort", "Insertion Sort", "Bubble Sort", "Merge Sort", "Quick Sort"
    };
    static final String[] CASES = {"Random", "Reverse", "Partial (80%)", "Sorted"};

    public static void main(String[] args) {
        int[][] inputs = createInputs();
        long[][] times = new long[ALGORITHMS.length][CASES.length];

        System.out.println("Sorting benchmark: N = " + N);
        System.out.println("Quadratic sorts may take a while. Times are in microseconds.");
        for (int algorithm = 0; algorithm < ALGORITHMS.length; algorithm++) {
            for (int test = 0; test < CASES.length; test++) {
                // Copy BEFORE starting the timer so every sort gets identical data.
                int[] copy = Arrays.copyOf(inputs[test], N);
                long start = System.nanoTime();
                sort(copy, algorithm);
                long end = System.nanoTime();
                times[algorithm][test] = (end - start) / 1000;
                // Checking correctness is also outside the measured interval.
                if (!isSorted(copy)) {
                    throw new IllegalStateException(ALGORITHMS[algorithm] + " failed");
                }
            }
            System.out.println("Finished " + ALGORITHMS[algorithm]);
        }
        System.out.printf("%-18s %14s %14s %14s %14s%n",
                "Algorithm", CASES[0], CASES[1], CASES[2], CASES[3]);
        for (int i = 0; i < ALGORITHMS.length; i++) {
            System.out.printf(Locale.US, "%-18s %,14d %,14d %,14d %,14d%n",
                    ALGORITHMS[i], times[i][0], times[i][1], times[i][2], times[i][3]);
        }
    }

    static int[][] createInputs() {
        int[][] inputs = new int[4][N];
        Random random = new Random(42); // Fixed seed makes input reproducible.
        for (int i = 0; i < N; i++) {
            inputs[0][i] = random.nextInt(1_000_000);
        }
        int[] ordered = Arrays.copyOf(inputs[0], N);
        // Library sort is used ONLY to prepare cases, never as a benchmark algorithm.
        Arrays.sort(ordered);
        inputs[3] = ordered;
        for (int i = 0; i < N; i++) {
            inputs[1][i] = ordered[N - 1 - i];
        }
        inputs[2] = Arrays.copyOf(ordered, N);
        // Keep the first 80% sorted and shuffle the remaining 20%.
        int boundary = N * 80 / 100;
        for (int i = N - 1; i > boundary; i--) {
            int j = boundary + random.nextInt(i - boundary + 1);
            swap(inputs[2], i, j);
        }
        return inputs;
    }

    static void sort(int[] a, int algorithm) {
        switch (algorithm) {
            case 0: selectionSort(a); break;
            case 1: insertionSort(a); break;
            case 2: bubbleSort(a); break;
            case 3: mergeSort(a, 0, a.length - 1); break;
            case 4: quickSort(a, 0, a.length - 1); break;
            default: throw new IllegalArgumentException("Unknown algorithm");
        }
    }

    static void selectionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int minimum = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[minimum]) minimum = j;
            }
            swap(a, i, minimum);
        }
    }

    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    static void bubbleSort(int[] a) {
        for (int end = a.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int j = 0; j < end; j++) {
                if (a[j] > a[j + 1]) {
                    swap(a, j, j + 1);
                    swapped = true;
                }
            }
            // A pass without swaps means the whole array is already sorted.
            if (!swapped) break;
        }
    }

    static void mergeSort(int[] a, int left, int right) {
        if (left >= right) return;
        int middle = left + (right - left) / 2;
        mergeSort(a, left, middle);
        mergeSort(a, middle + 1, right);
        merge(a, left, middle, right);
    }

    static void merge(int[] a, int left, int middle, int right) {
        int[] temporary = new int[right - left + 1];
        int i = left, j = middle + 1, k = 0;
        while (i <= middle && j <= right) {
            if (a[i] <= a[j]) temporary[k++] = a[i++];
            else temporary[k++] = a[j++];
        }
        while (i <= middle) temporary[k++] = a[i++];
        while (j <= right) temporary[k++] = a[j++];
        for (k = 0; k < temporary.length; k++) a[left + k] = temporary[k];
    }

    static void quickSort(int[] a, int low, int high) {
        if (low >= high) return;
        int i = low, j = high;
        // A middle-position pivot avoids the obvious end-pivot problem on sorted data.
        int pivot = a[low + (high - low) / 2];
        while (i <= j) {
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;
            if (i <= j) {
                swap(a, i, j);
                i++;
                j--;
            }
        }
        if (low < j) quickSort(a, low, j);
        if (i < high) quickSort(a, i, high);
    }

    static void swap(int[] a, int i, int j) {
        int temporary = a[i];
        a[i] = a[j];
        a[j] = temporary;
    }

    static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) return false;
        }
        return true;
    }
}
