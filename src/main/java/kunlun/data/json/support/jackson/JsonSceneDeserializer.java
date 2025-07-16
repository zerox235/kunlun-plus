/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import kunlun.common.constant.Nil;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import kunlun.data.json.support.jackson.util.JsonSceneManager;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneDeserializer;
import kunlun.data.json.support.jackson.util.JsonSceneUtil;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.fasterxml.jackson.databind.type.TypeFactory.unknownType;

/**
 * JsonSceneDeserializer.
 * @author Zerox
 */
public class JsonSceneDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    private static final Logger log = LoggerFactory.getLogger(JsonSceneDeserializer.class);
    private JavaType fieldType;
    private String scene;

    public JsonSceneDeserializer(String scene, JavaType fieldType) {
        this.fieldType = fieldType;
        this.scene = scene;
    }

    public JsonSceneDeserializer() {

    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        JsonSceneDeserialize jsonScene; Scene sceneEnum; String custom = null, scene;
        JavaType fieldType = property.getType();
        if ((jsonScene = property.getAnnotation(JsonSceneDeserialize.class)) == null) {
            return new JsonSceneDeserializer(Nil.STR, fieldType);
        }
        if (Scene.DEFAULT.equals(sceneEnum = jsonScene.value()) &&
                StrUtil.isBlank(custom = jsonScene.custom())) {
            return new JsonSceneDeserializer(Nil.STR, fieldType);
        }
        if (!Scene.DEFAULT.equals(sceneEnum)) {
            scene = sceneEnum.name();
        } else { scene = custom; }
        return new JsonSceneDeserializer(scene, fieldType);
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        //
        JsonSceneManager manager; SceneDeserializer deserializer;
        if (StrUtil.isBlank(scene) || (manager = JsonSceneUtil.getManager()) == null ||
                (deserializer = manager.getDeserializer(scene)) == null) {
            return context.readValue(parser, fieldType);
        }
        // process base on node
        TreeNode node = parser.readValueAsTree();
        if (node.isValueNode()) {
            return processValue((ValueNode) node, parser, context, deserializer);
        } else if (node.isArray()) {
            return processArray((ArrayNode) node, parser, context, deserializer);
        } else if (node.isObject()) {
            return processObject((ObjectNode) node, parser, context, deserializer);
        } else {
            return processDefault(node, parser, context);
        }
    }

    protected Object processDefault(TreeNode node, JsonParser parser,
                                    DeserializationContext context) throws IOException {
        JsonParser nodeParser = node.traverse(parser.getCodec());
        nodeParser.nextToken();
        return context.readValue(nodeParser, fieldType);
    }

    protected Object processValue(ValueNode valueNode, JsonParser parser,
                                  DeserializationContext context, SceneDeserializer deserializer) throws IOException {
        Object value;
        if (valueNode.isTextual()) { value = valueNode.asText(); }
        else if (valueNode.isNumber()) {
            if (valueNode.isInt()) { value = valueNode.asInt(); }
            else if (valueNode.isLong()) { value = valueNode.asLong(); }
            else if (valueNode.isDouble()) { value = valueNode.asDouble(); }
            else if (valueNode.isFloat()) { value = valueNode.asDouble(); }
            else { value = valueNode.asText(); } // like BigDecimal
        }
        else if (valueNode.isBoolean()) { value = valueNode.asBoolean(); }
        else if (valueNode.isBinary()) { value = valueNode.binaryValue(); }
        else if (valueNode.isNull()) { value = null; }
        else { value = null; }
        //
        Object processed = deserializer.deserialize(value, fieldType, parser.getCodec());
        if (processed == null) { return processDefault(valueNode, parser, context); }
        return processed;
    }

    protected Object processArray(ArrayNode arrayNode, JsonParser parser,
                                  DeserializationContext context, SceneDeserializer deserializer) throws IOException {
        List<Object> list = new ArrayList<Object>();
        for (JsonNode element : arrayNode) {
            JsonParser elementParser = element.traverse(parser.getCodec());
            elementParser.nextToken();
            JavaType elementType = fieldType.getContentType();
            if (elementType == null) { elementType = unknownType(); }
            list.add(context.readValue(elementParser, elementType));
        }
        //
        Object processed = deserializer.deserialize(list, fieldType, parser.getCodec());
        if (processed == null) { return list; }
        return processed;
    }

    protected Object processObject(ObjectNode objectNode, JsonParser parser,
                                   DeserializationContext context, SceneDeserializer deserializer) throws IOException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonParser elementParser = entry.getValue().traverse(parser.getCodec());
            elementParser.nextToken();
            JavaType elementType = fieldType.getContentType();
            if (elementType == null) { elementType = unknownType(); }
            map.put(entry.getKey(), context.readValue(elementParser, elementType));
        }
        //
        Object processed = deserializer.deserialize(map, fieldType, parser.getCodec());
        if (processed == null) {
            if (fieldType.isMapLikeType()) { return map; }
            else { return processDefault(objectNode, parser, context); }
        }
        return processed;
    }

}
