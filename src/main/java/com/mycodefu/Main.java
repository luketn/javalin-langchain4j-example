package com.mycodefu;

import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import io.javalin.http.staticfiles.Location;

import java.util.concurrent.ConcurrentLinkedQueue;

import static io.javalin.apibuilder.ApiBuilder.get;

public class Main {
    public static void main(String[] args) {
        var clients = new ConcurrentLinkedQueue<SseClient>();
        Javalin.create(config -> {
                    config.staticFiles.add("/public", Location.CLASSPATH);
                })
                .sse("/stream", client -> {
                    clients.add(client);
                    client.sendEvent("Hekki");
                    client.onClose(() -> clients.remove(client));
                })
                .start(7070);
    }
}