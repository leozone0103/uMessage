package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.locks.Lock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
//    double cellWidth, cellHeight;
    int[][] populationGrid;
    Lock[][] lockGrid;


//    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
//                                  double cellWidth, double cellHeight, int[][] popGrid, Lock[][] lockGrid) {
//        throw new NotYetImplementedException();
//    }
    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                              int[][] popGrid, Lock[][] lockGrid) {
//    throw new NotYetImplementedException();
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;
        this.corners = corners;
    }

    @Override
    public void run() {
//        throw new NotYetImplementedException();
        int longitudeIndex, latitudeIndex;
        double longitudeIndexDouble, latitudeIndexDouble;
        for (int i = lo; i < hi; i++) {
            longitudeIndexDouble = (censusGroups[i].longitude - corners.west) * numColumns / (corners.east - corners.west) + 1;
            longitudeIndex = (int) longitudeIndexDouble + (Double.compare(longitudeIndexDouble, (int) longitudeIndexDouble + 1) == 0 ? 1 : 0);
            longitudeIndex = longitudeIndex == numColumns + 1 ? longitudeIndex - 1 : longitudeIndex;

            latitudeIndexDouble = (censusGroups[i].latitude - corners.south) * numRows / (corners.north - corners.south) + 1;
            latitudeIndex = (int) latitudeIndexDouble + (Double.compare(latitudeIndexDouble, (int) latitudeIndexDouble + 1) == 0 ? 1 : 0);
            latitudeIndex = latitudeIndex == numRows + 1 ? latitudeIndex - 1 : latitudeIndex;
            lockGrid[latitudeIndex][longitudeIndex].lock();
            populationGrid[latitudeIndex][longitudeIndex] += censusGroups[i].population;
            lockGrid[latitudeIndex][longitudeIndex].unlock();
        }
    }
}
