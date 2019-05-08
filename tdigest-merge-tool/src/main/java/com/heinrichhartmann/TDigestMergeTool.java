package com.heinrichhartmann;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import com.tdunning.math.stats.TDigest;
import com.tdunning.math.stats.AVLTreeDigest;

/**
 * TDigest Merge Tool
 */
public class TDigestMergeTool
{
    // Constants
    static double TD_COMPRESSION = 10;

    // Helper
    static void printf(String fmt, Object... s) {
        System.out.printf(fmt, s);
    }

    static void errorf(String fmt, Object... s) {
        System.err.printf(fmt, s);
    }

    static void td_report(TDigest td, double[] quantiles) {
        printf("size\t%d\n", td.byteSize());
        for (double q : quantiles) {
         printf("q%f\t%f\n", q, td.quantile(q));
        }
    }

    public static void main(String[] args) {
        errorf("HistogramMergeTool\n");
        // parse arguments
        double[] quantiles = new double[args.length];
        for (int i=0; i<args.length; i++) {
            double q = Double.parseDouble(args[i]);
            errorf("- Input Quantile: %f\n", q);
            quantiles[i] = q;
        }
        // read input from stdin
        Scanner sc = new Scanner(System.in);
        TDigest td = null;
        List<TDigest> tdList = new LinkedList<TDigest>();
        int current_batch = -1;
        while (sc.hasNext()) {
            // printf("- %s\n", Rec.read(sc).toString());
            Rec rec = Rec.read(sc);
            if (rec.batch != current_batch) {
                if (td != null) {
                    td.compress();
                    // td_report(td);
                }
                // errorf("Starting next batch: %d\n", rec.batch);
                td = new AVLTreeDigest(TD_COMPRESSION);
                tdList.add(td);
                current_batch = rec.batch;
            }
            td.add(rec.val, 1);
        }
        td.compress();
        // MERGE
        errorf("Merging  %d batches\n", tdList.size());
        TDigest tdTotal = new AVLTreeDigest(TD_COMPRESSION);
        for (TDigest _td: tdList) {
            tdTotal.add(_td);
        }
        tdTotal.compress();
        td_report(tdTotal, quantiles);
    }
    static class Rec {
        int batch = 0;
        double val = 0;
        public String toString() {
            return String.format("Rec[%d]: %f", batch, val);
        }
        static Rec read(Scanner sc) {
            Rec r = new Rec();
            r.batch = sc.nextInt();
            r.val = sc.nextDouble();
            return r;
        }
    }
}
