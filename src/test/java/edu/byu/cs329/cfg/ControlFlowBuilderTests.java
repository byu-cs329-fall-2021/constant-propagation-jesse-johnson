package edu.byu.cs329.cfg;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import edu.byu.cs329.TestUtils;

@DisplayName("Tests for ControlFlowBuilder")
public class ControlFlowBuilderTests {
  ControlFlowGraphBuilder unitUnderTest = null;
  ControlFlowGraph controlFlowGraph = null;
  StatementTracker statementTracker = null;

  @BeforeEach
  void beforeEach() {
    unitUnderTest = new ControlFlowGraphBuilder();
  }

  void init(String fileName) {
    ASTNode node = TestUtils.getASTNodeFor(this, fileName);
    List<ControlFlowGraph> cfgList = unitUnderTest.build(node);
    assertEquals(1, cfgList.size());
    controlFlowGraph = cfgList.get(0);
    statementTracker = new StatementTracker(node);
  }

  @Test
  @Tag("MethodDeclaration")
  @DisplayName("Should set start and end same when empty method declaration")
  void should_SetStartAndEndSame_when_EmptyMethodDeclaration() {
    String fileName = "cfgInputs/should_SetStartAndEndSame_when_EmptyMethodDeclaration.java";
    init(fileName);
    assertAll("Method declaration with empty block",
        () -> assertNotNull(controlFlowGraph.getMethodDeclaration()),
        () -> assertEquals(controlFlowGraph.getStart(), controlFlowGraph.getEnd())
    );
  }

  @Test
  @Tag("MethodDeclaration")
  @DisplayName("Should set start to first statement and end different when non-empty method declaration")
  void should_SetStartToFirstStatementAndEndDifferent_when_NonEmptyMethodDeclaration() {
    String fileName = "cfgInputs/should_SetStartToFirstStatementAndEndDifferent_when_NonEmptyMethodDeclaration.java";
    init(fileName);
    Statement start = controlFlowGraph.getStart();
    Statement end = controlFlowGraph.getEnd();
    Statement variableDeclStatement = statementTracker.getVariableDeclarationStatement(0);
    assertAll("Method declaration with non-empty block",
        () -> assertNotNull(controlFlowGraph.getMethodDeclaration()), 
        () -> assertNotEquals(start, end),
        () -> assertTrue(start == variableDeclStatement),
        () -> assertTrue(hasEdge(variableDeclStatement, end))
    );    
  }

  @Test
  @Tag("Block")
  @DisplayName("Should link all when block has no return")
  void should_LinkAll_when_BlockHasNoReturn() {
    String fileName = "cfgInputs/should_LinkAll_when_BlockHasNoReturn.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    assertTrue(hasEdge(variableDeclaration, expressionStatement));
  }

  @Test
  @Tag("Block")
  @DisplayName("Should link to return when block has return") 
  void should_LinkToReturn_when_BlockHasReturn() {
    String fileName = "cfgInputs/should_LinkToReturn_when_BlockHasReturn.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    assertAll(
        () -> assertTrue(hasEdge(variableDeclaration, returnStatement)),
        () -> assertFalse(hasEdge(returnStatement, expressionStatement))
    );
  }

  @Test
  @Tag("ReturnStatement")
  @DisplayName("No Return Value in List When no Return Statement")
  void returnNullTest(){
    String fileName = "cfgInputs/returnNullTest.java";
    init(fileName);
    assertTrue(statementTracker.returnList.isEmpty());
  }
  @Test
  @Tag("ReturnStatement")
  @DisplayName("Return Value in List When Return Statement is Present")
  void returnTestAddSuccess(){
    String fileName = "cfgInputs/returnTestAddSuccess.java";
    init(fileName);
    Statement end = controlFlowGraph.getEnd();
    assertTrue(hasEdge(statementTracker.getReturnStatement(0), end));
  }
  @Test
  @Tag("ReturnStatement")
  @DisplayName("Return Statement Present even if File Won't Build")
  void returnFailTest(){
    String fileName = "cfgInputs/returnFailTest.java";
    init(fileName);
    Statement end = controlFlowGraph.getEnd();
    assertTrue(hasEdge(statementTracker.getReturnStatement(0), end));
  }

  @Test
  @Tag("WhileStatement")
  @DisplayName("No While Value in List When no While Statement")
  void whileNullTest(){
    String fileName = "cfgInputs/whileNullTest.java";
    init(fileName);
    assertTrue(statementTracker.whileList.isEmpty());
  }
  @Test
  @Tag("WhileStatement")
  @DisplayName("While Test Not Block")
  void whileNotBlockTest(){
    String fileName = "cfgInputs/whileNotBlockTest.java";
    String errorMessage = "";
    String expectedMessage = "class org.eclipse.jdt.core.dom.EmptyStatement cannot be cast to class org.eclipse.jdt.core.dom.Block (org.eclipse.jdt.core.dom.EmptyStatement and org.eclipse.jdt.core.dom.Block are in unnamed module of loader 'app')";
    try{
      init(fileName);
    }
    catch(Exception e){
      errorMessage = e.getMessage();
    }
    assertEquals(expectedMessage, errorMessage);
  }
  @Test
  @Tag("WhileStatement")
  @DisplayName("While Test Success")
  void whileSuccessTest(){
    String fileName = "cfgInputs/whileSuccessTest.java";
    init(fileName);
    Statement source = statementTracker.getWhileStatement(0);
    Statement dest = statementTracker.getReturnStatement(0);
    assertTrue(hasEdge(source, dest));
  }

  @Test
  @Tag("IfStatement")
  @DisplayName("If Test Null")
  void ifNullTest(){
    String fileName = "cfgInputs/ifNullTest.java";
    init(fileName);
    assertTrue(statementTracker.ifList.isEmpty());
  }
  @Test
  @Tag("IfStatement")
  @DisplayName("If Test Then Not Block")
  void ifThenNotBlockTest(){
    String fileName = "cfgInputs/ifThenNotBlockTest.java";
    String errorMessage = "";
    String expectedMessage = "class org.eclipse.jdt.core.dom.EmptyStatement cannot be cast to class org.eclipse.jdt.core.dom.Block (org.eclipse.jdt.core.dom.EmptyStatement and org.eclipse.jdt.core.dom.Block are in unnamed module of loader 'app')";
    try{
      init(fileName);
    }
    catch(Exception e){
      errorMessage = e.getMessage();
    }
    assertEquals(expectedMessage, errorMessage);
  }
  @Test
  @Tag("IfStatement")
  @DisplayName("If Test Else Not Block")
  void ifElseNotBlockTest(){
    String fileName = "cfgInputs/ifElseNotBlockTest.java";
    String errorMessage = "";
    String expectedMessage = "class org.eclipse.jdt.core.dom.EmptyStatement cannot be cast to class org.eclipse.jdt.core.dom.Block (org.eclipse.jdt.core.dom.EmptyStatement and org.eclipse.jdt.core.dom.Block are in unnamed module of loader 'app')";
    try{
      init(fileName);
    }
    catch(Exception e){
      errorMessage = e.getMessage();
    }
    assertEquals(expectedMessage, errorMessage);
  }
  @Test
  @Tag("IfStatement")
  @DisplayName("If Test Success Without Else")
  void ifAddedSuccessTestNoElse(){
    String fileName = "cfgInputs/ifAddedSuccessTestNoElse.java";
    init(fileName);
    Statement source = statementTracker.getIfStatement(0);
    Statement expr = statementTracker.getExpressionStatement(0);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    assertTrue(hasEdge(source, expr));
    assertTrue(hasEdge(expr, returnStatement));

  }
  @Test
  @Tag("IfStatement")
  @DisplayName("If Test Success With Else")
  void ifAddedSuccessTestWithElse(){
    String fileName = "cfgInputs/ifAddedSuccessTestWithElse.java";
    init(fileName);
    Statement source = statementTracker.getIfStatement(0);
    Statement ifexpr = statementTracker.getExpressionStatement(0);
    Statement elseexpr = statementTracker.getExpressionStatement(1);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    assertTrue(hasEdge(source, ifexpr));
    assertTrue(hasEdge(source, elseexpr));
    assertTrue(hasEdge(ifexpr, returnStatement));
    assertTrue(hasEdge(elseexpr, returnStatement));
  }

  private boolean hasEdge(Statement source, Statement dest) {
    Set<Statement> successors = controlFlowGraph.getSuccs(source);
    Set<Statement> predecessors = controlFlowGraph.getPreds(dest);
    return successors != null && successors.contains(dest) 
        && predecessors != null && predecessors.contains(source);
  }
}
