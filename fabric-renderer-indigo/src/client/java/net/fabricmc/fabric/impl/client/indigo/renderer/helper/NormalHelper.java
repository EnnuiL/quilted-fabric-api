/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2022 The Quilt Project
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

package net.fabricmc.fabric.impl.client.indigo.renderer.helper;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;

/**
 * Static routines of general utility for renderer implementations.
 * Renderers are not required to use these helpers, but they were
 * designed to be usable without the default renderer.
 */
public abstract class NormalHelper {
	private NormalHelper() { }

	private static final float PACK = 127.0f;
	private static final float UNPACK = 1.0f / PACK;

	/**
	 * Stores a normal plus an extra value as a quartet of signed bytes.
	 * This is the same normal format that vanilla rendering expects.
	 * The extra value is for use by shaders.
	 */
	public static int packNormal(float x, float y, float z, float w) {
		x = MathHelper.clamp(x, -1, 1);
		y = MathHelper.clamp(y, -1, 1);
		z = MathHelper.clamp(z, -1, 1);
		w = MathHelper.clamp(w, -1, 1);

		return ((int) (x * PACK) & 0xFF) | (((int) (y * PACK) & 0xFF) << 8) | (((int) (z * PACK) & 0xFF) << 16) | (((int) (w * PACK) & 0xFF) << 24);
	}

	/**
	 * Version of {@link #packNormal(float, float, float, float)} that accepts a vector type.
	 */
	public static int packNormal(Vector3f normal, float w) {
		return packNormal(normal.x(), normal.y(), normal.z(), w);
	}

	/**
	 * Like {@link #packNormal(float, float, float, float)}, but without a {@code w} value.
	 */
	public static int packNormal(float x, float y, float z) {
		x = MathHelper.clamp(x, -1, 1);
		y = MathHelper.clamp(y, -1, 1);
		z = MathHelper.clamp(z, -1, 1);

		return ((int) (x * PACK) & 0xFF) | (((int) (y * PACK) & 0xFF) << 8) | (((int) (z * PACK) & 0xFF) << 16);
	}

	/**
	 * Like {@link #packNormal(Vector3f, float)}, but without a {@code w} value.
	 */
	public static int packNormal(Vector3f normal) {
		return packNormal(normal.x(), normal.y(), normal.z());
	}

	public static float unpackNormalX(int packedNormal) {
		return ((byte) (packedNormal & 0xFF)) * UNPACK;
	}

	public static float unpackNormalY(int packedNormal) {
		return ((byte) ((packedNormal >>> 8) & 0xFF)) * UNPACK;
	}

	public static float unpackNormalZ(int packedNormal) {
		return ((byte) ((packedNormal >>> 16) & 0xFF)) * UNPACK;
	}

	public static float unpackNormalW(int packedNormal) {
		return ((byte) ((packedNormal >>> 24) & 0xFF)) * UNPACK;
	}

	public static void unpackNormal(int packedNormal, Vector3f target) {
		target.set(unpackNormalX(packedNormal), unpackNormalY(packedNormal), unpackNormalZ(packedNormal));
	}

	/**
	 * Computes the face normal of the given quad and saves it in the provided non-null vector.
	 * If {@link QuadView#nominalFace()} is set will optimize by confirming quad is parallel to that
	 * face and, if so, use the standard normal for that face direction.
	 *
	 * <p>Will work with triangles also. Assumes counter-clockwise winding order, which is the norm.
	 * Expects convex quads with all points co-planar.
	 */
	public static void computeFaceNormal(@NotNull Vector3f saveTo, QuadView q) {
		final Direction nominalFace = q.nominalFace();

		if (nominalFace != null && GeometryHelper.isQuadParallelToFace(nominalFace, q)) {
			Vec3i vec = nominalFace.getVector();
			saveTo.set(vec.getX(), vec.getY(), vec.getZ());
			return;
		}

		final float x0 = q.x(0);
		final float y0 = q.y(0);
		final float z0 = q.z(0);
		final float x1 = q.x(1);
		final float y1 = q.y(1);
		final float z1 = q.z(1);
		final float x2 = q.x(2);
		final float y2 = q.y(2);
		final float z2 = q.z(2);
		final float x3 = q.x(3);
		final float y3 = q.y(3);
		final float z3 = q.z(3);

		final float dx0 = x2 - x0;
		final float dy0 = y2 - y0;
		final float dz0 = z2 - z0;
		final float dx1 = x3 - x1;
		final float dy1 = y3 - y1;
		final float dz1 = z3 - z1;

		float normX = dy0 * dz1 - dz0 * dy1;
		float normY = dz0 * dx1 - dx0 * dz1;
		float normZ = dx0 * dy1 - dy0 * dx1;

		float l = (float) Math.sqrt(normX * normX + normY * normY + normZ * normZ);

		if (l != 0) {
			normX /= l;
			normY /= l;
			normZ /= l;
		}

		saveTo.set(normX, normY, normZ);
	}
}
