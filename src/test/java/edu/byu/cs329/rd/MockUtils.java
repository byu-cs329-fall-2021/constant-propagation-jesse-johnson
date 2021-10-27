package edu.byu.cs329.rd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.byu.cs329.cfg.ControlFlowGraph;

public class MockUtils {
  public static ControlFlowGraph newMockForEmptyMethodWithTwoParameters(String first, String second) {
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
    VariableDeclaration firstParameter = newMockForVariableDeclaration(first);
    VariableDeclaration secondParameter = newMockForVariableDeclaration(second);
    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
    return cfg;
  }

  public static ControlFlowGraph newMockForTwoIdenticalParameters(String parameter){
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);

    Block block = newMockForBlock(3);
    MethodDeclaration methodDeclarion = newMockForMethodDeclaration(block);
    VariableDeclaration firstParameter = newMockForVariableDeclaration(parameter);
    VariableDeclaration secondParameter = newMockForVariableDeclaration(parameter);

    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
    return cfg;
  }

  public static ControlFlowGraph newMockForContainingIf(String name){
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    Block block = newMockForBlockIf();
    MethodDeclaration methodDeclarion = newMockForMethodDeclaration(block);

    VariableDeclaration firstParameter = newMockForVariableDeclaration("First");
    VariableDeclaration secondParameter = newMockForVariableDeclaration("Second");

    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

    return cfg;
  }

  public static ControlFlowGraph newMockForContainingWhile(String name){
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    Block block = newMockForBlockWhile(3);
    
    MethodDeclaration methodDeclarion = newMockForMethodDeclaration(block);

    
    VariableDeclaration firstParameter = newMockForVariableDeclaration("First");
    VariableDeclaration secondParameter = newMockForVariableDeclaration("Second");

    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

    return cfg;
  }

  public static VariableDeclaration newMockForVariableDeclaration(String name) {
    VariableDeclaration declaration = mock(VariableDeclaration.class);
    SimpleName simpleName = mock(SimpleName.class);
    when(simpleName.getIdentifier()).thenReturn(name);
    when(declaration.getName()).thenReturn(simpleName);
    return declaration;
  }

  public static MethodDeclaration newMockForMethodDeclaration(Block block){
    MethodDeclaration declaration = mock(MethodDeclaration.class);
    declaration.setBody(block);
    return declaration;
  }

  public static Block newMockForBlock(int num){
    Block declaration = mock(Block.class);
    @SuppressWarnings("unchecked")
    List<Statement> statements = declaration.statements();
    for (int i = 0; i < num; i++){
      Statement statement = mock(Statement.class);
      statements.add(statement);
    }

    return declaration;
  }
  public static Block newMockForBlockIf(){
    Block declaration = mock(Block.class);
    IfStatement ifstmt = mock(IfStatement.class);
    ifstmt.setThenStatement(mock(Statement.class));
    ifstmt.setElseStatement(mock(Statement.class));
    @SuppressWarnings("unchecked")
    List<Statement> statements = declaration.statements();
    statements.add(ifstmt);
    return declaration;
  }
  public static Block newMockForBlockWhile(int num){
    Block declaration = mock(Block.class);
    WhileStatement whileStatement = mock(WhileStatement.class);
    @SuppressWarnings("unchecked")
    List<Statement> statements = declaration.statements();
    statements.add(whileStatement);

    return declaration;
  }
}
