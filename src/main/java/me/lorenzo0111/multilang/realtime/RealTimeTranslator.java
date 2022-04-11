package me.lorenzo0111.multilang.realtime;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.utils.RegexChecker;
import okhttp3.*;

public class RealTimeTranslator implements ITranslator {
    private final String api;
    private final OkHttpClient client;
    private final MediaType mediaType = MediaType.parse("application/json");

    public RealTimeTranslator(String api) {
        this.api = api;
        this.client = new OkHttpClient().newBuilder()
                .build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String translate(String text, String language) {
        if (RegexChecker.isUrl(text)) {
            return text;
        }

        if (text == null || text.isEmpty() || language == null || language.isEmpty()) {
            return null;
        }

        MultiLangPlugin.getInstance().debug("Translating: " + text + " to " + language);

        try {
            JsonObject object = new JsonObject();
            object.addProperty("text", text);
            object.addProperty("to", language);

            RequestBody body = RequestBody.create(object.toString(), mediaType);
            Request request = new Request.Builder()
                    .url("https://multilang.lorenzo0111.me/api/translate")
                    .method("POST", body)
                    .addHeader("authorization", api)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
            if (json.has("error")) {
                MultiLangPlugin.getInstance().getLogger().severe("RealTime server returned an error: " + json.get("error").getAsString());
                return null;
            } else if (json.has("text")) {
                MultiLangPlugin.getInstance().debug("Request returned: " + json.get("text").getAsString());
                return json.get("text").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MultiLangPlugin.getInstance().debug("Request failed. Returning null");

        return null;
    }

}
