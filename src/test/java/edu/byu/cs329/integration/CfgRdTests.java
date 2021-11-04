package edu.byu.cs329.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import edu.byu.cs329.TestUtils;
import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.cfg.ControlFlowGraphBuilder;
import edu.byu.cs329.rd.ReachingDefinitions;
import edu.byu.cs329.rd.ReachingDefinitionsBuilder;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;

public class CfgRdTests {
    ControlFlowGraphBuilder unitUnderTest = null;
    ControlFlowGraph controlFlowGraph = null;
    ReachingDefinitionsBuilder rdUnitUnderTest = null;

    @BeforeEach
    void beforeEach() {
        unitUnderTest = new ControlFlowGraphBuilder();
        rdUnitUnderTest = new ReachingDefinitionsBuilder();
    }
    void init(String fileName) {
        ASTNode node = TestUtils.getASTNodeFor(this, fileName);
        List<ControlFlowGraph> cfgList = unitUnderTest.build(node);
        assertEquals(1, cfgList.size());
        controlFlowGraph = cfgList.get(0);
        }

  @Test
  @DisplayName("Test That Shows rd and cfg working together")
  public void IntegrationTest(){
    String fileName = "cfgInputs/returnTestAddSuccess.java";
    init(fileName);
    ReachingDefinitions reachingDefinitions = getReachingDefinitions(controlFlowGraph);
    Set<Definition> definitions = reachingDefinitions.getReachingDefinitions(controlFlowGraph.getStart());
    assertEquals(1, definitions.size());
  }

  private ReachingDefinitions getReachingDefinitions(ControlFlowGraph controlFlowGraph) {
    List<ControlFlowGraph> list = new ArrayList<ControlFlowGraph>();
    list.add(controlFlowGraph);
    List<ReachingDefinitions> reachingDefinitionsList = rdUnitUnderTest.build(list);
    assertEquals(1, reachingDefinitionsList.size());
    return reachingDefinitionsList.get(0);
  }
}
