package com.wen.commons.utils;

import java.io.Serializable;

public final class Pair<F, S> implements Serializable {
	private static final long serialVersionUID = 3817640235756061262L;

	public F first;
	public S second;

	public Pair() {
	}

	public Pair(F f, S s) {
		this.first = f;
		this.second = s;
	}

	/**
	 * 通过值创建值对
	 * 
	 * @param f
	 *            第一个值
	 * @param s
	 *            第二个值
	 * @return 值对
	 */
	public static <FT, ST> Pair<FT, ST> makePair(FT f, ST s) {
		return new Pair<>(f, s);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o)
	{
		Pair<F, S> pr = (Pair<F, S>) o;
		return pr != null && eq(first, pr.first) && eq(second, pr.second);
	}

	private static <T> boolean eq(T o1, T o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	@Override
	public String toString() {
		return "{" + first + ", " + second + "}";
	}
}