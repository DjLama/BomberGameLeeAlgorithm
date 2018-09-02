package com.geekbrains.game;

import com.badlogic.gdx.math.MathUtils;

public class Route {
    private Map map;
    private Direction d;
    private int[][] routeMap = new int[Map.MAP_CELLS_WIDTH][Map.MAP_CELLS_HEIGHT];
    private int[][] arr1 = new int[Map.MAP_CELLS_WIDTH * Map.MAP_CELLS_HEIGHT][2];
    private int[][] arr2 = new int[Map.MAP_CELLS_WIDTH * Map.MAP_CELLS_HEIGHT][2];
    int arr1Counter = 0;
    int arr2Counter = 0;
    int pointId = 0;

    public Route(Map map) {
        this.map = map;
    }

    enum Direction {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, 1), DOWN(0, -1);

        private int dx;
        private int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public void getRandomRoute(int[][] route, int cellX, int cellY) {
        Direction direction;
        do {
            direction = Direction.values()[MathUtils.random(0, 3)];
        }
        while (!map.isCellEmpty(cellX + direction.dx, cellY + direction.dy, CheckType.BaseMapObjectsBombCollision));
        route[0][0] = cellX + direction.dx;
        route[0][1] = cellY + direction.dy;
    }

    public boolean getRoute(int[][] route, int cellX, int cellY, int targetX, int targetY) {
        if (!map.isCellEmpty(cellX, cellY, CheckType.BaseMapObjects))
            return false;
        if (makeWave((byte) cellX, (byte) cellY, (byte) targetX, (byte) targetY)) {
            genRoute((byte) targetX, (byte) targetY);
            for (int i = 0; i < route.length; i++) {
                if (arr1Counter > 0)
                    route[i] = arr1[--arr1Counter];
                else
                    break;
            }
            return true;
        }
        return false;
    }

    private void genRoute(int pointX, int pointY) {
        arr1[0][0] = pointX;
        arr1[0][1] = pointY;
        arr1Counter = 1;

        while (true) {
            for (int i = 0; i < Direction.values().length; i++) {
                d = Direction.values()[i];
                if (pointX + d.dx < 0 || pointX + d.dx >= routeMap.length)
                    continue;
                if (pointY + d.dy < 0 || pointY + d.dy >= routeMap[0].length)
                    continue;
                if (routeMap[pointX + d.dx][pointY + d.dy] != -1
                        && routeMap[pointX + d.dx][pointY + d.dy] != -2
                        && routeMap[pointX + d.dx][pointY + d.dy] < routeMap[pointX][pointY]) {
                    pointX = pointX + d.dx;
                    pointY = pointY + d.dy;
                    if (routeMap[pointX][pointY] == 0)
                        return;
                    arr1[arr1Counter][0] = pointX;
                    arr1[arr1Counter][1] = pointY;
                    arr1Counter++;
                }
            }
        }
    }

    private boolean makeWave(int cellX, int cellY, int targetX, int targetY) {
        for (int i = 0; i < Map.MAP_CELLS_WIDTH; i++)
            for (int j = 0; j < Map.MAP_CELLS_HEIGHT; j++)
                routeMap[i][j] = map.isCellEmpty(i, j, CheckType.BaseMapObjectsBombCollision) ? -1 : -2;
        routeMap[cellX][cellY] = 0;
        arr1[0][0] = cellX;
        arr1[0][1] = cellY;
        arr1Counter = 1;
        arr2Counter = 0;
        pointId = 0;
        while (true) {
            for (int i = 0; i < arr1Counter; i++)
                wave(arr1[i][0], arr1[i][1]);
            if (arr2Counter == 0)
                return false;
            for (int i = 0; i < arr2Counter; i++) {
                if (arr2[i][0] == targetX && arr2[i][1] == targetY)
                    return true;
                arr1[i][0] = arr2[i][0];
                arr1[i][1] = arr2[i][1];
            }
            arr1Counter = arr2Counter;
            arr2Counter = 0;
            pointId++;
        }
    }

    void wave(int cellX, int cellY) {
        for (int i = 0; i < Direction.values().length; i++) {
            d = Direction.values()[i];
            if (cellX + d.dx < 0 || cellX + d.dx >= routeMap.length)
                continue;
            if (cellY + d.dy < 0 || cellY + d.dy >= routeMap[0].length)
                continue;
            if (routeMap[cellX + d.dx][cellY + d.dy] == -1) {
                routeMap[cellX + d.dx][cellY + d.dy] = (pointId + 1);
                arr2[arr2Counter][0] = (cellX + d.dx);
                arr2[arr2Counter][1] = (cellY + d.dy);
                arr2Counter++;
            }
        }
    }
}

