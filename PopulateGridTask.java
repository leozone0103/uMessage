package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;
import queryresponders.ComplexSequential;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
//    double cellWidth, cellHeight;

//    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
//        throw new NotYetImplementedException();
//    }

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners) {
//        throw new NotYetImplementedException();
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = corners;
    }

    @Override
    protected int[][] compute() {
//        throw new NotYetImplementedException();
        if (this.hi - this.lo <= SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid();
        }
        int mid = (this.lo + this.hi) / 2;
        PopulateGridTask left = new PopulateGridTask(censusGroups, this.lo, mid, this.numRows, this.numColumns, corners);
        PopulateGridTask right = new PopulateGridTask(censusGroups, mid, this.hi, this.numRows, this.numColumns, corners);
        left.fork();
        int[][] rightGrid = right.compute();
        int[][] leftGrid = left.join();
        POOL.invoke(new MergeGridTask(leftGrid, rightGrid, 1, this.numRows + 1, 1, this.numColumns + 1));
        return leftGrid;
    }

    private int[][] sequentialPopulateGrid() {
//        throw new NotYetImplementedException();
        return ComplexSequential.createGridComplexSequential(this.censusGroups, this.corners, this.numColumns, this.numRows, this.lo, this.hi);
    }
}

