package diffusions;

import mpi.MPI;
import mpi.Status;

public class DiffusionAnneauBi {
    public static void diffuser(int sender, String message) {
        String[] buffer = new String[2];
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int last = (size / 2 + sender) % size;

        boolean forward = me % 2 == 0;

        buffer[0] = message;

        if (me == sender) {

            buffer[1] = "true";
            MPI.COMM_WORLD.Isend(buffer, 0, 2, MPI.OBJECT, next(me, size, forward), 99);

            buffer[1] = "false";
            MPI.COMM_WORLD.Isend(buffer, 0, 2, MPI.OBJECT, next(me, size, !forward), 99);
        } else {
            Status status = MPI.COMM_WORLD.Recv(buffer, 0, 2, MPI.OBJECT, MPI.ANY_SOURCE, 99);
            System.out.println(String.format("%d Received %s, %s from %d", me, buffer[0], buffer[1], status.source));
            forward = buffer[1].equals("true");

            int _next = next(me, size, forward);

            if (_next != last) {
                MPI.COMM_WORLD.Isend(buffer, 0, 2, MPI.OBJECT, next(me, size, forward), 99);
            } else if (me % 2 == 0) { // _next == last
                MPI.COMM_WORLD.Isend(buffer, 0, 2, MPI.OBJECT, next(me, size, forward), 99);
            }
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
