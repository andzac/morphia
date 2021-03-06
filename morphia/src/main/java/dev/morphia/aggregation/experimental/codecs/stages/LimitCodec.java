package dev.morphia.aggregation.experimental.codecs.stages;

import dev.morphia.aggregation.experimental.stages.Limit;
import dev.morphia.mapping.Mapper;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

public class LimitCodec extends StageCodec<Limit> {
    public LimitCodec(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public void encodeStage(final BsonWriter writer, final Limit value, final EncoderContext encoderContext) {
        writer.writeInt64(value.getLimit());
    }

    @Override
    public Class<Limit> getEncoderClass() {
        return Limit.class;
    }
}
