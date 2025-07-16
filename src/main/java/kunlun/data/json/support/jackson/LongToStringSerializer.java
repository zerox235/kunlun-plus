/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import static kunlun.common.constant.Numbers.SIXTEEN;
import static kunlun.common.constant.Numbers.ZERO;

/**
 * LongToStringSerializer.
 * @author Zerox
 */
public class LongToStringSerializer extends StdSerializer<Long> {
    private final int maxLength;

    public LongToStringSerializer(int maxLength) {
        super(Long.class);
        this.maxLength = maxLength;
    }

    public LongToStringSerializer() {

        this(SIXTEEN);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider p) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (maxLength >= ZERO && value.toString().length() >= maxLength) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value);
        }
    }
}
