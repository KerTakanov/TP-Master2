package diffusions;

import mpi.MPI;
import mpi.Status;

public class DiffusionCentralisee {
    public static void diffuser(int sender, String message) {
        String[] buffer = {message};
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (me == sender) {for (int i = 0; i < size; ++i) {
                MPI.COMM_WORLD.Isend(buffer, 0, 1, MPI.OBJECT, i, 99);
            }
        } else {
            MPI.COMM_WORLD.Recv(buffer, 0, 1, MPI.OBJECT, sender, 99);
            System.out.println(String.format("%s received `%s` from %s", me, buffer[0], sender));
        }
    }
}
