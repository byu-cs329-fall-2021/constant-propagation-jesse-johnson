package edu.byu.cs329.constantpropagation;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.cfg.ControlFlowGraphBuilder;
import edu.byu.cs329.constantfolding.ConstantFolding;
import edu.byu.cs329.utils.JavaSourceUtils;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
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
    ASTNode prevNode = node;
    ASTNode currNode = node;
    do{
      currNode = ConstantFolding.fold(currNode);
      ControlFlowGraphBuilder builder = new ControlFlowGraphBuilder();
      List<ControlFlowGraph> cfgList = builder.build(currNode);
      ControlFlowGraph cfg = cfgList.get(0);
      
      
    } while(prevNode.subtreeMatch(new ASTMatcher(), currNode) == false);
    return node;
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
