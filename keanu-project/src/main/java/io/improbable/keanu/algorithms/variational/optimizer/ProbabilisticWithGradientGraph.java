package io.improbable.keanu.algorithms.variational.optimizer;

import io.improbable.keanu.tensor.dbl.DoubleTensor;

import java.util.Map;

public interface ProbabilisticWithGradientGraph extends ProbabilisticGraph {

    Map<? extends VariableReference, DoubleTensor> logProbGradients(Map<VariableReference, ?> inputs);

    Map<? extends VariableReference, DoubleTensor> logProbGradients();

    Map<? extends VariableReference, DoubleTensor> logLikelihoodGradients(Map<VariableReference, ?> inputs);

    Map<? extends VariableReference, DoubleTensor> logLikelihoodGradients();

}
