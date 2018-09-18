package diffusions;

import mpi.MPI;

public class DiffusionAnneau {
    public static void diffuser(int sender, String message) {
        String[] buffer = {message};
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (me != sender) {
            int prec = me - 1 < 0 ? size - 1 : me - 1;
            MPI.COMM_WORLD.Recv(buffer, 0, 1, MPI.OBJECT, prec, 99);

            System.out.println(String.format("%d: received `%s` from %d", me, buffer[0], prec));

            int next = (me + 1) % size;
            if (next != sender) {
                MPI.COMM_WORLD.Isend(buffer, 0, 1, MPI.OBJECT, next, 99);
            }
        } else {
            int next = (me + 1) % size;
            MPI.COMM_WORLD.Isend(buffer, 0, 1, MPI.OBJECT, next, 99);
        }
    }
}
