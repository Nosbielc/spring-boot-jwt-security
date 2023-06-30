package com.nosbielc.app.infrastructure.shared.constants;

public final class PathsConstants {


    private PathsConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String PATH_API = "/api/v1/";
    public static final String PATH_MANAGEMENT = PATH_API.concat("management/");
    public static final String PATH_ADMIN = PATH_API.concat("admin/");

}
