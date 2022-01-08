package me.lorenzo0111.multilang.realtime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.api.objects.ITranslator;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MicrosoftTranslator implements ITranslator {
    private final String endpoint;
    private final String api;

    public MicrosoftTranslator(String api) {
        this.api = api;
        this.endpoint = "https://microsoft-translator-text.p.rapidapi.com/translate?api-version=3.0&to=%s&suggestedFrom=en&textType=plain&profanityAction=NoAction";
    }

    @Override
    public String translate(String text, String language) {
        if (RegexChecker.isUrl(text)) {
            return text;
        }

        try {
            URL url = new URL(String.format(this.endpoint, language));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("x-rapidapi-host", "microsoft-translator-text.p.rapidapi.com");
            connection.setRequestProperty("x-rapidapi-key", api);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            JsonArray array = new JsonArray();
            JsonObject object = new JsonObject();
            object.addProperty("Text", ChatColor.stripColor(text));
            array.add(object);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = array.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonArray jArray = new JsonParser().parse(response.toString()).getAsJsonArray();
                JsonObject result = jArray.get(0).getAsJsonObject();
                return result.get("translations").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
