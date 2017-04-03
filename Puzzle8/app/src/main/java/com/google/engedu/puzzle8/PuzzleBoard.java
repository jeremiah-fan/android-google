/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles = new ArrayList<>();
    private int steps;
    private PuzzleBoard prevBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, false);
        for(int num = 0; num < NUM_TILES * NUM_TILES - 1; num++) {
            //tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled, 0, 0, parentWidth / NUM_TILES, parentWidth / NUM_TILES, null, false), num));
            //Bitmap.createBitmap(bitmap, 0, 0, 25, 25);
            //tiles.add(new PuzzleTile(Bitmap.createScaledBitmap(bitmap, parentWidth / 3, parentWidth /3, false), num));
            tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,
                    (num % NUM_TILES) * (parentWidth / NUM_TILES),
                    (num / NUM_TILES) * (parentWidth / NUM_TILES),
                    parentWidth / NUM_TILES,
                    parentWidth / NUM_TILES,
                    null,
                    false), num));
        }
        tiles.add(null);
        steps = 0;
        prevBoard = null;
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        prevBoard = otherBoard;
    }

    public void reset() {
        steps = 0;
        prevBoard = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> neighbor = new ArrayList<>();
        int nulltile = -1;
        for(int i = 0; i < NUM_TILES * NUM_TILES; i++){
            PuzzleTile tile = tiles.get(i);
            if(tile == null) {
                nulltile = i;
                break;
            }
        }

        if (nulltile == -1)
            return null;

        for(int[] direction: NEIGHBOUR_COORDS){
            // Think of an elegant solution
            int collapsedindex = nulltile + direction[0] + direction[1] * NUM_TILES;
            if(collapsedindex >= 0 && collapsedindex < NUM_TILES * NUM_TILES && !(direction[1] == 0 && collapsedindex / NUM_TILES != nulltile / NUM_TILES)){
                PuzzleBoard copy = new PuzzleBoard(this);
                copy.swapTiles(collapsedindex, nulltile);
                neighbor.add(copy);
            }
        }
        return neighbor;
    }

    public int priority() {
        int manhattan = steps;
        for(int i = 0; i < NUM_TILES * NUM_TILES; i++){
            PuzzleTile tile;
            if((tile = tiles.get(i)) != null) {
                int flattendist = Math.abs(tile.getNumber() - i);
                manhattan += flattendist / NUM_TILES + flattendist % NUM_TILES;
            }
        }
        return manhattan;
    }

    public PuzzleBoard getPrevBoard(){
        return prevBoard;
    }
}
