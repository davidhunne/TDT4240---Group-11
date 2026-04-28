package com.assignments.mtnpen.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkManager {
    public static final String DEFAULT_BASE_URL = "http://localhost:3000/api";
    private final String baseUrl;
    private final JsonReader jsonReader;

    public NetworkManager() {
        this(DEFAULT_BASE_URL);
    }

    public NetworkManager(String baseUrl) {
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.jsonReader = new JsonReader();
    }

    public void upsertPlayer(String playerId, String displayName, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.POST, "/players",
                "{\"id\":\"" + escape(playerId) + "\",\"displayName\":\"" + escape(displayName) + "\"}",
                callback);
    }

    public void getPlayer(String playerId, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.GET, "/players/" + path(playerId), null, callback);
    }

    public void updateDisplayName(String playerId, String displayName, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.PATCH, "/players/" + path(playerId),
                "{\"displayName\":\"" + escape(displayName) + "\"}",
                callback);
    }

    public void getLeaderboard(int limit, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.GET, "/players/leaderboard/top?limit=" + limit, null, callback);
    }

    public void listLobbies(final NetworkCallback callback) {
        sendJson(Net.HttpMethods.GET, "/games/lobbies", null, callback);
    }

    public void createGame(String playerId, String displayName, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.POST, "/games",
                "{\"playerId\":\"" + escape(playerId) + "\",\"displayName\":\"" + escape(displayName) + "\"}",
                callback);
    }

    public void getGame(String gameId, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.GET, "/games/" + path(gameId), null, callback);
    }

    public void joinGame(String gameId, String playerId, String displayName, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.POST, "/games/" + path(gameId) + "/join",
                "{\"playerId\":\"" + escape(playerId) + "\",\"displayName\":\"" + escape(displayName) + "\"}",
                callback);
    }

    public void leaveGame(String gameId, String playerId, final NetworkCallback callback) {
        sendPlayerId(Net.HttpMethods.POST, "/games/" + path(gameId) + "/leave", playerId, callback);
    }

    public void startGame(String gameId, String playerId, final NetworkCallback callback) {
        sendPlayerId(Net.HttpMethods.POST, "/games/" + path(gameId) + "/start", playerId, callback);
    }

    public void submitMove(String gameId, String playerId, String action, String dataJson, final NetworkCallback callback) {
        String data = dataJson == null || dataJson.trim().isEmpty() ? "{}" : dataJson;
        sendJson(Net.HttpMethods.POST, "/games/" + path(gameId) + "/move",
                "{\"playerId\":\"" + escape(playerId) + "\",\"action\":\"" + escape(action) + "\",\"data\":" + data + "}",
                callback);
    }

    public void submitMovePosition(String gameId, String playerId, int x, int y, final NetworkCallback callback) {
        submitMove(gameId, playerId, "move", "{\"position\":{\"x\":" + x + ",\"y\":" + y + "}}", callback);
    }

    public void endGame(String gameId, String winnerId, final NetworkCallback callback) {
        String content = winnerId == null || winnerId.trim().isEmpty()
                ? "{}"
                : "{\"winnerId\":\"" + escape(winnerId) + "\"}";
        sendJson(Net.HttpMethods.POST, "/games/" + path(gameId) + "/end", content, callback);
    }

    public void getGameMoves(String gameId, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.GET, "/games/" + path(gameId) + "/moves", null, callback);
    }

    public void updateConnectionState(String gameId, String playerId, boolean connected, final NetworkCallback callback) {
        sendJson(Net.HttpMethods.POST, "/games/" + path(gameId) + "/connection",
                "{\"playerId\":\"" + escape(playerId) + "\",\"connected\":" + connected + "}",
                callback);
    }

    public JsonValue parse(String response) {
        return jsonReader.parse(response);
    }

    private void sendPlayerId(String method, String endpoint, String playerId, final NetworkCallback callback) {
        sendJson(method, endpoint, "{\"playerId\":\"" + escape(playerId) + "\"}", callback);
    }

    private void sendJson(String method, String endpoint, String content, final NetworkCallback callback) {
        Net.HttpRequest httpRequest = new Net.HttpRequest(method);
        httpRequest.setUrl(baseUrl + endpoint);
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setHeader("Accept", "application/json");
        httpRequest.setTimeOut(10000);
        if (content != null) {
            httpRequest.setContent(content);
        }

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String body = httpResponse.getResultAsString();
                if (statusCode >= 200 && statusCode < 300) {
                    callback.onSuccess(body);
                } else {
                    callback.onError(new NetworkException(statusCode, extractError(body)));
                }
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

    private String extractError(String body) {
        if (body == null || body.trim().isEmpty()) return "Empty error response";
        try {
            JsonValue parsed = jsonReader.parse(body);
            String message = parsed.getString("message", null);
            if (message != null) return message;
            String error = parsed.getString("error", null);
            if (error != null) return error;
        } catch (Exception ignored) {
        }
        return body;
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.trim().isEmpty()) return DEFAULT_BASE_URL;
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String path(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public interface NetworkCallback {
        void onSuccess(String response);
        void onError(Throwable t);
    }

    public static class NetworkException extends Exception {
        private final int statusCode;

        public NetworkException(int statusCode, String message) {
            super("HTTP " + statusCode + ": " + message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
