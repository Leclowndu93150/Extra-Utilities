package com.leclowndu93150.extrautils2.client.ctm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ConnectedTexturesHelper {
    public static int[] textureFromArrangement;
    public static boolean[] isAdvancedArrangement;
    public static int[] textureIds;
    static int[] sideA;
    static int[] sideB;
    static int[] corner;
    static int[][] cornerTex;
    static int[] trueTextures;
    public static int[][][] texBounds;

    static {
        init();
    }

    private ConnectedTexturesHelper() {}

    public static void init() {
        sideA = new int[]{1, 4, 4, 1};
        sideB = new int[]{2, 2, 8, 8};
        corner = new int[]{16, 32, 64, 128};
        cornerTex = new int[47][4];
        texBounds = new int[47][][];
        textureFromArrangement = new int[256];
        isAdvancedArrangement = new boolean[16];
        textureIds = new int[47];
        int j = 0;
        boolean[] validTexture = new boolean[625];
        int[] revTextureIds = new int[625];
        int[] k = new int[]{1, 5, 25, 125};
        HashMap<Long, Integer> texToArrangement = new HashMap<>();

        for (int ar = 0; ar < 256; ar++) {
            int texId = 0;
            int[] t = new int[4];

            for (int i = 0; i < 4; i++) {
                boolean sa = (ar & sideA[i]) != 0;
                boolean sb = (ar & sideB[i]) != 0;
                boolean c = (ar & corner[i]) != 0;
                int tex = getTex(sa, sb, c);
                t[i] = tex;
                texId += tex * k[i];
                if (!sa && !sb) {
                    isAdvancedArrangement[ar & 15] = true;
                }
            }

            if (!validTexture[texId]) {
                texToArrangement.put(intArrKey(t), ar);
                textureIds[j] = texId;
                cornerTex[j] = t;
                revTextureIds[texId] = j;
                validTexture[texId] = true;
                j++;
            }

            textureFromArrangement[ar] = revTextureIds[texId];
        }

        Set<Integer> definites = new LinkedHashSet<>();

        for (int ix = 0; ix < 5; ix++) {
            definites.add(texToArrangement.get(intArrKey(ix, ix, ix, ix)));
        }

        definites.add(texToArrangement.get(intArrKey(3, 4, 3, 4)));
        definites.add(texToArrangement.get(intArrKey(4, 3, 4, 3)));
        definites.add(makeArrangementFull(false, true, true, true));
        definites.add(makeArrangementFull(true, false, true, true));
        definites.add(makeArrangementFull(true, true, false, true));
        definites.add(makeArrangementFull(true, true, true, false));
        definites.add(makeArrangementEmpty(true, false, false, false));
        definites.add(makeArrangementEmpty(false, true, false, false));
        definites.add(makeArrangementEmpty(false, false, true, false));
        definites.add(makeArrangementEmpty(false, false, false, true));
        definites.add(makeArrangementFull(false, true, false, true));
        definites.add(makeArrangementFull(false, true, true, false));
        definites.add(makeArrangementFull(true, false, false, true));
        definites.add(makeArrangementFull(true, false, true, false));
        definites.add(makeArrangementEmpty(false, true, false, true));
        definites.add(makeArrangementEmpty(false, true, true, false));
        definites.add(makeArrangementEmpty(true, false, false, true));
        definites.add(makeArrangementEmpty(true, false, true, false));

        int[] list = definites.stream().mapToInt(ix -> textureFromArrangement[ix]).toArray();
        Set<Integer> listSet = new LinkedHashSet<>();
        for (int v : list) listSet.add(v);
        trueTextures = list;

        HashMap<Integer, Integer> horizUp = new HashMap<>();
        HashMap<Integer, Integer> horizDown = new HashMap<>();
        int ul = 0;
        int dl = 1;
        int dr = 2;
        int ur = 3;

        for (int trueTexture : trueTextures) {
            int[] tex = cornerTex[trueTexture];
            horizUp.putIfAbsent(tex[ul] * 8 + tex[ur], trueTexture);
            horizDown.putIfAbsent(tex[dl] * 8 + tex[dr], trueTexture);
        }

        for (int ix = 0; ix < 47; ix++) {
            if (listSet.contains(ix)) {
                texBounds[ix] = new int[][]{{ix, 0, 0, 16, 16}};
            } else {
                int[] tex = cornerTex[ix];
                int hu = horizUp.getOrDefault(tex[ul] * 8 + tex[ur], -1);
                int hd = horizDown.getOrDefault(tex[dl] * 8 + tex[dr], -1);
                if (hu < 0 || hd < 0) {
                    throw new IllegalStateException();
                }
                texBounds[ix] = new int[][]{{hu, 0, 0, 16, 8}, {hd, 0, 8, 16, 16}};
            }
        }
    }

    private static long intArrKey(int... values) {
        return ((long) Arrays.hashCode(values) << 32) | (values[0] & 0xFFL) << 24 | (values[1] & 0xFFL) << 16 | (values[2] & 0xFFL) << 8 | (values[3] & 0xFFL);
    }

    private static int makeArrangementEmpty(boolean l, boolean r, boolean u, boolean d) {
        return makeArrangement(l, r, u, d, false, false, false, false);
    }

    private static int makeArrangementFull(boolean l, boolean r, boolean u, boolean d) {
        return makeArrangement(l, r, u, d, true, true, true, true);
    }

    private static int makeArrangement(boolean l, boolean r, boolean u, boolean d, boolean ul, boolean ur, boolean dl, boolean dr) {
        int t = 0;
        if (l) t |= 1;
        if (u) t |= 2;
        if (r) t |= 4;
        if (d) t |= 8;
        if (ul) t |= 16;
        if (ur) t |= 32;
        if (dr) t |= 64;
        if (dl) t |= 128;
        return t;
    }

    private static int getTex(boolean sideA, boolean sideB, boolean corner) {
        return sideA ? (sideB ? 0 : 1) : (sideB ? 2 : (corner ? 3 : 4));
    }
}
