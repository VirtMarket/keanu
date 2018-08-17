package io.improbable.keanu.vertices.intgr.nonprobabilistic.operators.binary;


import static io.improbable.keanu.tensor.TensorShapeValidation.checkHasSingleNonScalarShapeOrAllScalar;

import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.update.NonProbabilisticValueUpdater;

public abstract class IntegerBinaryOpVertex extends IntegerVertex {

    protected final IntegerVertex a;
    protected final IntegerVertex b;

    /**
     * A vertex that performs a user defined operation on two input vertices
     *
     * @param a  first input vertex
     * @param b  second input vertex
     */
    public IntegerBinaryOpVertex(IntegerVertex a, IntegerVertex b) {
        this(checkHasSingleNonScalarShapeOrAllScalar(a.getShape(), b.getShape()), a, b);
    }

    /**
     * A vertex that performs a user defined operation on two input vertices
     *
     * @param shape the shape of the tensor
     * @param a     first input vertex
     * @param b     second input vertex
     */
    public IntegerBinaryOpVertex(int[] shape, IntegerVertex a, IntegerVertex b) {
        super(
            new NonProbabilisticValueUpdater<>(v -> ((IntegerBinaryOpVertex) v).op(a.getValue(), b.getValue()))
        );
        this.a = a;
        this.b = b;
        setParents(a, b);
        setValue(IntegerTensor.placeHolder(shape));
    }

    @Override
    public IntegerTensor sample(KeanuRandom random) {
        return op(a.sample(random), b.sample(random));
    }

    protected abstract IntegerTensor op(IntegerTensor l, IntegerTensor r);
}
