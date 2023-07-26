package com.anton.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.schema.GenericObject;
import org.apache.pulsar.client.impl.schema.BytesSchema;
import org.apache.pulsar.functions.api.Record;
import org.apache.pulsar.io.core.Sink;
import org.apache.pulsar.io.core.SinkContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MySink implements Sink<GenericObject> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_06)
            .build();

    @Override
    public void open(Map<String, Object> config, SinkContext sinkContext) throws Exception {

    }

    @Override
    public void write(Record<GenericObject> record) {
        Schema schema = record.getSchema();
        if (schema instanceof BytesSchema) {

            byte[] dataByteArr = record.getMessage().get().getData();
            if (dataByteArr != null) {
                String data = new String(dataByteArr);
                if (isValidJson(data)) {
                    try {
                        JsonNode sample = mapper.readTree(data);
                        ObjectNode inferredSchema = inferrer.inferForSample(sample);
                        System.out.println("Schema derived from data...");
                        System.out.printf("%s : %s%n", "TOPIC NAME ", record.getTopicName());
                        System.out.printf("%s : %s%n", "SCHEMA INFO ", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inferredSchema));
                        // TODO : Expose a REST service from the application responsible for handling the schema upload by user and call the API


                        // if not preferred to use the schema inferring lib, we can still use our own implementation
//                        JSONObject jsonObject = new JSONObject(data);
//                        JSONObject dynamicSchema = SchemaConstructor.constructDynamicSchema(jsonObject);
//                        System.out.println("----------------------------");
//                        System.out.println("Schema derived from data...");
//                        System.out.printf("%s : %s%n", "SCHEMA INFO ", dynamicSchema);
//                        System.out.println("----------------------------");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        // handle exception if needed
                    }
                } else {
                    System.out.println("Message is simple text, no schema can be derived. : " + data);
                }
            }


        } else {
//            System.out.println("schema: " + schema );
//            System.out.println("getNativeSchema: " + schema.getNativeSchema().get() );
            System.out.println("Schema derived from topic schema info...");
            System.out.println(String.format("%s : %s", "TOPIC NAME ", record.getTopicName()));
            System.out.println(String.format("%s : %s", "SCHEMA INFO ", schema.getSchemaInfo()));
            // TODO : Expose a REST service from the application responsible for handling the schema upload by user and call the API
        }

        // more methods that can be useful
//        GenericObject genericObject = record.getValue();
//        if (genericObject != null) {
//            SchemaType type = genericObject.getSchemaType();
//            Object nativeObject = genericObject.getNativeObject();
//            System.out.println("type: " + type );
//            System.out.println("nativeObject: " + nativeObject );
//        }
//        System.out.println("record: " + new String(record.getMessage().get().getData()) );

    }

    @Override
    public void close() throws Exception {

    }

    public boolean isValidJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }


//    private static String absoluteUriFormatInferrer(FormatInferrerInput input) {
//        final String textValue = input.getSample().textValue();
//        if (textValue == null) {
//            return null;
//        }
//        try {
//            final URI uri = new URI(input.getSample().textValue());
//            if (uri.isAbsolute()) {
//                return "uri";
//            }
//        } catch (Exception e) {
//            // ignore
//        }
//        return null;
//    }


}
