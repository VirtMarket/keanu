package io.improbable.keanu.vertices.intgr.nonprobabilistic.operators.unary;

import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.NonProbabilistic;
import io.improbable.keanu.vertices.SaveVertexParam;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.intgr.IntegerVertex;

public abstract class IntegerUnaryOpVertex extends IntegerVertex implements NonProbabilistic<IntegerTensor> {

    protected final IntegerVertex inputVertex;
    protected static final String INPUT_NAME = "inputVertex";

    /**
     * A vertex that performs a user defined operation on a singe input vertex
     *
     * @param inputVertex the input vertex
     */
    public IntegerUnaryOpVertex(IntegerVertex inputVertex) {
        this(inputVertex.getShape(), inputVertex);
    }

    /**
     * A vertex that performs a user defined operation on a singe input vertex
     *
     * @param shape       the shape of the tensor
     * @param inputVertex the input vertex
     */
    public IntegerUnaryOpVertex(long[] shape, IntegerVertex inputVertex) {
        super(shape);
        this.inputVertex = inputVertex;
        setParents(inputVertex);
    }

    @Override
    public IntegerTensor sample(KeanuRandom random) {
        return op(inputVertex.sample(random));
    }

    @Override
    public IntegerTensor calculate() {
        return op(inputVertex.getValue());
    }

    protected abstract IntegerTensor op(IntegerTensor value);

    @SaveVertexParam(INPUT_NAME)
    public IntegerVertex getInputVertex() {
        return inputVertex;
    }
}
