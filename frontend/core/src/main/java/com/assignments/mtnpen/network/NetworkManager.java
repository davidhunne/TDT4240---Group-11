package com.assignments.mtnpen.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class NetworkManager {
    private static final String BASE_URL = "http://localhost:3000/api";
    private final Json json;

    public NetworkManager() {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
    }

    public void createGame(String playerId, String displayName, final NetworkCallback callback) {
        String url = BASE_URL + "/games";
        String content = "{\"playerId\":\"" + playerId + "\", \"displayName\":\"" + displayName + "\"}";

        sendRequest(Net.HttpMethods.POST, url, content, callback);
    }

    public void joinGame(String gameId, String playerId, String displayName, final NetworkCallback callback) {
        String url = BASE_URL + "/games/" + gameId + "/join";
        String content = "{\"playerId\":\"" + playerId + "\", \"displayName\":\"" + displayName + "\"}";

        sendRequest(Net.HttpMethods.POST, url, content, callback);
    }

    public void startGame(String gameId, String playerId, final NetworkCallback callback) {
        String url = BASE_URL + "/games/" + gameId + "/start";
        String content = "{\"playerId\":\"" + playerId + "\"}";

        sendRequest(Net.HttpMethods.POST, url, content, callback);
    }

    private void sendRequest(String method, String url, String content, final NetworkCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(method)
                .url(url)
                .header("Content-Type", "application/json")
                .content(content)
                .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                callback.onSuccess(httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                callback.onError(t);
            }

            @Override
            public void cancelled() {
                callback.onError(new Exception("Request cancelled"));
            }
        });
    }

    public interface NetworkCallback {
        void onSuccess(String response);
        void onError(Throwable t);
    }
}
