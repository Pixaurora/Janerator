package net.pixaurora.janerator.graphing.grapher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.config.SerialType;
import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;

public class FunctionGrapher extends CachingGrapher {
    public static final Codec<FunctionGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(FunctionGrapher::getGraphDefinition)
        ).apply(instance, FunctionGrapher::new)
    );
    public static final SerialType<ChunkGrapher> TYPE = new SerialType<>("function", CODEC);

    private GraphFunctionDefinition graphDefinition;
    protected ThreadLocal<GraphFunction> graphFunction;

    public FunctionGrapher(GraphFunctionDefinition graphDefinition) {
        this.graphDefinition = graphDefinition;
        this.graphFunction = ThreadLocal.withInitial(() -> GraphFunction.fromDefinition(this.graphDefinition));
    }

    public GraphFunctionDefinition getGraphDefinition() {
        return this.graphDefinition;
    }

    @Override
    public boolean isPointShaded(Coordinate pos)  {
        return this.graphFunction.get().evaluate(pos.x(), pos.z()) == 1.0;
    }

    @Override
    public SerialType<ChunkGrapher> type() {
        return TYPE;
    }
}
