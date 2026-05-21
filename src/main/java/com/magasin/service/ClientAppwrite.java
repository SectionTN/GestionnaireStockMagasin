package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.magasin.util.Configuration;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Client REST pour l'API Appwrite TablesDB.
 * Endpoints :
 *   POST   /v1/tablesdb/{dbId}/tables/{tableId}/rows
 *   GET    /v1/tablesdb/{dbId}/tables/{tableId}/rows
 *   GET    /v1/tablesdb/{dbId}/tables/{tableId}/rows/{rowId}
 *   PATCH  /v1/tablesdb/{dbId}/tables/{tableId}/rows/{rowId}
 *   DELETE /v1/tablesdb/{dbId}/tables/{tableId}/rows/{rowId}
 */
public final class ClientAppwrite {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private ClientAppwrite() {}

    private static HttpRequest.Builder requeteBase(String chemin) {
        URI uri = URI.create(Configuration.pointTerminaison() + chemin);
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("X-Appwrite-Project", Configuration.idProjet())
                .header("X-Appwrite-Key", Configuration.cleApi())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private static String basePourTable(String tableId) {
        return "/tablesdb/" + Configuration.idBase() + "/tables/" + tableId + "/rows";
    }

    private static JsonObject envoyer(HttpRequest req) throws AppwriteException {
        try {
            HttpResponse<String> rep = HTTP.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            int code = rep.statusCode();
            String corps = rep.body();
            if (code >= 200 && code < 300) {
                if (corps == null || corps.isEmpty()) return new JsonObject();
                JsonElement el = JsonParser.parseString(corps);
                return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
            }
            String msg = "Erreur HTTP " + code;
            try {
                JsonElement el = JsonParser.parseString(corps);
                if (el.isJsonObject() && el.getAsJsonObject().has("message")) {
                    msg += " — " + el.getAsJsonObject().get("message").getAsString();
                }
            } catch (Exception ignore) {
                msg += " — " + corps;
            }
            throw new AppwriteException(msg, code);
        } catch (AppwriteException e) {
            throw e;
        } catch (Exception e) {
            throw new AppwriteException("Echec de la requete reseau : " + e.getMessage(), -1);
        }
    }

    /** Liste toutes les lignes d'une table. Renvoie le tableau JSON "rows". */
    public static JsonArray listerLignes(String tableId, List<String> requetes) throws AppwriteException {
        StringBuilder url = new StringBuilder(basePourTable(tableId));
        if (requetes != null && !requetes.isEmpty()) {
            url.append("?");
            for (int i = 0; i < requetes.size(); i++) {
                if (i > 0) url.append("&");
                url.append("queries[]=").append(URLEncoder.encode(requetes.get(i), StandardCharsets.UTF_8));
            }
        }
        HttpRequest req = requeteBase(url.toString()).GET().build();
        JsonObject rep = envoyer(req);
        if (rep.has("rows") && rep.get("rows").isJsonArray()) return rep.getAsJsonArray("rows");
        // retrocompat : ancienne API renvoie "documents"
        if (rep.has("documents") && rep.get("documents").isJsonArray()) return rep.getAsJsonArray("documents");
        return new JsonArray();
    }

    /** Cree une nouvelle ligne. donnees = JSON des colonnes. */
    public static JsonObject creerLigne(String tableId, JsonObject donnees) throws AppwriteException {
        JsonObject corps = new JsonObject();
        corps.addProperty("rowId", "unique()");
        corps.add("data", donnees);
        HttpRequest req = requeteBase(basePourTable(tableId))
                .POST(BodyPublishers.ofString(corps.toString(), StandardCharsets.UTF_8))
                .build();
        return envoyer(req);
    }

    /** Met a jour une ligne existante. */
    public static JsonObject mettreAJourLigne(String tableId, String rowId, JsonObject donnees) throws AppwriteException {
        JsonObject corps = new JsonObject();
        corps.add("data", donnees);
        HttpRequest req = requeteBase(basePourTable(tableId) + "/" + rowId)
                .method("PATCH", BodyPublishers.ofString(corps.toString(), StandardCharsets.UTF_8))
                .build();
        return envoyer(req);
    }

    /** Supprime une ligne. */
    public static void supprimerLigne(String tableId, String rowId) throws AppwriteException {
        HttpRequest req = requeteBase(basePourTable(tableId) + "/" + rowId)
                .DELETE()
                .build();
        envoyer(req);
    }

    /** Recupere une ligne par son id. */
    public static JsonObject obtenirLigne(String tableId, String rowId) throws AppwriteException {
        HttpRequest req = requeteBase(basePourTable(tableId) + "/" + rowId).GET().build();
        return envoyer(req);
    }
}
