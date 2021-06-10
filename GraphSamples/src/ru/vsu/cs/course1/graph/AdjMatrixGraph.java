package ru.vsu.cs.course1.graph;

import java.util.Arrays;
import java.util.Iterator;

public class AdjMatrixGraph implements Graph {

    private boolean[][] adjMatrix = null;
    private int vCount = 0;
    private int eCount = 0;

    public AdjMatrixGraph(int vertexCount) {
        adjMatrix = new boolean[vertexCount][vertexCount];
        vCount = vertexCount;
    }

    public AdjMatrixGraph() {
        this(0);
    }

    @Override
    public int vertexCount() {
        return vCount;
    }

    @Override
    public int edgeCount() {
        return eCount;
    }

    @Override
    public void addAdge(int v1, int v2) {
        int maxV = Math.max(v1, v2);
        if (maxV >= vertexCount()) {
            adjMatrix = Arrays.copyOf(adjMatrix, maxV + 1);
            for (int i = 0; i <= maxV; i++) {
                adjMatrix[i] = i < vCount ? Arrays.copyOf(adjMatrix[i], maxV + 1) : new boolean[maxV + 1];
            }
            vCount = maxV + 1;
        }
        if (!adjMatrix[v1][v2]) {
            adjMatrix[v1][v2] = true;
            eCount++;
            if (!(this instanceof Digraph)) {
                adjMatrix[v2][v1] = true;
            }
        }
    }

    @Override
    public void removeAdge(int v1, int v2) {
        if (adjMatrix[v1][v2]) {
            adjMatrix[v1][v2] = false;
            eCount--;
            if (!(this instanceof Digraph)) {
                adjMatrix[v2][v1] = false;
            }
        }
    }

    @Override
    public Iterable<Integer> adjacencies(int v) {
        return new Iterable<Integer>() {
            Integer nextAdj = null;

            @Override
            public Iterator<Integer> iterator() {
                for (int i = 0; i < vCount; i++) {
                    if (adjMatrix[v][i]) {
                        nextAdj = i;
                        break;
                    }
                }

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return nextAdj != null;
                    }

                    @Override
                    public Integer next() {
                        Integer result = nextAdj;
                        nextAdj = null;
                        for (int i = result + 1; i < vCount; i++) {
                            if (adjMatrix[v][i]) {
                                nextAdj = i;
                                break;
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    public int findMin() {
        int[] min = new int[1];
        min[0] = -1;
        for (int i = 0; i < vCount; i++) {
            findShortCycle(i, new boolean[vCount], i, min);
        }
        return min[0];
    }

    private void findShortCycle(int from, boolean[] wasCycle, int curr, int[] min) {
        if (from == curr && wasCycle[curr]) {
            int length = 0;
            for (boolean was : wasCycle) {
                if (was) {
                    length++;
                }
            }
            if ((min[0] == -1 || length < min[0]) && length != 2) {
                min[0] = length;
            }
            return;
        } else if (wasCycle[curr]) {
            return;
        }

        wasCycle[curr] = true;

        for (int i = 0; i < adjMatrix.length; i++) {
            if (adjMatrix[curr][i]) {
                findShortCycle(from, wasCycle.clone(), i, min);
            }
        }
    }
}
