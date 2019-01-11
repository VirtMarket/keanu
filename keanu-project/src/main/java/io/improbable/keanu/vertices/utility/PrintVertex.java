package io.improbable.keanu.vertices.utility;

import com.google.common.base.Preconditions;
import io.improbable.keanu.annotation.ExportVertexToPythonBindings;
import io.improbable.keanu.vertices.LoadVertexParam;
import io.improbable.keanu.vertices.NonProbabilistic;
import io.improbable.keanu.vertices.SaveVertexParam;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import java.io.PrintStream;

public class PrintVertex<T> extends Vertex<T> implements NonProbabilistic<T> {

    private static PrintStream printStream = System.out;

    public static void setPrintStream(PrintStream printStream) {
        PrintVertex.printStream = Preconditions.checkNotNull(printStream);
    }

    private static final String PARENT = "parent";
    private static final String MESSAGE = "message";
    private static final String PRINT_DATA = "printData";

    private final Vertex<T> parent;

    private final String message;


    private final boolean printData;

    @ExportVertexToPythonBindings
    public PrintVertex(@LoadVertexParam(PARENT) Vertex<T> parent,
                       @LoadVertexParam(MESSAGE) final String message,
                       @LoadVertexParam(PRINT_DATA) boolean printData) {
        super(parent.getShape());
        this.parent = parent;
        this.message = Preconditions.checkNotNull(message);
        this.printData = printData;
        setParents(parent);
    }

    public PrintVertex(Vertex<T> parent) {
        this(parent, "Calculated Vertex:\n", true);
    }

    @Override
    public T sample(KeanuRandom random) {
        return parent.sample();
    }

    @Override
    public T calculate() {
        final String dataOutput = printData ? parent.getValue().toString() + "\n" : "";
        printStream.print(message + dataOutput);
        return parent.getValue();
    }

    @SaveVertexParam(PARENT)
    public Vertex<T> getParent() {
        return parent;
    }

    @SaveVertexParam(MESSAGE)
    public String getMessage() {
        return message;
    }

    @SaveVertexParam(PRINT_DATA)
    public boolean printData() {
        return printData;
    }
}
