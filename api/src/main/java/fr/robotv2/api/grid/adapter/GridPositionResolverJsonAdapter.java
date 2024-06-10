package fr.robotv2.api.grid.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import fr.robotv2.api.grid.GridPositionResolver;

import java.lang.reflect.Type;

public class GridPositionResolverJsonAdapter implements JsonSerializer<GridPositionResolver>, JsonDeserializer<GridPositionResolver> {

    private static final String CLASS_NAME = "CLASSNAME";
    private static final String DATA = "DATA";

    @Override
    public GridPositionResolver deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject object = json.getAsJsonObject();
        final JsonPrimitive primitive = (JsonPrimitive) object.get(CLASS_NAME);
        final Class<?> clazz = getClassFromName(primitive.getAsString());

        return context.deserialize(object.get(DATA), clazz);
    }

    @Override
    public JsonElement serialize(GridPositionResolver src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        object.addProperty(CLASS_NAME, src.getClass().getName());
        object.add(DATA, context.serialize(src));
        return object;
    }

    private Class<?> getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            throw new JsonParseException(exception.getMessage());
        }
    }
}
