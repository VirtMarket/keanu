from keanu import BayesNet, KeanuRandom, Model
from keanu.vertex import Gaussian, Uniform
from keanu.algorithm import sample, MetropolisHastingsSampler
from keanu import stats
import numpy as np

def autocorrelation_example_scalar():
    with Model() as m:
        m.a = Gaussian(20, 1.)
        m.b = Gaussian(20, 1.)
        m.c = Gaussian(m.a+m.b, 1.)
    m.c.observe(43.)
    m.a.set_value(20.)
    m.b.set_value(20.)
    bayes_net = m.to_bayes_net()
    # %%SNIPPET_START%% PythonScalarAutocorrelation
    algo = MetropolisHastingsSampler()
    posterior_samples = sample(net=bayes_net, sample_from=bayes_net.get_latent_vertices(),
                               sampling_algorithm=algo, draws=100)
    vertex_samples = posterior_samples.get('a')
    ac = stats.autocorrelation(vertex_samples)
    # %%SNIPPET_END%% PythonScalarAutocorrelation

def autocorrelation_example_nd():
    with Model() as m:
        m.a = Gaussian(np.array([[20., 30.], [40., 60.]]), np.array([[1., 1.], [1., 1.]]))
    bayes_net = m.to_bayes_net()
    # %%SNIPPET_START%% PythonNdAutocorrelation
    algo = MetropolisHastingsSampler()
    posterior_samples = sample(net=bayes_net, sample_from=bayes_net.get_latent_vertices(),
                               sampling_algorithm=algo, draws=100)
    vertex_samples = posterior_samples.get('a')
    ac = stats.autocorrelation(vertex_samples, (0,1))
    # %%SNIPPET_END%% PythonNdAutocorrelation

autocorrelation_example_scalar()
autocorrelation_example_nd()