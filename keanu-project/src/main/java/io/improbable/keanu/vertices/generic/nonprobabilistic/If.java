package io.improbable.keanu.vertices.generic.nonprobabilistic;

import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.ConstantVertex;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.bool.BooleanVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.BooleanIfVertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.DoubleIfVertex;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.IntegerIfVertex;

public class If {

    private If() {
    }

    public static IfThenBuilder isTrue(Vertex<BooleanTensor> predicate) {
        return new IfThenBuilder(predicate);
    }

    public static class IfThenBuilder {
        private final Vertex<? extends BooleanTensor> predicate;

        public IfThenBuilder(Vertex<? extends BooleanTensor> predicate) {
            this.predicate = predicate;
        }

        public <T> IfThenElseBuilder<T> then(Vertex<? extends Tensor<T>> thn) {
            return new IfThenElseBuilder<>(predicate, thn);
        }

        public BooleanIfThenElseBuilder then(BooleanVertex thn) {
            return new BooleanIfThenElseBuilder(predicate, thn);
        }

        public BooleanIfThenElseBuilder then(boolean thn) {
            return then(ConstantVertex.of(thn));
        }

        public DoubleIfThenElseBuilder then(DoubleVertex thn) {
            return new DoubleIfThenElseBuilder(predicate, thn);
        }

        public DoubleIfThenElseBuilder then(double thn) {
            return then(ConstantVertex.of(thn));
        }

        public IntegerIfThenElseBuilder then(IntegerVertex thn) {
            return new IntegerIfThenElseBuilder(predicate, thn);
        }

        public IntegerIfThenElseBuilder then(int thn) {
            return then(ConstantVertex.of(thn));
        }
    }

    public static class IfThenElseBuilder<T> {

        private final Vertex<? extends BooleanTensor> predicate;
        private final Vertex<? extends Tensor<T>> thn;

        public IfThenElseBuilder(Vertex<? extends BooleanTensor> predicate,
                                 Vertex<? extends Tensor<T>> thn) {
            this.predicate = predicate;
            this.thn = thn;
        }

        public IfVertex<T> orElse(Vertex<? extends Tensor<T>> els) {
            return new IfVertex<>(predicate, thn, els);
        }
    }

    public static class BooleanIfThenElseBuilder {

        private final Vertex<? extends BooleanTensor> predicate;
        private final Vertex<? extends BooleanTensor> thn;

        public BooleanIfThenElseBuilder(Vertex<? extends BooleanTensor> predicate,
                                        Vertex<? extends BooleanTensor> thn) {
            this.predicate = predicate;
            this.thn = thn;
        }

        public BooleanIfVertex orElse(Vertex<? extends BooleanTensor> els) {
            return new BooleanIfVertex(predicate, thn, els);
        }

        public BooleanIfVertex orElse(boolean els) {
            return orElse(ConstantVertex.of(els));
        }
    }

    public static class DoubleIfThenElseBuilder {

        private final Vertex<? extends BooleanTensor> predicate;
        private final DoubleVertex thn;

        public DoubleIfThenElseBuilder(Vertex<? extends BooleanTensor> predicate,
                                       DoubleVertex thn) {
            this.predicate = predicate;
            this.thn = thn;
        }

        public DoubleIfVertex orElse(DoubleVertex els) {
            return new DoubleIfVertex(predicate, thn, els);
        }

        public DoubleIfVertex orElse(double els) {
            return orElse(ConstantVertex.of(els));
        }
    }

    public static class IntegerIfThenElseBuilder {

        private final Vertex<? extends BooleanTensor> predicate;
        private final IntegerVertex thn;

        public IntegerIfThenElseBuilder(Vertex<? extends BooleanTensor> predicate,
                                        IntegerVertex thn) {
            this.predicate = predicate;
            this.thn = thn;
        }

        public IntegerIfVertex orElse(IntegerVertex els) {
            return new IntegerIfVertex(predicate, thn, els);
        }

        public IntegerIfVertex orElse(int els) {
            return orElse(ConstantVertex.of(els));
        }
    }
}
