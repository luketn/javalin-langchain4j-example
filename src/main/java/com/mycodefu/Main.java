package com.mycodefu;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import static io.javalin.apibuilder.ApiBuilder.get;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
                    config.staticFiles.add("/public", Location.CLASSPATH);
                })
                .get("/stream", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}