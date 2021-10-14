package edu.byu.cs329.constantfolding;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;

public class InfixExpressionFolding implements Folding{

  static final Logger log = LoggerFactory.getLogger(InfixExpressionFolding.class);
  
  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    private boolean isNumberLiteral(ASTNode exp) {
      return (exp instanceof NumberLiteral);
    }
    
    @Override
    public void endVisit(InfixExpression node) {
      Expression left = node.getLeftOperand();
      Expression right = node.getRightOperand();
      if (!isNumberLiteral(left)) return;
      if (!isNumberLiteral(right)) return;
      InfixExpression.Operator op = node.getOperator();
      AST ast = node.getAST();
      Expression newExp = null;
      if (op == InfixExpression.Operator.PLUS){ 
        @SuppressWarnings("unchecked")
        List<Expression> extOperands = node.extendedOperands();
        for (ASTNode i : extOperands){
          if (!isNumberLiteral(i)) return;
        }
        int comb = 0;
        int leftint = Integer.parseInt(((NumberLiteral) left).getToken());
        int rightint = Integer.parseInt(((NumberLiteral) right).getToken());
        comb = leftint + rightint;

        for (ASTNode i : extOperands){
            int tempint = Integer.parseInt(((NumberLiteral) i).getToken());
            comb += tempint;
        }
        String comblit = Integer.toString(comb);
        newExp = ast.newNumberLiteral(comblit);
      }
      else if (op == InfixExpression.Operator.LESS){
        int leftint = Integer.decode(((NumberLiteral) left).getToken());
        int rightint = Integer.decode(((NumberLiteral) right).getToken());
        Boolean bool = (leftint < rightint);
        newExp = ast.newBooleanLiteral(bool);
      }
      TreeModificationUtils.replaceChildInParent(node, newExp);
      didFold = true;
    }
  }

  /**
   * Combines operands that are on either side of the '+' operator, or if it instead contains
   * the '<' operator it replaces them with the boolean value.
   * 
   * <p>Visits the root and all reachable nodes from the root to replace any number literals
   * with '+' in between them with the values added together. In the scenario where the operator
   * is instead '<' it will change the operands to the proper boolean value.
   * 
   * <p>top := all nodes reachable from root such that each node 
   *           is a set of two or more number literals with '+' between each,
   *           or exactly two with '<' between them
   * 
   * <p>parents := all nodes such that each one is the parent
   *               of some node in top
   * 
   * <p>isFoldable(n) :=    isNumberLiteral(n)
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
   * @return true if number literals were replaced in the rooted tree
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
