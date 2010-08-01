package com.tinkerpop.gremlin.compiler.functions.g.lme;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.gremlin.BaseTest;
import com.tinkerpop.gremlin.GremlinScriptEngine;
import com.tinkerpop.gremlin.compiler.types.Atom;
import com.tinkerpop.gremlin.compiler.context.GremlinScriptContext;
import com.tinkerpop.gremlin.compiler.functions.Function;
import com.tinkerpop.gremlin.compiler.operations.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ListFunctionTest extends BaseTest {

    public void testEmptyList() {
        GremlinScriptContext context = new GremlinScriptContext();
        Function<Iterable> function = new ListFunction();
        this.stopWatch();
        Atom<Iterable> atom = function.compute(new ArrayList<Operation>(), context);
        printPerformance(function.getFunctionName() + " function", 0, "arguments", this.stopWatch());
        assertEquals(count(atom.getValue()), 0);
    }

    public void testListGremlin() throws Exception {

        final GremlinScriptEngine engine   = new GremlinScriptEngine();
        final GremlinScriptContext context = new GremlinScriptContext();

        this.stopWatch();
        
        String script = "g:list(1,2,3)[0]";
        Iterable itty = (Iterable) ((Iterable) engine.eval(script, context)).iterator().next();
        printPerformance(script, 1, "pipe constructed", this.stopWatch());
        this.stopWatch();
        List<Vertex> results = asList(itty);
        printPerformance(script, 1, "pipe listed", this.stopWatch());
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), 1);

        this.stopWatch();
        script = "g:list(1,2,3)[2]";
        itty = (Iterable) ((Iterable) engine.eval(script, context)).iterator().next();
        printPerformance(script, 1, "pipe constructed", this.stopWatch());
        this.stopWatch();
        results = asList(itty);
        printPerformance(script, 1, "pipe listed", this.stopWatch());
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), 3);

        this.stopWatch();
        script = "g:list(1,2,3)[3]";
        itty = (Iterable) ((Iterable) engine.eval(script, context)).iterator().next();
        printPerformance(script, 1, "pipe constructed", this.stopWatch());
        this.stopWatch();
        results = asList(itty);
        printPerformance(script, 1, "pipe listed", this.stopWatch());
        assertEquals(results.size(), 0);
    }
}
