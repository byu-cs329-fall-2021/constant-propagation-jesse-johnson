package edu.byu.cs329.constantfolding;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;

public class IfStatementFolding implements Folding {
    static final Logger log = LoggerFactory.getLogger(InfixExpressionFolding.class);

    class Visitor extends ASTVisitor {
        public boolean didFold = false;

        private boolean isBooleanLiteral(ASTNode exp) {
            return (exp instanceof BooleanLiteral);
        }

        @Override
        public void endVisit(IfStatement node) {
            Expression exp = node.getExpression();
            AST ast = node.getAST();
            if (!isBooleanLiteral(exp))
                return;
            Boolean bool = ((BooleanLiteral) exp).booleanValue();
            ASTNode newNode = null;
            if (bool)
                newNode = (ASTNode) ((Block) node.getThenStatement()).statements().get(0);
            else {
                Statement elseStmt = node.getElseStatement();
                if (elseStmt == null) {
                    TreeModificationUtils.removeChildInParent(node);
                    didFold = true;
                    return;
                }
                else newNode = (ASTNode) ((Block) elseStmt).statements().get(0);
            }
            ASTNode newExp = ASTNode.copySubtree(ast, newNode);
            TreeModificationUtils.replaceChildInParent(node, newExp);
            didFold = true;
        }
    }

    /**
     * Folds if statements when what follows for the condition will always be the
     * same.
     * 
     * <p>
     * Visits the root and all reachable nodes from the root to replace any if
     * statements containing a boolean literal, if it is always true it removes the
     * if statement and if it is always false it removes that block entirely.
     * 
     * <p>
     * top := all nodes reachable from root such that each node is an if statement
     * 
     * <p>
     * parents := all nodes such that each one is the parent of some node in top
     * 
     * <p>
     * isFoldable(n) := isNumberLiteral(n) /\ ( isLiteral(expression(n)) ||
     * isFoldable(expression(n)))
     * 
     * <p>
     * literal(n) := if isLiteral(n) then n else literal(expression(n))
     * 
     * @modifies nodes in parents
     * 
     * @requires root != null
     * @requires (root instanceof CompilationUnit) \/ parent(root) != null
     * 
     * @ensures fold(root) == (old(top) != emptyset)
     * @ensures forall n in old(top), exists n' in nodes fresh(n') /\ isLiteral(n')
     *          /\ value(n') == value(literal(n)) /\ parent(n') == parent(n) /\
     *          children(parent(n')) == (children(parent(n)) setminus {n}) union
     *          {n'}
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
