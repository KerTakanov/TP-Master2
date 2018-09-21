import diffusions.DiffusionAnneau;
import diffusions.DiffusionCentralisee;
import diffusions.DiffusionHypercube;
import mpi.MPI;
import mpi.Status;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int choix = 2;

        switch (choix) {
            case 1:
                DiffusionCentralisee.diffuser(0, "Centralisé");
                break;
            case 2:
                DiffusionAnneau.diffuser(6, "Anneau");
                break;
            case 3:
                DiffusionHypercube.diffuser(0, "Hypercube");
                break;
        }


        MPI.Finalize();
    }
}
