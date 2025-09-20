package recursion;

/**
 * The Tower of Hanoi is a mathematical puzzle that consists of three rods and a
 * number of disks of various diameters, which can slide onto any rod.
 *
 * The puzzle starts with the disks stacked on one rod in order of decreasing
 * size, the smallest at the top. The objective is to move the entire stack to
 * another rod, obeying the following simple rules:
 * 1. Only one disk can be moved at a time.
 * 2. Each move consists of taking the upper disk from one of the stacks and
 *    placing it on top of another stack or on an empty rod.
 * 3. No disk may be placed on top of a smaller disk.
 *
 * The solution is naturally recursive.
 */
public class TowerOfHanoi {

    /**
     * Solves the Tower of Hanoi puzzle recursively.
     *
     * @param n           The number of disks to move.
     * @param source      The rod where the disks start.
     * @param destination The rod where the disks should end up.
     * @param auxiliary   The helper rod.
     */
    public static void solveHanoi(int n, char source, char destination, char auxiliary) {
        // Base case: If there is only one disk, move it directly.
        if (n == 1) {
            System.out.println("Move disk 1 from " + source + " to " + destination);
            return;
        }

        // 1. Move n-1 disks from source to auxiliary, using destination as the helper.
        solveHanoi(n - 1, source, auxiliary, destination);

        // 2. Move the nth disk from source to destination.
        System.out.println("Move disk " + n + " from " + source + " to " + destination);

        // 3. Move the n-1 disks from auxiliary to destination, using source as the helper.
        solveHanoi(n - 1, auxiliary, destination, source);
    }
    // Iterative solution can also be implemented, but the recursive approach is more straightforward for this problem.
    public static void solveHanoiIterative(int n, char source, char destination, char auxiliary) {
        List<Deque<Integer>> rods = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            rods.add(new ArrayDeque<>());
        }
        for (int i = n; i >= 1; i--) {
            rods.get(0).push(i);
        }
        int totalMoves = (1 << n) - 1; // 2^n - 1
        char[] rodNames = {source, destination, auxiliary};
        for (int i = 1; i <= totalMoves; i++) {
            int fromRod = (i & i - 1) % 3;
            int toRod = ((i | i - 1) + 1) % 3;
            if (rods.get(fromRod).isEmpty() || (!rods.get(toRod).isEmpty() && rods.get(fromRod).peek() > rods.get(toRod).peek())) {
                int disk = rods.get(toRod).pop();
                rods.get(fromRod).push(disk);
                System.out.println("Move disk " + disk + " from " + rodNames[toRod] + " to " + rodNames[fromRod]);
            } else {
                int disk = rods.get(fromRod).pop();
                rods.get(toRod).push(disk);
                System.out.println("Move disk " + disk + " from " + rodNames[fromRod    ] + " to " + rodNames[toRod]);
            }
        }
    }   


    public static void main(String[] args) {
        int numberOfDisks = 3;
        System.out.println("Solving Tower of Hanoi for " + numberOfDisks + " disks:");
        // A, B, C are the names of the rods.
        solveHanoi(numberOfDisks, 'A', 'C', 'B');
    }
}