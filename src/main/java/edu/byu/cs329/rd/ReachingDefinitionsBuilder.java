package edu.byu.cs329.rd;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;



public class ReachingDefinitionsBuilder {
  private List<ReachingDefinitions> rdList = null;
  private Map<Statement, Set<Definition>> entrySetMap = null;
  private SimpleName name = null;

  /**
   * Computes the reaching definitions for each control flow graph.
   * 
   * @param cfgList the list of control flow graphs.
   * @return the coresponding reaching definitions for each graph.
   */
  public List<ReachingDefinitions> build(List<ControlFlowGraph> cfgList) {
    rdList = new ArrayList<ReachingDefinitions>();
    for (ControlFlowGraph cfg : cfgList) {
      ReachingDefinitions rd = computeReachingDefinitions(cfg);
      rdList.add(rd);
    }
    return rdList;
  }

  private ReachingDefinitions computeReachingDefinitions(ControlFlowGraph cfg) {
    Set<Definition> parameterDefinitions = createParameterDefinitions(cfg.getMethodDeclaration());
    entrySetMap = new HashMap<Statement, Set<Definition>>();
    Statement start = cfg.getStart();
    entrySetMap.put(start, parameterDefinitions);
    
    List<Statement> worklist = new ArrayList<Statement>();
    worklist.add(start);
    Set<Definition> oldEntry = parameterDefinitions;
    Set<Definition> oldExit = new HashSet<Definition>();
    Set<Definition> newEntry = new HashSet<Definition>();
    Set<Definition> newExit = new HashSet<Definition>();
    while (!worklist.isEmpty()){
      Statement stmt = worklist.remove(0);
      newEntry = new HashSet<Definition>();
      newExit = new HashSet<Definition>();
      if (!oldExit.isEmpty()){
        newEntry.addAll(oldExit);
      }
      else newEntry = oldEntry;
      if (stmt instanceof ExpressionStatement){
        newExit.addAll(oldExit);
        Expression curExp = ((ExpressionStatement) stmt).getExpression();
      
        if (curExp instanceof Assignment){
          Definition def = new Definition();

          def.name = name;
          def.statement = stmt;
          newExit.add(def);
        }
      }
      else if (stmt instanceof VariableDeclarationStatement){
        newExit.addAll(oldExit);
        @SuppressWarnings("unchecked")
        List<VariableDeclaration> curExp = ((VariableDeclarationStatement) stmt).fragments();
      
        for (VariableDeclaration vd : curExp){
          Definition def = new Definition();
          def.name = vd.getName();
          def.statement = stmt;
          newExit.add(def);
        }
      }
      else{
        newExit = oldExit;
      }
      if (!newExit.equals(oldExit)){
        worklist.addAll(cfg.getSuccs(stmt));
      }
      
      entrySetMap.put(stmt, newEntry);
      oldEntry = newEntry;
      oldExit = newExit;
    } 
    
    return new ReachingDefinitions() {
      final Map<Statement, Set<Definition>> reachingDefinitions = 
          Collections.unmodifiableMap(entrySetMap);

      @Override 
      public Set<Definition> getReachingDefinitions(final Statement s) {
        Set<Definition> returnValue = null;
        if (reachingDefinitions.containsKey(s)) {
          returnValue = reachingDefinitions.get(s);
        }
        return returnValue;
      }
    };
  }

  private Set<Definition> createParameterDefinitions(MethodDeclaration methodDeclaration) {
    List<VariableDeclaration> parameterList = 
        getParameterList(methodDeclaration.parameters());
    Set<Definition> set = new HashSet<Definition>();

    for (VariableDeclaration parameter : parameterList) {
      Definition definition = createDefinition(parameter.getName(), null);
      set.add(definition);  
    }

    return set;
  }

  private Definition createDefinition(SimpleName name, Statement statement) {
    Definition definition = new Definition();
    definition.name = name;
    definition.statement = statement;
    return definition;
  }

  private List<VariableDeclaration> getParameterList(Object list) {
    @SuppressWarnings("unchecked")
    List<VariableDeclaration> statementList = (List<VariableDeclaration>)(list);
    return statementList;
  }
}
