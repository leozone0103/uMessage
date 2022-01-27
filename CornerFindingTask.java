package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class will do the corner finding from version 1 in parallel for use in versions 2, 4, and 5
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The compute method returns a result of a MapCorners and an Integer.
        - The MapCorners will represent the extremes/bounds/corners of the entire land mass (latitude and longitude)
        - The Integer value should represent the total population contained inside the MapCorners
 */

public class CornerFindingTask extends RecursiveTask<CornerFindingResult> {
    final int SEQUENTIAL_CUTOFF = 10000;
    CensusGroup[] censusGroups;
    int lo, hi;

    public CornerFindingTask(CensusGroup[] censusGroups, int lo, int hi) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
    }

    // Returns a pair of MapCorners for the grid and Integer for the total population
    // Key = grid, Value = total population
    @Override
    protected CornerFindingResult compute() {
        if(hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialCornerFinding(censusGroups, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            CornerFindingTask left = new CornerFindingTask(censusGroups, lo, mid);
            CornerFindingTask right = new CornerFindingTask(censusGroups, mid, hi);

            left.fork();
            CornerFindingResult rightRes = right.compute();
            CornerFindingResult leftRes = left.join();
            return new CornerFindingResult(leftRes.getMapCorners().encompass(rightRes.getMapCorners()),
                    leftRes.getTotalPopulation() + rightRes.getTotalPopulation());
        }
    }

    private CornerFindingResult sequentialCornerFinding(CensusGroup[] censusGroups, int lo, int hi) {
        MapCorners corners = new MapCorners(censusGroups[lo]);
        int totalPop = censusGroups[lo].population;
        for(int i = lo + 1; i < hi; i++) {
            corners = corners.encompass(new MapCorners(censusGroups[i]));
            totalPop += censusGroups[i].population;
        }
        return new CornerFindingResult(corners, totalPop);
    }

}

