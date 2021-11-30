package edu.byu.cs329.constantpropagation;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.cfg.ControlFlowGraphBuilder;
import edu.byu.cs329.constantfolding.ConstantFolding;
import edu.byu.cs329.rd.ReachingDefinitions;
import edu.byu.cs329.rd.ReachingDefinitionsBuilder;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;
import edu.byu.cs329.utils.JavaSourceUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant Propagation.
 * 
 * @author Eric Mercer
 */
public class ConstantPropagation {

  static final Logger log = LoggerFactory.getLogger(ConstantPropagation.class);
  /**
   * Performs constant propagation.
   * 
   * @param node the root node for constant propagation.
   */
  public static ASTNode propagate(ASTNode node) {
    ASTNode prevNode = ASTNode.copySubtree(node.getAST(), node);
    ASTNode currNode = ASTNode.copySubtree(node.getAST(), node);
    while(true){
      ConstantFolding.fold(currNode);
      ControlFlowGraphBuilder builder = new ControlFlowGraphBuilder();
      List<ControlFlowGraph> cfgList = builder.build(currNode);
      ControlFlowGraph cfg = cfgList.get(0);
      
      ReachingDefinitionsBuilder rdBuilder = new ReachingDefinitionsBuilder();
      List<ControlFlowGraph> list = new ArrayList<ControlFlowGraph>();
      list.add(cfg);
      List<ReachingDefinitions> reachingDefinitionsList = rdBuilder.build(list);
      ReachingDefinitions rd = reachingDefinitionsList.get(0);
      Statement start = cfg.getStart();
      if(start instanceof VariableDeclarationStatement){
        VariableDeclarationStatement varstmt = (VariableDeclarationStatement) start;
        @SuppressWarnings("unchecked")
        List<Object> frags = varstmt.fragments();
        String left = ((VariableDeclarationFragment) frags.get(0)).getName().toString();
        Expression right = ((VariableDeclarationFragment) frags.get(0)).getInitializer();
        for (Definition def : rd.getReachingDefinitions(start)){
          Expression defExp = ((ExpressionStatement) def.statement).getExpression();
          InfixExpression infExp = (InfixExpression) defExp;
          for(Object ob : infExp.extendedOperands()){
            if (ob.toString().equals(left)){
              ob = right;
            }
          }
        }
      }
      for (Statement st : cfg.getSuccs(start)){
        if(st instanceof ExpressionStatement){
          Expression exp = ((ExpressionStatement) st).getExpression();
          Expression left = ((InfixExpression) exp).getLeftOperand();
          Expression right = ((InfixExpression) exp).getRightOperand();
          for (Definition def : rd.getReachingDefinitions(start)){
            Expression defExp = ((ExpressionStatement) def.statement).getExpression();
            InfixExpression infExp = (InfixExpression) defExp;
            for(Object ob : infExp.extendedOperands()){
              if (ob.equals(left)){
                ob = right;
              }
            }
          }
        }
      }
    
      if(prevNode.subtreeMatch(new ASTMatcher(), currNode)){
        break;
      }
      else {
        prevNode = ASTNode.copySubtree(node.getAST(), currNode);
      }
    }
    return currNode;
  }

  /**
   * Performs constant folding an a Java file.
   * 
   * @param args args[0] is the file to fold and args[1] is where to write the
   *             output
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      log.error("Missing Java input file or output file on command line");
      System.out.println("usage: java DomViewer <java file to parse> <html file to write>");
      System.exit(1);
    }

    File inputFile = new File(args[0]);
    // String inputFileAsString = readFile(inputFile.toURI());
    ASTNode node = JavaSourceUtils.getCompilationUnit(inputFile.toURI());//parse(inputFileAsString);
    ConstantPropagation.propagate(node);

    try {
      PrintWriter writer = new PrintWriter(args[1], "UTF-8");
      writer.print(node.toString());
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
