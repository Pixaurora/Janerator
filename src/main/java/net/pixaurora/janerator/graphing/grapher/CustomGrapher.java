package net.pixaurora.janerator.graphing.grapher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.pixaurora.janerator.graphing.Coordinate;
import net.pixaurora.janerator.graphing.GraphFunction;
import net.pixaurora.janerator.graphing.GraphFunctionDefinition;

public class CustomGrapher extends CachingGrapher {
    public static final Codec<CustomGrapher> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GraphFunctionDefinition.BIVARIATE_CODEC.fieldOf("graph(x, z)").forGetter(CustomGrapher::getGraphDefinition)
        ).apply(instance, CustomGrapher::new)
    );

    private GraphFunctionDefinition graphDefinition;
    protected ThreadLocal<GraphFunction> graphFunction;

    public CustomGrapher(GraphFunctionDefinition graphDefinition) {
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
    public GrapherType type() {
        return GrapherType.CUSTOM;
    }
}
