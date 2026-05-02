/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package otros;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author jeffm
 */
public class GsonConfig {
    private static final DateTimeFormatter DATE_FMT     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final Gson INSTANCE = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class,     new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .serializeNulls()
        .create();

    private GsonConfig() {}

    public static Gson get() {
        return INSTANCE;
    }
   
    private static class LocalDateAdapter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type t, JsonSerializationContext ctx) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(DATE_FMT));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type t, JsonDeserializationContext ctx)
                throws JsonParseException {
            String s = json.getAsString();
            if (s == null || s.isBlank()) return null;
            return LocalDate.parse(s, DATE_FMT);
        }
    }

    private static class LocalDateTimeAdapter
            implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type t, JsonSerializationContext ctx) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(DATETIME_FMT));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type t, JsonDeserializationContext ctx)
                throws JsonParseException {
            String s = json.getAsString();
            if (s == null || s.isBlank()) return null;
            return LocalDateTime.parse(s, DATETIME_FMT);
        }
    }
}
