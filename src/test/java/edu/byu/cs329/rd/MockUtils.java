package edu.byu.cs329.rd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
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
    MethodDeclaration methodDeclarion = newMockForMethodDeclaration(List.of(block));
    VariableDeclaration firstParameter = newMockForVariableDeclaration(parameter);
    VariableDeclaration secondParameter = newMockForVariableDeclaration(parameter);

    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
    return cfg;
  }

  public static ControlFlowGraph newMockForContainingIf(int statementsInThen, int statementsInElse, int statementsBefore, int statementsAfter){
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    Block blockBefore = newMockForBlock(statementsBefore);
    Block blockAfter = newMockForBlock(statementsAfter);
    Block blockIf = mock(Block.class);
    Random rand = new Random();
    @SuppressWarnings("unchecked")
    List<Statement> statements = blockIf.statements();
    int randomInt = rand.nextInt() % 2;
    if (randomInt != 0){
      Statement randomVariableDeclaration = newMockForVariableDeclarationStatement(1);
      statements.add(randomVariableDeclaration);
    }
    IfStatement ifStatement = mock(IfStatement.class);
    Block thenAdd = mock(Block.class);
    @SuppressWarnings("unchecked")
    List<Statement> thenStatements = thenAdd.statements();
    for(int i = 0; i < statementsInThen; i++){
      Statement add;
      if (rand.nextInt() % 2 == 1 && statements.size() > 1){
        add = newMockForExpressionStatement();
      }
      else{
        add = newMockForVariableDeclarationStatement(rand.nextInt() % 4 + 1);
      }
      thenStatements.add(add);
    }
    ifStatement.setThenStatement(thenAdd);
    
    Block elseAdd = mock(Block.class);
    @SuppressWarnings("unchecked")
    List<Statement> elseStatements = elseAdd.statements();
    for(int i = 0; i < statementsInThen; i++){
      Statement add;
      if (rand.nextInt() % 2 == 1 && statements.size() > 1){
        add = newMockForExpressionStatement();
      }
      else{
        add = newMockForVariableDeclarationStatement(rand.nextInt() % 4 + 1);
      }
      elseStatements.add(add);
    }
    ifStatement.setThenStatement(elseAdd);
    statements.add(ifStatement);
    List<Block> blocks = List.of(blockBefore, blockIf, blockAfter);
    MethodDeclaration methodDeclarion = newMockForMethodDeclaration(blocks);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

    return cfg;
  }

  public static ControlFlowGraph newMockForContainingWhile(int statementsBefore, int statementsInside, int statementsAfter){
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    Block blockBefore = newMockForBlock(statementsBefore);
    Block blockAfter = newMockForBlock(statementsAfter);
    Block blockInside = mock(Block.class);
    WhileStatement whileStatement = mock(WhileStatement.class);
    Block whileBlock = mock(Block.class);
    Random rand = new Random();
    @SuppressWarnings("unchecked")
    List<Statement> statements = whileBlock.statements();
    for(int i = 0; i < statementsInside; i++){
      Statement add;
      if (rand.nextInt() % 2 == 1 && statements.size() > 1){
        add = newMockForExpressionStatement();
      }
      else{
        add = newMockForVariableDeclarationStatement(rand.nextInt() % 4 + 1);
      }
      statements.add(add);
    }
    whileStatement.setBody(whileBlock);
    whileStatement.setExpression(newMockForExpressionStatement().getExpression());
    @SuppressWarnings("unchecked")
    List<Statement> blockStatements = whileBlock.statements();
    blockStatements.add(whileStatement);
    List<Block> block = List.of(blockBefore, blockInside, blockAfter);
    
    MethodDeclaration methodDeclarion = newMockForMethodDeclaration(block);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
    return cfg;
  }

  public static VariableDeclaration newMockForVariableDeclaration(String name) {
    VariableDeclaration declaration = mock(VariableDeclaration.class);
    SimpleName simpleName1 = newMockForSimpleName();
    SimpleName simpleName2 = newMockForSimpleName();
    when(simpleName1.getIdentifier()).thenReturn(name);
    declaration.setInitializer(simpleName2);
    when(declaration.getName()).thenReturn(simpleName1);
    return declaration;
  }

  public static VariableDeclarationStatement newMockForVariableDeclarationStatement(int numStatements){
    VariableDeclarationStatement declaration = mock(VariableDeclarationStatement.class);
    List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
    for (int i = 0; i < numStatements; i++){
      SimpleName simpleName = newMockForSimpleName();
      VariableDeclaration variableDeclaration = newMockForVariableDeclaration(simpleName.getFullyQualifiedName());
      vars.add(variableDeclaration);
    }
    when(declaration.fragments()).thenReturn(vars);
    return declaration;
  }

  public static Assignment newMockForAssignment(Expression assignee,  Expression assigner){
    Assignment assignment = mock(Assignment.class);
    assignment.setLeftHandSide(assignee);
    assignment.setRightHandSide(assigner);
    return assignment;
  }

  public static ExpressionStatement newMockForExpressionStatement(){
    ExpressionStatement expr = mock(ExpressionStatement.class);
    SimpleName simpleName = newMockForSimpleName();
    expr.setExpression(simpleName);
    return expr;
  }

  public static MethodDeclaration newMockForMethodDeclaration(List<Block> blocks){
    MethodDeclaration declaration = mock(MethodDeclaration.class);
    Block block = mock(Block.class);
    @SuppressWarnings("unchecked")
    List<Statement> statements = block.statements();
    for (Block b : blocks){
      statements.add(b);
    }
    
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
  public static SimpleName newMockForSimpleName(){
    SimpleName name = mock(SimpleName.class);
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 6) {
        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
        salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    when(name.getIdentifier()).thenReturn(saltStr);
    return name;
  }
}
