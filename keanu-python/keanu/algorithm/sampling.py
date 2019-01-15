from py4j.java_gateway import java_import, JavaObject
from py4j.java_collections import JavaList

from keanu.algorithm._proposal_distribution import ProposalDistribution
from keanu.algorithm.proposal_listeners import proposal_listener_types
from keanu.context import KeanuContext
from keanu.tensor import Tensor
from keanu.vertex.base import Vertex
from keanu.net import BayesNet
from typing import Any, Iterable, Dict, List, Tuple, Generator, Optional
from keanu.vartypes import sample_types, sample_generator_types, numpy_types
from keanu.plots import traceplot

k = KeanuContext()

java_import(k.jvm_view(), "io.improbable.keanu.algorithms.mcmc.MetropolisHastings")
java_import(k.jvm_view(), "io.improbable.keanu.algorithms.mcmc.nuts.NUTS")
java_import(k.jvm_view(), "io.improbable.keanu.algorithms.mcmc.Hamiltonian")

algorithms = {
    'metropolis': k.jvm_view().MetropolisHastings,
    'NUTS': k.jvm_view().NUTS,
    'hamiltonian': k.jvm_view().Hamiltonian
}


class PosteriorSamplingAlgorithm:

    def __init__(self, sampler: JavaObject):
        self._sampler = sampler

    def get_sampler(self) -> JavaObject:
        return self._sampler


class MetropolisHastingsSampler(PosteriorSamplingAlgorithm):

    def __init__(self,
                 proposal_distribution: str = None,
                 proposal_distribution_sigma: numpy_types = None,
                 proposal_listeners=[]):

        if (proposal_distribution is None and len(proposal_listeners) > 0):
            raise TypeError("If you pass in proposal_listeners you must also specify proposal_distribution")

        builder: JavaObject = k.jvm_view().MetropolisHastings.builder()

        if proposal_distribution is not None:
            proposal_distribution_object = ProposalDistribution(
                type_=proposal_distribution, sigma=proposal_distribution_sigma, listeners=proposal_listeners)
            builder = builder.proposalDistribution(proposal_distribution_object.unwrap())

        sampling_algorithm: JavaObject = builder.build()
        super().__init__(sampling_algorithm)


class HamiltonianSampler(PosteriorSamplingAlgorithm):

    def __init__(self):
        pass


class NUTSSampler(PosteriorSamplingAlgorithm):

    def __init__(self):
        pass


def sample(net: BayesNet,
           sample_from: Iterable[Vertex],
           sampling_algorithm: PosteriorSamplingAlgorithm = MetropolisHastingsSampler(),
           draws: int = 500,
           drop: int = 0,
           down_sample_interval: int = 1,
           plot: bool = False,
           ax: Any = None) -> sample_types:

    vertices_unwrapped: JavaList = k.to_java_object_list(sample_from)

    network_samples: JavaObject = sampling_algorithm.get_sampler().getPosteriorSamples(
        net.unwrap(), vertices_unwrapped, draws).drop(drop).downSample(down_sample_interval)

    vertex_samples = {
        Vertex._get_python_label(vertex_unwrapped): list(
            map(Tensor._to_ndarray,
                network_samples.get(vertex_unwrapped).asList())) for vertex_unwrapped in vertices_unwrapped
    }

    if plot:
        traceplot(vertex_samples, ax=ax)

    return vertex_samples


def generate_samples(net: BayesNet,
                     sample_from: Iterable[Vertex],
                     sampling_algorithm: PosteriorSamplingAlgorithm = MetropolisHastingsSampler(),
                     drop: int = 0,
                     down_sample_interval: int = 1,
                     live_plot: bool = False,
                     refresh_every: int = 100,
                     ax: Any = None) -> sample_generator_types:

    vertices_unwrapped: JavaList = k.to_java_object_list(sample_from)

    samples: JavaObject = sampling_algorithm.get_sampler().generatePosteriorSamples(net.unwrap(), vertices_unwrapped)
    samples = samples.dropCount(drop).downSampleInterval(down_sample_interval)
    sample_iterator: JavaObject = samples.stream().iterator()

    return _samples_generator(
        sample_iterator, vertices_unwrapped, live_plot=live_plot, refresh_every=refresh_every, ax=ax)


def _samples_generator(sample_iterator: JavaObject, vertices_unwrapped: JavaList, live_plot: bool, refresh_every: int,
                       ax: Any) -> sample_generator_types:
    traces = []
    x0 = 0
    while (True):
        network_sample = sample_iterator.next()
        sample = {
            Vertex._get_python_label(vertex_unwrapped): Tensor._to_ndarray(network_sample.get(vertex_unwrapped))
            for vertex_unwrapped in vertices_unwrapped
        }

        if live_plot:
            traces.append(sample)
            if len(traces) % refresh_every == 0:
                joined_trace = {k: [t[k] for t in traces] for k in sample.keys()}
                if ax is None:
                    ax = traceplot(joined_trace, x0=x0)
                else:
                    traceplot(joined_trace, ax=ax, x0=x0)
                x0 += refresh_every
                traces = []

        yield sample
