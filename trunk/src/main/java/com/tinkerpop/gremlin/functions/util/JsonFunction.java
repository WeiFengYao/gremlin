package com.tinkerpop.gremlin.functions.util;

import com.tinkerpop.gremlin.FunctionHelper;
import com.tinkerpop.gremlin.GremlinFunctions;
import com.tinkerpop.gremlin.model.Edge;
import com.tinkerpop.gremlin.model.Element;
import com.tinkerpop.gremlin.model.Vertex;
import com.tinkerpop.gremlin.statements.EvaluationException;
import com.tinkerpop.gremlin.statements.Tokens;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Function;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class JsonFunction implements Function {

    public static final String FUNCTION_NAME = "json";
    private static final String ID = "_id";
    private static final String PROPERTIES = "properties";
    private static final String TYPE = "type";
    private static final String EDGE = "edge";
    private static final String VERTEX = "vertex";

    public String invoke(final ExpressionContext context, final Object[] parameters) {

        if (null != parameters && parameters.length == 1) {
            Object object = FunctionHelper.nodeSetConversion(parameters[0]);
            if (object instanceof List) {
                return createJSONList((List) object).toJSONString();
            } else if (object instanceof Map) {
                JSONObject jsonMap = new JSONObject();
                jsonMap.putAll((Map) object);
                return jsonMap.toJSONString();
            } else if (object instanceof Element) {
                return createJSONElement((Element) object).toJSONString();
            } else {
                return object.toString();
            }
        }

        throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctions.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.UNSUPPORTED_PARAMETERS);

    }

    private static JSONArray createJSONList(List list) {
        JSONArray jsonArray = new JSONArray();
        for (Object item : list) {
            if (item instanceof Element) {
                jsonArray.add(createJSONElement((Element) item));
            } else if (item instanceof List) {
                jsonArray.add(createJSONList((List) item));
            } else if (item instanceof Map) {
                jsonArray.add(createJSONMap((Map) item));
            } else {
                jsonArray.add(item);
            }
        }
        return jsonArray;
    }

    private static JSONObject createJSONMap(Map map) {
        JSONObject jsonMap = new JSONObject();
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof List) {
                value = createJSONList((List) value);
            } else if (value instanceof Map) {
                value = createJSONMap((Map) value);
            } else if (value instanceof Element) {
                value = createJSONElement((Element) value);
            }

            if (key instanceof List) {
                key = createJSONList((List) key);
            } else if (key instanceof Map) {
                key = createJSONMap((Map) key);
            } else if (key instanceof Element) {
                key = createJSONElement((Element) key);
            }
            jsonMap.put(key, value);
        }
        return jsonMap;

    }

    private static JSONObject createJSONElement(Element element) {
        JSONObject jsonElement = new JSONObject();
        jsonElement.put(ID, element.getId());
        jsonElement.put(PROPERTIES, createJSONMap(createPropertyMap(element)));
        if (element instanceof Vertex) {
            Vertex vertex = (Vertex) element;
            jsonElement.put(TYPE, VERTEX);
            JSONArray jsonArrayOut = new JSONArray();
            for (Edge edge : vertex.getOutEdges()) {
                jsonArrayOut.add(edge.getId());
            }
            jsonElement.put(Tokens.OUT_EDGES, jsonArrayOut);
            JSONArray jsonArrayIn = new JSONArray();
            for (Edge edge : vertex.getInEdges()) {
                jsonArrayIn.add(edge.getId());
            }
            jsonElement.put(Tokens.IN_EDGES, jsonArrayIn);
        } else if (element instanceof Edge) {
            Edge edge = (Edge) element;
            jsonElement.put(TYPE, EDGE);
            jsonElement.put(Tokens.OUT_VERTEX, edge.getOutVertex().getId());
            jsonElement.put(Tokens.IN_VERTEX, edge.getInVertex().getId());

        }
        return jsonElement;
    }

    private static Map createPropertyMap(Element element) {
        Map map = new HashMap();
        for(String key : element.getPropertyKeys()) {
            map.put(key, element.getProperty(key));
        }
        return map;
    }
}