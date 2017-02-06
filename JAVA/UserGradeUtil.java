package com.smartmirror.advertisement.util;

public enum UserGradeUtil {
	MEMBER(0, "ROLE_MEMBER"),
	ANONYMOUS(-1, "ROLE_ANONYMOUS"),
	INVALID_LOGIN(-2, "INVALID_LOGIN"),
	NOT_CONNECT_API_SERVER(-100, "NOT_CONNECT_API_SERVER");

	public final int level;
	public final String gradeName;

	UserGradeUtil(int level, String gradeName) {
		this.level = level;
		this.gradeName = gradeName;
	}
}
