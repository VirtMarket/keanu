package io.improbable.keanu.vertices;

import io.improbable.keanu.DeterministicRule;
import io.improbable.keanu.algorithms.mcmc.MetropolisHastings;
import io.improbable.keanu.algorithms.variational.optimizer.KeanuOptimizer;
import io.improbable.keanu.network.BayesianNetwork;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.ConstantBoolVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.UniformVertex;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AssertVertexTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public DeterministicRule rule = new DeterministicRule();

    @Test
    public void assertThrowsOnFalseConstBool() {
        thrown.expect(AssertionError.class);
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(false));
        constBool.assertTrue().eval();
    }

    @Test
    public void assertPassesOnTrueConstBool() {
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(true));
        constBool.assertTrue().eval();
    }

    @Test
    public void lazyEvalThrowsOnFalseConstBool() {
        thrown.expect(AssertionError.class);
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(false));
        constBool.assertTrue().lazyEval();
    }

    @Test
    public void assertPassesOnRVWithTruePredicate() {
        UniformVertex uniform = new UniformVertex(0, 5);
        BoolVertex predicate = uniform.lessThan(new ConstantDoubleVertex(new double[]{10}));
        predicate.assertTrue().eval();
    }

    @Test
    public void assertPassesOnRVWithFalsePredicate() {
        thrown.expect(AssertionError.class);
        UniformVertex uniform = new UniformVertex(0, 5);
        BoolVertex predicate = uniform.greaterThan(new ConstantDoubleVertex(new double[]{10}));
        predicate.assertTrue().eval();
    }

    @Test
    public void samplingWithAssertionWorks() {
        thrown.expect(AssertionError.class);
        GaussianVertex gaussian = new GaussianVertex(5, 1);
        gaussian.greaterThan(new ConstantDoubleVertex(1000)).assertTrue();

        BayesianNetwork bayesianNetwork = new BayesianNetwork(gaussian.getConnectedGraph());
        MetropolisHastings.withDefaultConfig().generatePosteriorSamples(bayesianNetwork, bayesianNetwork.getLatentVertices()).generate(10);
    }

    @Test
    public void valuePropagationPassesOnTrueAssertion() {
        ConstantDoubleVertex constDouble = new ConstantDoubleVertex(3);
        constDouble.lessThan(new ConstantDoubleVertex(10)).assertTrue();
        constDouble.setAndCascade(8);
    }

    @Test
    public void valuePropagationThrowsOnFalseAssertion() {
        thrown.expect(AssertionError.class);
        ConstantDoubleVertex constDouble = new ConstantDoubleVertex(3);
        constDouble.lessThan(new ConstantDoubleVertex(10)).assertTrue();
        constDouble.setAndCascade(15);
    }

    @Test
    public void optimizerWithAssertionWorks() {
        thrown.expect(AssertionError.class);
        UniformVertex temperature = new UniformVertex(20, 30);
        GaussianVertex observedTemp = new GaussianVertex(temperature, 1);
        observedTemp.observe(29);
        temperature.greaterThan(new ConstantDoubleVertex(34)).assertTrue();
        KeanuOptimizer.of(temperature.getConnectedGraph()).maxAPosteriori();
    }

    @Test
    public void assertGivesCorrectErrorWhenLabelledAndMessagePresent() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("AssertVertex (testAssert): this is wrong");
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(false));
        AssertVertex assertVertex = constBool.assertTrue("this is wrong");
        assertVertex.setLabel("testAssert");
        assertVertex.eval();
    }

    @Test
    public void assertGivesCorrectErrorWhenLabelled() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("AssertVertex (testAssert)");
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(false));
        AssertVertex assertVertex = constBool.assertTrue();
        assertVertex.setLabel("testAssert");
        assertVertex.eval();
    }

    @Test
    public void assertGivesCorrectErrorWhenMessagePresent() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("AssertVertex: this is wrong");
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(false));
        AssertVertex assertVertex = constBool.assertTrue("this is wrong");
        assertVertex.eval();
    }

    @Test
    public void assertGivesCorrectErrorWhenPlain() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("AssertVertex");
        ConstantBoolVertex constBool = new ConstantBoolVertex(BooleanTensor.create(false));
        AssertVertex assertVertex = constBool.assertTrue();
        assertVertex.eval();
    }
}
