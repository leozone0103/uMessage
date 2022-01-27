package paralleltasks;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelPrefixSum {
    public static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int CUTOFF = 1000;

    public static int[][] parallelPrefixSum(int[][] input) {
        if (input.length == 0) {
            return input;
        }
        return parallelPrefixSum(parallelPrefixSum(input, false), true);
    }

    public static int[][] parallelPrefixSum(int[][] input, boolean sumColumn) {
        int[][] arr = new int[input.length][input[0].length];

        if (sumColumn) {
            SumThisTask[] sumThisTasks = new SumThisTask[input[0].length];
            for (int i = 0; i < input[0].length; i++) {
                sumThisTasks[i] = new SumThisTask(input, arr, 0, arr.length, i, sumColumn);
            }
            for (int i = 1; i < input[0].length; i++) {
                sumThisTasks[i].fork();
            }
            sumThisTasks[0].compute();
            for (int i = 1; i < input[0].length; i++) {
                sumThisTasks[i].join();
            }

            AddLeftTask[] addLeftTasks = new AddLeftTask[input.length];
            for (int i = 0; i < input[0].length; i++) {
                addLeftTasks[i] = new AddLeftTask(input, arr, 0, 0, arr.length, i, sumColumn);
            }
            for (int i = 1; i < input[0].length; i++) {
                addLeftTasks[i].fork();
            }
            addLeftTasks[0].compute();
            for (int i = 1; i < input[0].length; i++) {
                addLeftTasks[i].join();
            }
        } else {
            SumThisTask[] sumThisTasks = new SumThisTask[input.length];
            for (int i = 0; i < input.length; i++) {
                sumThisTasks[i] = new SumThisTask(input, arr, 0, arr[i].length, i, sumColumn);
            }
            for (int i = 1; i < input.length; i++) {
                sumThisTasks[i].fork();
            }
            sumThisTasks[0].compute();
            for (int i = 1; i < input.length; i++) {
                sumThisTasks[i].join();
            }

            AddLeftTask[] addLeftTasks = new AddLeftTask[input.length];
            for (int i = 0; i < input.length; i++) {
                addLeftTasks[i] = new AddLeftTask(input, arr, 0, 0, arr[i].length, i, sumColumn);
            }
            for (int i = 1; i < input.length; i++) {
                addLeftTasks[i].fork();
            }
            addLeftTasks[0].compute();
            for (int i = 1; i < input.length; i++) {
                addLeftTasks[i].join();
            }
        }
        return arr;
    }

    private static class SumThisTask extends RecursiveAction {
        private final int[][] input, arr;
        private final int lo, hi, lineIndex;
        private final boolean sumColumn;

        public SumThisTask(int[][] input, int[][] arr, int lo, int hi, int lineIndex, boolean sumColumn) {
            this.input = input;
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.lineIndex = lineIndex;
            this.sumColumn = sumColumn;
        }

        protected void compute() {
            if (hi - lo <= CUTOFF) {
                sequentialSumThisTask(this.input, this.arr, this.lo, this.hi, lineIndex, sumColumn);
            } else {
                int mid = lo + (hi - lo) / 2;
                SumThisTask left = new SumThisTask(input, arr, lo, mid, lineIndex, sumColumn);
                SumThisTask right = new SumThisTask(input, arr, mid, hi, lineIndex, sumColumn);

                left.fork();
                right.compute();
                left.join();
                if (sumColumn) {
                    arr[hi - 1][lineIndex] = arr[mid - 1][lineIndex] + arr[hi - 1][lineIndex];
                } else {
                    arr[lineIndex][hi - 1] = arr[lineIndex][mid - 1] + arr[lineIndex][hi - 1];
                }
            }
        }
    }

    private static void sequentialSumThisTask(int[][] input, int[][] arr, int lo, int hi, int lineIndex, boolean sumColumn) {
        int sum = 0;
        if (sumColumn) {
            for (int i = lo; i < hi; i++) {
                sum += input[i][lineIndex];
            }
            arr[hi - 1][lineIndex] = sum;
        } else {
            for (int i = lo; i < hi; i++) {
                sum += input[lineIndex][i];
            }
            arr[lineIndex][hi - 1] = sum;
        }
    }

    private static class AddLeftTask extends RecursiveAction {
        private final int[][] input, arr;
        private final int fromLeft, lo, hi, lineIndex;
        private final boolean sumColumn;

        public AddLeftTask(int[][] input, int[][] arr, int fromLeft, int lo, int hi, int lineIndex, boolean sumColumn) {
            this.input = input;
            this.arr = arr;
            this.fromLeft = fromLeft;
            this.lo = lo;
            this.hi = hi;
            this.lineIndex = lineIndex;
            this.sumColumn = sumColumn;
        }

        protected void compute() {
            if (hi - lo <= CUTOFF) {
                sequentialAddLeftTask(this.input, this.arr, this.fromLeft, this.lo, this.hi, this.lineIndex, sumColumn);
            } else {
                int mid = lo + (hi - lo) / 2;
                AddLeftTask left = new AddLeftTask(input, arr, fromLeft, lo, mid, this.lineIndex, sumColumn);
                AddLeftTask right;
                if (sumColumn) {
                    right = new AddLeftTask(input, arr, fromLeft + arr[mid - 1][lineIndex], mid, hi, this.lineIndex, sumColumn);
                } else {
                    right = new AddLeftTask(input, arr, fromLeft + arr[lineIndex][mid - 1], mid, hi, this.lineIndex, sumColumn);
                }
                left.fork();
                right.compute();
                left.join();
            }
        }
    }

    private static void sequentialAddLeftTask(int[][] input, int[][] arr, int fromLeft, int lo, int hi, int lineIndex, boolean sumColumn) {
        int sum = fromLeft;
        if (sumColumn) {
            for (int i = lo; i < hi; i++) {
                sum += input[i][lineIndex];
                arr[i][lineIndex] = sum;
            }
        } else {
            for (int i = lo; i < hi; i++) {
                sum += input[lineIndex][i];
                arr[lineIndex][i] = sum;
            }
        }
    }
}
