package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary;

import io.improbable.keanu.vertices.dbl.DoubleVertex;
import org.junit.Test;

import static io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary.BinaryOperationTestHelpers.*;

public class AdditionVertexTest {

    @Test
    public void addsTwoScalarVertexValues() {
        operatesOnTwoScalarVertexValues(
            2.0,
            3.0,
            5.0,
            DoubleVertex::plus
        );
    }

    @Test
    public void calculatesDualNumberOfTwoScalarsAdded() {
        calculatesDualNumberOfTwoScalars(
            2.0,
            3.0,
            1.0,
            1.0,
            DoubleVertex::plus
        );
    }

    @Test
    public void addsTwoMatrixVertexValues() {
        operatesOnTwo2x2MatrixVertexValues(
            new double[]{1.0, 2.0, 6.0, 4.0},
            new double[]{2.0, 4.0, 3.0, 8.0},
            new double[]{3.0, 6.0, 9.0, 12.0},
            DoubleVertex::plus
        );
    }

    @Test
    public void calculatesDualNumberOfTwoMatricesElementWiseAdded() {
        calculatesDualNumberOfTwoMatricesElementWiseOperator(
            new double[]{1.0, 2.0, 3.0, 4.0},
            new double[]{2.0, 3.0, 4.0, 5.0},
            toDiagonalArray(new double[]{1.0, 1.0, 1.0, 1.0}),
            toDiagonalArray(new double[]{1.0, 1.0, 1.0, 1.0}),
            DoubleVertex::plus
        );
    }
}
