package io.github.guentherjulian.masterthesis.patterndetection.engine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MathUtil {

	public static int[][] multichooseMin1(int n, int k) {
		return multichoose(n - k, k, 1);
	}

	public static int[][] multichoose(int n, int k) {
		return multichoose(n, k, 0);
	}

	private static int[][] multichoose(int n, int k, int init) {
		if (n < 0 || k < 0) {
			return null;
		}

		if (n == 0) {
			int[][] newArray = new int[1][k];
			Arrays.fill(newArray[0], init);
			return newArray;
		}
		if (k == 0) {
			return new int[0][0];
		}

		if (k == 1) {
			int[][] result = new int[1][1];
			result[0][0] = n + init;
			return result;
		}

		int[][] arr = multichoose(n, k - 1, init);
		int[][] arr2 = multichoose(n - 1, k, init);
		List<int[]> out = new ArrayList<>();

		for (int i = 0; i < arr.length; i++) {
			int[] trans = new int[arr[i].length + 1];
			trans[0] = init;
			System.arraycopy(arr[i], 0, trans, 1, arr[i].length);
			out.add(trans);
		}

		for (int i = 0; i < arr2.length; i++) {
			arr2[i][0]++;
			out.add(arr2[i]);
		}

		return out.toArray(new int[0][0]);
	}

}
