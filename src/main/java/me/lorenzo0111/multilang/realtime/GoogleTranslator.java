package me.lorenzo0111.multilang.realtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.utils.RegexChecker;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class GoogleTranslator implements ITranslator {
    private final OkHttpClient client;

    public GoogleTranslator() {
        this.client = new OkHttpClient()
                .newBuilder()
                // Chat messages wait for the translation, so a slow response must not hold them for long
                .connectTimeout(3, TimeUnit.SECONDS)
                .callTimeout(5, TimeUnit.SECONDS)
                .build();
    }

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
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("translate.googleapis.com")
                    .addPathSegments("translate_a/single")
                    .addQueryParameter("client", "gtx")
                    .addQueryParameter("sl", "auto")
                    .addQueryParameter("tl", language)
                    .addQueryParameter("dt", "t")
                    .addQueryParameter("dj", "1")
                    .addQueryParameter("source", "input")
                    .addQueryParameter("q", text)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            JsonObject json;
            try (Response response = client.newCall(request).execute()) {
                json = new JsonParser().parse(response.body().string()).getAsJsonObject();
            }

            if (!json.has("sentences")) {
                MultiLangPlugin.getInstance().getLogger().severe("Google Translate did not return a correct response.");
                return null;
            }

            // Google splits the text in sentences, each one with its own translation
            StringBuilder builder = new StringBuilder();
            for (JsonElement sentence : json.getAsJsonArray("sentences")) {
                JsonObject result = sentence.getAsJsonObject();
                if (result.has("trans")) builder.append(result.get("trans").getAsString());
            }

            if (builder.length() == 0) {
                MultiLangPlugin.getInstance().debug("Request returned no translation. Returning null");
                return null;
            }

            String trans = builder.toString();

            MultiLangPlugin.getInstance().debug("Request returned: " + trans);
            return trans;
        } catch (Exception e) {
            e.printStackTrace();
        }

        MultiLangPlugin.getInstance().debug("Request failed. Returning null");

        return null;
    }

}
