package diffusions;

import mpi.MPI;

public class DiffusionAnneauBi {
    public static void diffuser(int sender, String message) {
        String[] buffer = {message};
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int last = (size / 2 + me) % size;

        if (me == sender) {
            boolean forward = me % 2 == 0;

        }
    }

    private static int next(int me, int size, boolean forward) {
        int next;

        if (forward) {
            next = (me + 1) % size;
        } else {
            next = me - 1 < 0 ? size - 1 : me - 1;
        }

        return next;
    }
}
