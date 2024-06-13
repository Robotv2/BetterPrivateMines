package fr.robotv2.api.json;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class MultiMapJsonAdapter<K, V> implements JsonSerializer<ArrayListMultimap<K, V>>, JsonDeserializer<ArrayListMultimap<K, V>> {

    @Override
    public ArrayListMultimap<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            ArrayListMultimap<K, V> multimap = ArrayListMultimap.create();

            Type[] typeArgs = ((ParameterizedType) typeOfT).getActualTypeArguments();
            Type keyType = typeArgs[0];
            Type valueType = typeArgs[1];

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                K key = context.deserialize(new JsonPrimitive(entry.getKey()), keyType);
                JsonArray values = entry.getValue().getAsJsonArray();
                for (JsonElement value : values) {
                    V deserializedValue = context.deserialize(value, valueType);
                    multimap.put(key, deserializedValue);
                }
            }

            return multimap;
        } catch (JsonParseException | ClassCastException e) {
            throw new JsonParseException("Failed to deserialize Multimap", e);
        }
    }

    @Override
    public JsonElement serialize(ArrayListMultimap<K, V> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (K key : src.keySet()) {
            JsonArray jsonArray = new JsonArray();
            for (V value : src.get(key)) {
                jsonArray.add(context.serialize(value));
            }
            jsonObject.add(key.toString(), jsonArray);
        }

        return jsonObject;
    }
}

