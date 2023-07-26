package com.anton.sync;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

public class SchemaConstructor {

    public static JSONObject constructDynamicSchema(JSONObject jsonObject) {
        JSONObject dynamicSchema = new JSONObject();
        dynamicSchema.put("$schema", "http://json-schema.org/draft-07/schema#");
        dynamicSchema.put("type", "object");

        JSONObject properties = new JSONObject();

        // Traverse the keys of the JSONObject to infer the schema
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String type = value.getClass().getSimpleName().toLowerCase();

            // If the value is another JSONObject, construct a recursive schema for it
            if (type.equals("jsonobject")) {
                properties.put(key, constructDynamicSchema((JSONObject) value));
            } else {
                // For other types (String, Integer, Double, Boolean), directly infer the type
                JSONObject propertySchema = new JSONObject();
                propertySchema.put("type", type);
                properties.put(key, propertySchema);
            }
        }

        dynamicSchema.put("properties", properties);

        // Optional: Define required properties if necessary
        // JSONArray requiredProperties = new JSONArray();
        // requiredProperties.putAll(jsonObject.keySet());
        // dynamicSchema.put("required", requiredProperties);

        return dynamicSchema;
    }
}
