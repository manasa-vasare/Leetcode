import java.util.*;

class Solution {

    static class State {
        int r, c, energy, mask, moves;

        State(int r, int c, int energy, int mask, int moves) {
            this.r = r;
            this.c = c;
            this.energy = energy;
            this.mask = mask;
            this.moves = moves;
        }
    }

    public int minMoves(String[] classroom, int energy) {

        int m = classroom.length;
        int n = classroom[0].length();

        int sr = 0;
        int sc = 0;

        int litterCount = 0;

        // Store the ID of every litter cell
        int[][] id = new int[m][n];

        for (int i = 0; i < m; i++) {
            Arrays.fill(id[i], -1);

            for (int j = 0; j < n; j++) {

                char ch = classroom[i].charAt(j);

                if (ch == 'S') {
                    sr = i;
                    sc = j;
                }

                if (ch == 'L') {
                    id[i][j] = litterCount++;
                }
            }
        }

        // No litter -> already done
        if (litterCount == 0) {
            return 0;
        }

        // If there are k litter cells:
        // allCollected = 111...111
        int allCollected = (1 << litterCount) - 1;

        /*
         * best[r][c][mask]
         *
         * Maximum energy with which we have reached
         * cell (r,c), having collected the litter
         * represented by mask.
         */
        int[][][] best = new int[m][n][1 << litterCount];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Arrays.fill(best[i][j], -1);
            }
        }

        Queue<State> queue = new ArrayDeque<>();

        // Start state
        queue.offer(new State(sr, sc, energy, 0, 0));

        best[sr][sc][0] = energy;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {

            State cur = queue.poll();

            // Try 4 directions
            for (int d = 0; d < 4; d++) {

                int nr = cur.r + dr[d];
                int nc = cur.c + dc[d];

                // Outside grid
                if (nr < 0 || nr >= m ||
                    nc < 0 || nc >= n) {
                    continue;
                }

                char cell = classroom[nr].charAt(nc);

                // Cannot cross obstacle
                if (cell == 'X') {
                    continue;
                }

                // Every move costs 1 energy
                if (cur.energy == 0) {
                    continue;
                }

                int newEnergy = cur.energy - 1;
                int newMask = cur.mask;

                // If we enter litter cell, collect it
                if (cell == 'L') {
                    int litterId = id[nr][nc];

                    newMask |= (1 << litterId);
                }

                // Reset energy on R
                if (cell == 'R') {
                    newEnergy = energy;
                }

                int newMoves = cur.moves + 1;

                // All litter collected
                if (newMask == allCollected) {
                    return newMoves;
                }

                /*
                 * If we have already reached this same
                 * cell with the same mask and at least
                 * as much energy, this state is useless.
                 */
                if (best[nr][nc][newMask] >= newEnergy) {
                    continue;
                }

                best[nr][nc][newMask] = newEnergy;

                queue.offer(
                    new State(
                        nr,
                        nc,
                        newEnergy,
                        newMask,
                        newMoves
                    )
                );
            }
        }

        return -1;
    }
}