package io.improbable.keanu.network;

import com.google.common.primitives.Longs;
import io.improbable.keanu.KeanuSavedBayesNet;
import io.improbable.keanu.vertices.ConstantVertex;
import io.improbable.keanu.vertices.NonSaveableVertex;
import io.improbable.keanu.vertices.SaveVertexParam;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.ConstantBoolVertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.ConstantIntegerVertex;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class ProtobufSaver implements NetworkSaver {
    private final BayesianNetwork net;
    private KeanuSavedBayesNet.BayesianNetwork.Builder bayesNetBuilder = null;

    public ProtobufSaver(BayesianNetwork net) {
        this.net = net;
    }

    @Override
    public void save(OutputStream output, boolean saveValues) throws IOException {
        bayesNetBuilder = KeanuSavedBayesNet.BayesianNetwork.newBuilder();

        net.save(this);

        if (saveValues) {
            net.saveValues(this);
        }

        bayesNetBuilder.build().writeTo(output);
        bayesNetBuilder = null;
    }

    @Override
    public void save(Vertex vertex) {
        if (vertex instanceof NonSaveableVertex) {
            throw new IllegalArgumentException("Trying to save a vertex that isn't Saveable");
        }

        KeanuSavedBayesNet.Vertex.Builder vertexBuilder = buildVertex(vertex);
        bayesNetBuilder.addVertices(vertexBuilder.build());
    }

    @Override
    public void save(ConstantDoubleVertex vertex) {
        KeanuSavedBayesNet.Vertex.Builder vertexBuilder = buildVertex(vertex);
        vertexBuilder.setConstantValue(getValue(vertex).getValue());
        bayesNetBuilder.addVertices(vertexBuilder.build());
    }

    @Override
    public void save(ConstantIntegerVertex vertex) {
        KeanuSavedBayesNet.Vertex.Builder vertexBuilder = buildVertex(vertex);
        vertexBuilder.setConstantValue(getValue(vertex).getValue());
        bayesNetBuilder.addVertices(vertexBuilder.build());
    }

    @Override
    public void save(ConstantBoolVertex vertex) {
        KeanuSavedBayesNet.Vertex.Builder vertexBuilder = buildVertex(vertex);
        vertexBuilder.setConstantValue(getValue(vertex).getValue());
        bayesNetBuilder.addVertices(vertexBuilder.build());
    }

    @Override
    public void save(ConstantVertex vertex) {
        throw new IllegalArgumentException("Don't know how to save an untyped ConstantVertex");
    }

    private KeanuSavedBayesNet.Vertex.Builder buildVertex(Vertex vertex) {
        KeanuSavedBayesNet.Vertex.Builder vertexBuilder = KeanuSavedBayesNet.Vertex.newBuilder();

        if (vertex.getLabel() != null) {
            vertexBuilder = vertexBuilder.setLabel(vertex.getLabel().toString());
        }

        vertexBuilder = vertexBuilder.setId(KeanuSavedBayesNet.VertexID.newBuilder().setId(vertex.getId().toString()));
        vertexBuilder = vertexBuilder.setVertexType(vertex.getClass().getCanonicalName());
        saveParents(vertexBuilder, vertex);

        return vertexBuilder;
    }

    private void saveParents(KeanuSavedBayesNet.Vertex.Builder vertexBuilder,
                             Vertex vertex) {
        Class vertexClass = vertex.getClass();
        Method[] methods = vertexClass.getMethods();

        for (Method method : methods) {
            SaveVertexParam annotation = method.getAnnotation(SaveVertexParam.class);
            if (annotation != null) {
                String parentName = annotation.value();
                vertexBuilder.addParents(getEncodedParent(vertex, parentName, method));
            }
        }
    }

    private KeanuSavedBayesNet.NamedParent getEncodedParent(Vertex vertex, String parentName, Method getParentMethod) {
        KeanuSavedBayesNet.NamedParent.Builder parentBuilder = KeanuSavedBayesNet.NamedParent.newBuilder();

        try {
            Vertex parent = (Vertex)getParentMethod.invoke(vertex);
            parentBuilder.setId(KeanuSavedBayesNet.VertexID.newBuilder().setId(parent.getId().toString()));
            parentBuilder.setName(parentName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid parent retrieval function specified", e);
        }

        return parentBuilder.build();
    }

    @Override
    public void saveValue(Vertex vertex) {
        if (vertex.hasValue()) {
            KeanuSavedBayesNet.StoredValue value = getValue(vertex, vertex.getValue().toString());
            bayesNetBuilder.addDefaultState(value);
        }
    }

    @Override
    public void saveValue(DoubleVertex vertex) {
        if (vertex.hasValue()) {
            KeanuSavedBayesNet.StoredValue value = getValue(vertex);
            bayesNetBuilder.addDefaultState(value);
        }
    }

    @Override
    public void saveValue(IntegerVertex vertex) {
        if (vertex.hasValue()) {
            KeanuSavedBayesNet.StoredValue value = getValue(vertex);
            bayesNetBuilder.addDefaultState(value);
        }
    }

    @Override
    public void saveValue(BoolVertex vertex) {
        if (vertex.hasValue()) {
            KeanuSavedBayesNet.StoredValue value = getValue(vertex);
            bayesNetBuilder.addDefaultState(value);
        }
    }

    private KeanuSavedBayesNet.StoredValue getValue(Vertex vertex, String formattedValue) {
        KeanuSavedBayesNet.GenericTensor savedValue = KeanuSavedBayesNet.GenericTensor.newBuilder()
            .addAllShape(Longs.asList(vertex.getShape()))
            .addValues(formattedValue)
            .build();

        KeanuSavedBayesNet.VertexValue value = KeanuSavedBayesNet.VertexValue.newBuilder()
            .setGenericVal(savedValue)
            .build();

        return getStoredValue(vertex, value);

    }

    private KeanuSavedBayesNet.StoredValue getValue(DoubleVertex vertex) {
        KeanuSavedBayesNet.DoubleTensor savedValue = KeanuSavedBayesNet.DoubleTensor.newBuilder()
            .addAllValues(vertex.getValue().asFlatList())
            .addAllShape(Longs.asList(vertex.getShape()))
            .build();

        KeanuSavedBayesNet.VertexValue value = KeanuSavedBayesNet.VertexValue.newBuilder()
            .setDoubleVal(savedValue)
            .build();

        return getStoredValue(vertex, value);
    }

    private KeanuSavedBayesNet.StoredValue getValue(IntegerVertex vertex) {
        KeanuSavedBayesNet.IntegerTensor savedValue = KeanuSavedBayesNet.IntegerTensor.newBuilder()
            .addAllValues(vertex.getValue().asFlatList())
            .addAllShape(Longs.asList(vertex.getShape()))
            .build();
        KeanuSavedBayesNet.VertexValue value = KeanuSavedBayesNet.VertexValue.newBuilder()
            .setIntVal(savedValue)
            .build();

        return getStoredValue(vertex, value);
    }

    private KeanuSavedBayesNet.StoredValue getValue(BoolVertex vertex) {
        KeanuSavedBayesNet.BooleanTensor savedValue = KeanuSavedBayesNet.BooleanTensor.newBuilder()
            .addAllValues(vertex.getValue().asFlatList())
            .addAllShape(Longs.asList(vertex.getShape()))
            .build();
        KeanuSavedBayesNet.VertexValue value = KeanuSavedBayesNet.VertexValue.newBuilder()
            .setBoolVal(savedValue)
            .build();

        return getStoredValue(vertex, value);
    }

    private KeanuSavedBayesNet.StoredValue getStoredValue(Vertex vertex, KeanuSavedBayesNet.VertexValue value) {
        return KeanuSavedBayesNet.StoredValue.newBuilder()
            .setId(KeanuSavedBayesNet.VertexID.newBuilder().setId(vertex.getId().toString()).build())
            .setValue(value)
            .build();
    }
}
