package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi;
    double w, s, e, n;
    MapCorners corners;
    int col, row;

    public GetPopulationTask(CensusGroup[] censusGroups, MapCorners corners, int col, int row, int lo, int hi, double w, double s, double e, double n) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this. hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.corners = corners;
        this.row = row;
        this.col = col;
    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if(hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w,s,e,n);
        } else {
            int mid = lo + (hi - lo) / 2;
            GetPopulationTask left = new GetPopulationTask(censusGroups, corners, col, row, lo, mid, w,s,e,n);
            GetPopulationTask right = new GetPopulationTask(censusGroups, corners, col, row, mid, hi, w,s,e,n);
            right.fork();
            int leftRes = left.compute();
            int rightRes = right.join();
            return leftRes + rightRes;
        }
    }

    private Integer sequentialGetPopulation(CensusGroup[] data, int lo, int hi, double west, double south, double east, double north) {
        int population = 0;
        for (int i = lo; i < hi; i++) {
            if (Double.compare(corners.west * (col - west + 1) + corners.east * (west - 1), data[i].longitude * col) <= 0 &&
                    (east == col || Double.compare(corners.west * (col - east) + corners.east * east, data[i].longitude * col) > 0) &&
                    Double.compare(corners.south * (row - south + 1) + corners.north * (south - 1), data[i].latitude * row) <= 0 &&
                    (north == row || Double.compare(corners.south * (row - north) + corners.north * north, data[i].latitude * row) > 0)) {
                population += data[i].population;
            }
        }
        return population;
    }
}
