package com.mycodefu;

import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import io.javalin.http.sse.SseHandler;
import io.javalin.http.staticfiles.Location;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public record EventData(String message) { }
    public static void main(String[] args) {
        StreamingChatLanguageModel model = AnthropicStreamingChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(AnthropicChatModelName.CLAUDE_3_5_SONNET_20241022)
                .build();

        Javalin.create(config -> {
                    config.staticFiles.add("/public", Location.CLASSPATH);
                })
                .post("/stream", new SseHandler(sseClient -> {
                    sseClient.keepAlive();

                    String message = sseClient.ctx().queryParam("message");

                    sseClient.sendEvent("init", new EventData(message));

                    model.chat(message, new StreamingChatResponseHandler() {
                        @Override
                        public void onPartialResponse(String s) {
                            sseClient.sendEvent("message", new EventData(s));
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse chatResponse) {
                            sseClient.sendEvent("complete", new EventData("Done!"));
                        }

                        @Override
                        public void onError(Throwable error) {
                            sseClient.sendEvent("error", new EventData(error.getMessage()));
                        }
                    });
                }))
                .start(7070);
    }
}