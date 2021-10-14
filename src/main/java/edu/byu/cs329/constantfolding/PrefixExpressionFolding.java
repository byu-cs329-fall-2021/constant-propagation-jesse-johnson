package edu.byu.cs329.constantfolding;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;

public class PrefixExpressionFolding implements Folding {

  static final Logger log = LoggerFactory.getLogger(PrefixExpressionFolding.class);
  
  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    private boolean isBooleanLiteral(ASTNode exp) {
      return (exp instanceof BooleanLiteral);
    }
    
    @Override
    public void endVisit(PrefixExpression node) {
      ASTNode exp = node.getOperand();
      if (!isBooleanLiteral(exp)) {
        return;
      }
      AST ast = node.getAST();
      PrefixExpression.Operator op = node.getOperator();
      boolean bool = ((BooleanLiteral)exp).booleanValue();
      if (op.toString() == "!") {
          if (bool == false){
            ((BooleanLiteral) exp).setBooleanValue(true);
          }
          else ((BooleanLiteral) exp).setBooleanValue(false);
      }
      else return;
      
      ASTNode newExp = ASTNode.copySubtree(ast, exp);
      TreeModificationUtils.replaceChildInParent(node, newExp);
      didFold = true;
    }
  }
  /**
   * Inverts boolean literals if preceeded by '!', or the not operator.
   * 
   * <p>Visits the root and all reachable nodes from the root to replace any boolean literal 
   * preceeded by the unary not operator.
   * 
   * <p>top := all nodes reachable from root such that each node 
   *           is a boolean literal preceeded by '!'
   * 
   * <p>parents := all nodes such that each one is the parent
   *               of some node in top
   * 
   * <p>isFoldable(n) :=    isBooleanLiteral(n)
   *                     /\ (   isLiteral(expression(n))
   *                         || isFoldable(expression(n)))
   * 
   * <p>literal(n) := if isLiteral(n) then n else literal(expression(n))
   * 
   * @modifies nodes in parents
   * 
   * @requires root != null
   * @requires (root instanceof CompilationUnit) \/ parent(root) != null
   * 
   * @ensures fold(root) == (old(top) != emptyset)
   * @ensures forall n in old(top), exists n' in nodes 
   *             fresh(n')
   *          /\ isLiteral(n')
   *          /\ value(n') == value(literal(n))
   *          /\ parent(n') == parent(n)
   *          /\ children(parent(n')) == (children(parent(n)) setminus {n}) union {n'}
   *   
   * @param root the root of the tree to traverse.
   * @return true if boolean literals were replaced in the rooted tree
   * 
   */
    @Override
    public boolean fold(ASTNode root) {
        checkRequires(root);
        Visitor visitor = new Visitor();
        root.accept(visitor);
        return visitor.didFold;
    }

    private void checkRequires(final ASTNode root) {
        ExceptionUtils.requiresNonNull(root, "Null root passed to PrefixExpressionFolding.fold");
    
        if (!(root instanceof CompilationUnit) && root.getParent() == null) {
          ExceptionUtils.throwRuntimeException(
              "Non-CompilationUnit root with no parent passed to PrefixExpressionFolding.fold");
        }
      }
    
}
