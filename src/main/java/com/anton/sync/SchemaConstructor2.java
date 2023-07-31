package com.anton.sync;

import com.google.gson.Gson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SchemaConstructor2 {





//    public static List<Field> convertToDotKeys(JSONObject jsonObject) {
//        return convertToDotKeys(jsonObject, "");
//    }
//
//    private static List<Field> convertToDotKeys(JSONObject jsonObject, String parentKey) {
//        List<Field> dotKeysList = new ArrayList<>();
//
//        Iterator<String> keys = jsonObject.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String newKey = parentKey.isEmpty() ? key : parentKey + "." + key;
//            Object value = jsonObject.get(key);
//
//            if (value instanceof JSONObject) {
//                dotKeysList.addAll(convertToDotKeys((JSONObject) value, newKey));
//            } if (value instanceof JSONArray) {
//
//            } else {
//
//                Field f = new Field();
//                f.fieldName = newKey;
//
//                dotKeysList.add(f);
//            }
//        }
//
//        return dotKeysList;
//    }

    @ToString
    @Data
    @EqualsAndHashCode(exclude = {"sampleData"})
    public static class Field implements Comparable<Field> {
        private String fieldName;
        private String fieldType;
        private Object sampleData;

        @Override
        public int compareTo(Field o) {
            return this.fieldName.compareTo(o.fieldName);
        }
    }



    public static Set<Field> convertToDotKeys(JSONObject jsonObject) {
        return convertToDotKeys(jsonObject, "");
    }

    private static Set<Field> convertToDotKeys(JSONObject jsonObject, String parentKey) {
        Set<Field> dotKeysList = new HashSet<>();

        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String newKey = parentKey.isEmpty() ? key : parentKey + "." + key;
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                Field field = new Field();
                field.setFieldName(newKey);
                field.setFieldType("Object");
                dotKeysList.add(field);
                dotKeysList.addAll(convertToDotKeys((JSONObject) value, newKey));
            } else if (value instanceof JSONArray) {
//                Field field = new Field();
//                field.setFieldName(newKey);
//                field.setFieldType("List");
//                dotKeysList.add(field);
                dotKeysList.addAll(convertArrayToDotKeys((JSONArray) value, newKey));
            } else {

                Field field = new Field();
                field.setFieldName(newKey);
                field.setFieldType(value.getClass().getSimpleName());
                field.setSampleData(value);
                dotKeysList.add(field);
            }
        }

        return dotKeysList;
    }

    private static Set<Field> convertArrayToDotKeys(JSONArray jsonArray, String parentKey) {
        Set<Field> dotKeysList = new HashSet<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);
            if (item instanceof JSONObject) {
                dotKeysList.addAll(convertToDotKeys((JSONObject) item, parentKey));
            } else {
                Field field = new Field();
                field.setFieldName(parentKey);
                field.setFieldType("List[" + item.getClass().getSimpleName() + "]");
                field.setSampleData(jsonArray.toList());
                dotKeysList.add(field);
            }
        }

        return dotKeysList;
    }

    public static String getDataType(Object object) {
        if (object == null) {
            return "null";
        }

        Class<?> dataType = object.getClass();

        if (dataType.isArray()) {
            Class<?> componentType = dataType.getComponentType();
            return componentType.getName() + "[]";
        } else if (object instanceof List<?>) {
            return "List";
        } else if (object instanceof Map<?, ?>) {
            return "Map";
        } else {
            return dataType.getName();
        }
    }
}
