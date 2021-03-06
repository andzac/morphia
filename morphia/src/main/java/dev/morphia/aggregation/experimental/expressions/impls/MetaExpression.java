package dev.morphia.aggregation.experimental.expressions.impls;

import dev.morphia.mapping.Mapper;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

public class MetaExpression extends Expression {

    public MetaExpression() {
        super("$meta");
    }

    @Override
    public void encode(final Mapper mapper, final BsonWriter writer, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString(getOperation(), "textScore");
        writer.writeEndDocument();
    }
}
