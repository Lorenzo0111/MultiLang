package me.lorenzo0111.multilang.realtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.utils.RegexChecker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleTranslator implements ITranslator {
    private final OkHttpClient client;

    public GoogleTranslator() {
        this.client = new OkHttpClient()
                .newBuilder()
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
            Request request = new Request.Builder()
                    .url("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" +
                            language +
                            "&dt=t&dj=1&source=input&q=" + text)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
            if (!json.has("sentences")) {
                MultiLangPlugin.getInstance().getLogger().severe("Google Translate did not return a correct response.");
                return null;
            }

            JsonArray sentences = json.getAsJsonArray("sentences");
            JsonObject result = sentences.get(0).getAsJsonObject();
            String trans = result.get("trans").getAsString();

            MultiLangPlugin.getInstance().debug("Request returned: " + trans);
            return trans;
        } catch (Exception e) {
            e.printStackTrace();
        }

        MultiLangPlugin.getInstance().debug("Request failed. Returning null");

        return null;
    }

}
