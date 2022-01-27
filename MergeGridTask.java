package paralleltasks;

import cse332.exceptions.NotYetImplementedException;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    final static int SEQUENTIAL_CUTOFF = 10;
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;

    // Merge right to left
    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
//        throw new NotYetImplementedException();
        this.left = left;
        this.right = right;
        this.rowLo = rowLo;
        this.rowHi = rowHi;
        this.colLo = colLo;
        this.colHi = colHi;
    }

    @Override
    protected void compute() {
//        throw new NotYetImplementedException();
        if ((this.rowHi - this.rowLo) * (this.colHi - this.colLo) <= SEQUENTIAL_CUTOFF) {
            sequentialMergeGird();
        } else {
            int rowMid = (this.rowLo + this.rowHi) / 2;
            int colMid = (this.colLo + this.colHi) / 2;
            MergeGridTask[] subtasks = new MergeGridTask[4];
            subtasks[0] = new MergeGridTask(left,right,this.rowLo,rowMid,this.colLo,colMid);
            subtasks[1] = new MergeGridTask(left,right,this.rowLo,rowMid,colMid,this.colHi);
            subtasks[2] = new MergeGridTask(left,right,rowMid,this.rowHi,this.colLo,colMid);
            subtasks[3] = new MergeGridTask(left,right,rowMid,this.rowHi,colMid,this.colHi);
            for (int i = 1; i <= 3; i++) {
                subtasks[i].fork();
            }
            subtasks[0].compute();
            for (int i = 1; i <= 3; i++) {
                subtasks[i].join();
            }
        }
    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private void sequentialMergeGird() {
//        throw new NotYetImplementedException();
        for (int i = this.rowLo; i < this.rowHi; i++) {
            for (int j = this.colLo; j < this.colHi; j++) {
                this.left[i][j] += this.right[i][j];
            }
        }
    }
}
