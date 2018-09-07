package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.unary;

import static io.improbable.keanu.tensor.TensorShape.shapeSlice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.VertexId;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.DualNumber;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.PartialDerivatives;

public class SliceVertex extends DoubleUnaryOpVertex {

    private final int dimension;
    private final int index;

    /**
     * Takes the slice along a given dimension and index of a vertex
     *
     * @param inputVertex the input vertex
     * @param dimension   the dimension to extract along
     * @param index       the index of extraction
     */
    public SliceVertex(DoubleVertex inputVertex, int dimension, int index) {
        super(shapeSlice(dimension, inputVertex.getShape()), inputVertex);
        this.dimension = dimension;
        this.index = index;
    }

    @Override
    protected DoubleTensor op(DoubleTensor value) {
        return value.slice(dimension, index);
    }

    @Override
    public Map<Vertex, PartialDerivatives> reverseModeAutoDifferentiation(PartialDerivatives derivativeOfOutputsWithRespectToSelf) {
        Map<Vertex, PartialDerivatives> partials = new HashMap<>();

        for (Map.Entry<VertexId, DoubleTensor> entry : derivativeOfOutputsWithRespectToSelf.asMap().entrySet()) {
            VertexId k = entry.getKey();
            DoubleTensor v = entry.getValue();
            DoubleTensor padded = padSliceWithZerosToMatchOriginalShape(v);
            partials.put(inputVertex, new PartialDerivatives(k, padded));
        }

        return partials;
     }

    @Override
    protected DualNumber dualOp(DualNumber dualNumber) {
        return dualNumber.slice(dimension, index);
    }

    private DoubleTensor padSliceWithZerosToMatchOriginalShape(DoubleTensor tensor) {
        int[] partialShape = tensor.getShape();
        int[] inputShape = inputVertex.getShape();
        int length = getShape().length;
        int dimensionOfWrtToExtend = dimension + length;
        int lengthInSlicedDimension = inputShape[dimension] - 1;

        if (index == 0) {
            partialShape[dimensionOfWrtToExtend] = lengthInSlicedDimension;
            return tensor.concat(dimensionOfWrtToExtend, DoubleTensor.zeros(partialShape));
        } else if (index == lengthInSlicedDimension) {
            partialShape[dimensionOfWrtToExtend] = lengthInSlicedDimension;
            return DoubleTensor.zeros(partialShape).concat(dimensionOfWrtToExtend, tensor);
        } else {
            int[] zerosBeforeSlice = Arrays.copyOf(partialShape, partialShape.length);
            int[] zerosAfterSlice = Arrays.copyOf(partialShape, partialShape.length);
            zerosBeforeSlice[dimensionOfWrtToExtend] = index;
            zerosAfterSlice[dimensionOfWrtToExtend] = lengthInSlicedDimension - index;
            return DoubleTensor.zeros(zerosBeforeSlice).concat(dimensionOfWrtToExtend, tensor, DoubleTensor.zeros(zerosAfterSlice));
        }
    }

}