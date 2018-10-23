package io.improbable.keanu.backend.tensorflow;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import io.improbable.keanu.backend.ComputableGraph;
import io.improbable.keanu.backend.ProbabilisticWithGradientGraph;
import io.improbable.keanu.network.BayesianNetwork;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.VertexId;
import io.improbable.keanu.vertices.VertexLabel;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.bool.probabilistic.BernoulliVertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.LogProbGradientCalculator;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.probabilistic.UniformIntVertex;

public class TensorflowGraphConverterTest {

    @Test
    public void canRunSimpleAddition() {
        DoubleVertex A = new GaussianVertex(0, 1);
        A.setValue(2);
        DoubleVertex B = new GaussianVertex(1, 1);
        B.setValue(3);

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        DoubleVertex C = A.plus(B);

        String outputName = "someOutput";
        C.setLabel(new VertexLabel(outputName));

        ComputableGraph graph = TensorflowGraphConverter.convert(C.getConnectedGraph());

        Map<String, DoubleTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());

        DoubleTensor result = graph.compute(inputs, outputName);

        assertEquals(C.getValue(), result);
    }

    @Test
    public void canRunDoubleTensorAddition() {
        DoubleVertex A = new GaussianVertex(new long[]{2, 2}, 0, 1);
        A.setValue(DoubleTensor.create(new double[]{1, 2, 3, 4}, 2, 2));

        DoubleVertex B = new GaussianVertex(new long[]{2, 2}, 1, 1);
        B.setValue(DoubleTensor.create(new double[]{5, 6, 7, 8}, 2, 2));

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        DoubleVertex C = A.plus(B);

        String outputName = "someOutput";
        C.setLabel(new VertexLabel(outputName));

        ComputableGraph graph = TensorflowGraphConverter.convert(C.getConnectedGraph());

        Map<String, DoubleTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());

        DoubleTensor result = graph.compute(inputs, outputName);

        assertEquals(C.getValue(), result);
    }

    @Test
    public void canRunIntegerTensorAddition() {
        IntegerVertex A = new UniformIntVertex(new long[]{2, 2}, 0, 1);
        A.setValue(IntegerTensor.create(new int[]{1, 2, 3, 4}, 2, 2));

        IntegerVertex B = new UniformIntVertex(new long[]{2, 2}, 1, 1);
        B.setValue(IntegerTensor.create(new int[]{5, 6, 7, 8}, 2, 2));

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        IntegerVertex C = A.times(B).abs();

        String outputName = "someOutput";
        C.setLabel(new VertexLabel(outputName));

        ComputableGraph graph = TensorflowGraphConverter.convert(C.getConnectedGraph());

        Map<String, IntegerTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());

        IntegerTensor result = graph.compute(inputs, outputName);

        assertEquals(C.getValue(), result);
    }

    @Test
    public void canRunTensorAnd() {
        BoolVertex A = new BernoulliVertex(new long[]{2, 2}, 0.5);
        A.setValue(BooleanTensor.create(new boolean[]{true, false, true, false}, 2, 2));

        BoolVertex B = new BernoulliVertex(new long[]{2, 2}, 0.75);
        B.setValue(BooleanTensor.create(new boolean[]{false, false, true, true}, 2, 2));

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        BoolVertex C = A.and(B).not();

        String outputName = "someOutput";
        C.setLabel(new VertexLabel(outputName));

        ComputableGraph graph = TensorflowGraphConverter.convert(C.getConnectedGraph());

        Map<String, BooleanTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());

        BooleanTensor result = graph.compute(inputs, outputName);

        assertEquals(C.getValue(), result);
    }

    @Test
    public void canTensorConcat() {
        DoubleVertex A = new GaussianVertex(new long[]{2, 2}, 0, 1);
        A.setValue(DoubleTensor.create(new double[]{1, 2, 3, 4}, 2, 2));

        DoubleVertex B = new GaussianVertex(new long[]{2, 2}, 1, 1);
        B.setValue(DoubleTensor.create(new double[]{5, 6, 7, 8}, 2, 2));

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        DoubleVertex C = DoubleVertex.concat(0, A, B);

        String outputName = "someOutput";
        C.setLabel(new VertexLabel(outputName));

        ComputableGraph graph = TensorflowGraphConverter.convert(C.getConnectedGraph());

        Map<String, DoubleTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());

        DoubleTensor result = graph.compute(inputs, outputName);

        assertEquals(C.getValue(), result);
    }

    /**
     * Tensorflow does not support autodiff through a concat at the moment
     */
    @Test
    @Ignore
    public void canAutoDiffTensorConcat() {
        DoubleVertex A = new GaussianVertex(new long[]{2, 2}, 0, 1);
        A.setValue(DoubleTensor.create(new double[]{1, 2, 3, 4}, 2, 2));
        A.setLabel(new VertexLabel("A"));

        DoubleVertex B = new GaussianVertex(new long[]{2, 2}, 1, 1);
        B.setValue(DoubleTensor.create(new double[]{5, 6, 7, 8}, 2, 2));
        B.setLabel(new VertexLabel("B"));

        DoubleVertex C = new GaussianVertex(new long[]{2, 2}, 1, 1);
        C.setValue(DoubleTensor.create(new double[]{-3, 2, -4, 9}, 2, 2));
        C.setLabel(new VertexLabel("C"));

        DoubleVertex D = DoubleVertex.concat(0, A.times(C), B.times(C));
        DoubleVertex E = new GaussianVertex(new long[]{4, 2}, D, 1);
        E.setLabel(new VertexLabel("E"));
        E.observe(DoubleTensor.create(new double[]{0, 0, 0, 0, 0, 0, 0, 0}, 4, 2));

        Map<String, DoubleTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());
        inputs.put(C.getLabel().toString(), C.getValue());
        inputs.put(E.getLabel().toString(), E.getValue());

        BayesianNetwork network = new BayesianNetwork(E.getConnectedGraph());
        double expectedLogProb = network.getLogOfMasterP();

        LogProbGradientCalculator calculator = new LogProbGradientCalculator(
            network.getLatentOrObservedVertices(),
            network.getContinuousLatentVertices()
        );

        Map<VertexId, DoubleTensor> keanuGradients = calculator.getJointLogProbGradientWrtLatents();

        try (ProbabilisticWithGradientGraph graph = TensorflowGraphConverter.convertWithGradient(network)) {

            double tensorflowLogProb = graph.logProb(inputs);
            Map<String, DoubleTensor> tensorflowGradients = graph.logProbGradients(inputs);

            assertEquals(keanuGradients.get(A.getId()), tensorflowGradients.get("A"));
            assertEquals(keanuGradients.get(B.getId()), tensorflowGradients.get("B"));
            assertEquals(keanuGradients.get(C.getId()), tensorflowGradients.get("C"));
            assertEquals(keanuGradients.get(E.getId()), tensorflowGradients.get("E"));
            assertEquals(expectedLogProb, tensorflowLogProb, 1e-2);
        }
    }

    @Test
    public void canRunTensorMultiplication() {
        DoubleVertex A = new GaussianVertex(new long[]{2, 2}, 0, 1);
        A.setValue(DoubleTensor.create(new double[]{1, 2, 3, 4}, 2, 2));

        DoubleVertex B = new GaussianVertex(new long[]{2, 2}, 1, 1);
        B.setValue(DoubleTensor.create(new double[]{5, 6, 7, 8}, 2, 2));

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        DoubleVertex C = A.plus(B);
        DoubleVertex D = C.times(B);
        DoubleVertex out = D.matrixMultiply(C);

        String outputName = "output";
        out.setLabel(new VertexLabel(outputName));

        ComputableGraph graph = TensorflowGraphConverter.convert(C.getConnectedGraph());

        Map<String, DoubleTensor> inputs = new HashMap<>();
        inputs.put(A.getLabel().toString(), A.getValue());
        inputs.put(B.getLabel().toString(), B.getValue());

        DoubleTensor result = graph.compute(inputs, outputName);

        assertEquals(out.getValue(), result);
    }

    @Test
    public void canRunLogProbabilityAndGradientLogProbabilityOfGaussian() {

        long n = 20;
        long[] shape = new long[]{n, n};

        GaussianVertex A = new GaussianVertex(shape, 0, 1);
        GaussianVertex B = new GaussianVertex(shape, 1, 1);

        DoubleTensor initialA = A.sample();
        DoubleTensor initialB = B.sample();

        A.setValue(initialA);
        B.setValue(initialB);

        A.setLabel(new VertexLabel("A"));
        B.setLabel(new VertexLabel("B"));

        DoubleVertex C = A.matrixMultiply(B).matrixMultiply(A).times(0.5).matrixMultiply(B);

        DoubleVertex CObserved = new GaussianVertex(shape, C, 2);
        CObserved.observe(initialA);
        BayesianNetwork network = new BayesianNetwork(C.getConnectedGraph());

        double expectedLogProb = network.getLogOfMasterP();

        LogProbGradientCalculator calculator = new LogProbGradientCalculator(
            network.getLatentOrObservedVertices(),
            network.getContinuousLatentVertices()
        );

        Map<VertexId, DoubleTensor> keanuGradients = calculator.getJointLogProbGradientWrtLatents();

        try (ProbabilisticWithGradientGraph graph = TensorflowGraphConverter.convertWithGradient(network)) {

            Map<String, DoubleTensor> inputs = new HashMap<>();
            inputs.put(A.getLabel().toString(), initialA);
            inputs.put(B.getLabel().toString(), initialB);

            double tensorflowResult = graph.logProb(inputs);
            Map<String, DoubleTensor> tensorflowGradients = graph.logProbGradients(inputs);

            assertEquals(keanuGradients.get(A.getId()), tensorflowGradients.get("A"));
            assertEquals(keanuGradients.get(B.getId()), tensorflowGradients.get("B"));
            assertEquals(expectedLogProb, tensorflowResult, 1e-2);
        }

    }

}