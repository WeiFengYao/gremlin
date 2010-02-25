package com.tinkerpop.gremlin.functions.sail;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.sail.SailGraph;
import com.tinkerpop.gremlin.functions.Function;
import com.tinkerpop.gremlin.functions.FunctionHelper;
import com.tinkerpop.gremlin.statements.EvaluationException;
import org.apache.commons.jxpath.ExpressionContext;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RemoveNamespaceFunction implements Function {

    public static final String FUNCTION_NAME = "remove-ns";

    public Boolean invoke(final ExpressionContext context, final Object[] parameters) {
        if (null != parameters) {
            Object[] objects = FunctionHelper.nodeSetConversion(parameters);
            if (objects.length == 2 && FunctionHelper.assertTypes(objects, new Class[]{SailGraph.class, String.class})) {
                ((SailGraph) objects[0]).removeNamespace((String) objects[1]);
                return Boolean.TRUE;
            } else if (objects.length == 1) {
                Graph graph = FunctionHelper.getGraph(context);
                if (graph instanceof SailGraph && objects[0] instanceof String) {
                    ((SailGraph) graph).removeNamespace((String) objects[0]);
                    return Boolean.TRUE;
                }
            }
        }
        throw EvaluationException.createException(FunctionHelper.makeFunctionName(SailFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.UNSUPPORTED_PARAMETERS);
    }

    public String getName() {
        return FUNCTION_NAME;
    }

}