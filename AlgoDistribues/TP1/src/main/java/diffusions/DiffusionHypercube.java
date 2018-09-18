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
            MPI.COMM_WORLD.Recv(buffer, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 99);
            System.out.println(String.format("%s received buf [%s, %s]", me, buffer[0], buffer[1]));
            dimension = Integer.parseInt(buffer[1]);
        } else {
            buffer[0] = message;
            dimension = 0;
            buffer[1] = "0";
        }

        do {
            dest = me + (int) Math.pow(2, dimension);

            if (dest < size) {
                dimension++;
                buffer[1] = Integer.toString(dimension);
                System.out.println(String.format("Buffer[1]=%s", buffer[1]));
                buffer[0] = "oskdqods";

                System.out.println(String.format("%s sending buf [%s, %s]", me, buffer[0], buffer[1]));

                MPI.COMM_WORLD.Isend(buffer, 0, 1, MPI.OBJECT, dest, 99);
            }
        } while (dest < size);
    }
}
