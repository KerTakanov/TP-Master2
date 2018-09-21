import diffusions.DiffusionAnneau;
import diffusions.DiffusionAnneauBi;
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

        int choix = 3;

        switch (choix) {
            case 1:
                DiffusionCentralisee.diffuser(0, "Centralisé");
                break;
            case 2:
                DiffusionAnneau.diffuser(6, "Anneau");
                break;
            case 3:
                DiffusionHypercube.diffuser(3, "Hypercube");
                break;
            case 4:
                DiffusionAnneauBi.diffuser(0, "Anneau Bidir");
        }


        MPI.Finalize();
    }
}
