/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ListIntegerJsonDeserializer extends JsonDeserializer<List<Integer>> {

    @Override
    public List<Integer> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) { return null; }
        if (node.isArray()) {
            if (node.isEmpty()) { return emptyList(); }
            Iterator<JsonNode> elements = node.elements();
            List<Integer> result = new ArrayList<Integer>();
            while (elements.hasNext()) {
                JsonNode next = elements.next();
                if (next == null) { continue; }
                result.add(next.asInt());
            }
            return result;
        } else {
            if (StrUtil.isBlank(node.asText())) { return null; }
            return new ArrayList<Integer>(singletonList(node.asInt()));
        }
    }

}
