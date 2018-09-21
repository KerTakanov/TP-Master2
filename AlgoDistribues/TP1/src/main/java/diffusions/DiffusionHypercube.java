package diffusions;

import mpi.MPI;
import mpi.Status;

public class DiffusionHypercube {

    /*
    http://3nity.io/~vj/downloads/publications/europar05_hypercubes.pdf
     */

    public static void diffuser(int sender, String message) {
        String[] buffer = new String[2];
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int dimension;

        int dest;

        if (me != sender) {
            Status status = MPI.COMM_WORLD.Recv(buffer, 0, 2, MPI.OBJECT, MPI.ANY_SOURCE, 99);
            System.out.println(String.format("%s received buf [%s, %s] from %d", me, buffer[0], buffer[1], status.source));
            dimension = Integer.parseInt(buffer[1]);
        } else {
            dimension = 0;
        }

        do {
            dest = me + (int) Math.pow(2, dimension);

            if (dest < size) {
                dimension++;

                String[] _buffer = { message, Integer.toString(dimension) };
                MPI.COMM_WORLD.Isend(_buffer, 0, 2, MPI.OBJECT, dest, 99);
            }
        } while (dest < size);
    }
}
