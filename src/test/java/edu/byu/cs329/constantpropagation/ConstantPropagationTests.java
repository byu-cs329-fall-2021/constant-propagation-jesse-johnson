package edu.byu.cs329.constantpropagation;

import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

@DisplayName("Tests for ConstantPropagation")
public class ConstantPropagationTests {
  @Test
  void noFoldingNoRD(){
    String rootName = "propagationInputs/no_fold_no_rd_test-root.java";
    String expectedName = "propagationInputs/no_fold_no_rd_test.java";
    ASTNode expected = TestUtils.getASTNodeFor(this, expectedName);
    ASTNode root = TestUtils.getASTNodeFor(this, rootName);
    ASTNode completed = ConstantPropagation.propagate(root);
    assertTrue(expected.subtreeMatch(new ASTMatcher(), completed));
  }
  @Test
  void yesFoldingYesRD(){
    String rootName = "propagationInputs/yes_fold_yes_rd_test-root.java";
    String expectedName = "propagationInputs/yes_fold_yes_rd_test.java";
    ASTNode expected = TestUtils.getASTNodeFor(this, expectedName);
    ASTNode root = TestUtils.getASTNodeFor(this, rootName);
    ASTNode completed = ConstantPropagation.propagate(root);
    assertTrue(expected.subtreeMatch(new ASTMatcher(), completed));
  }
  @Test
  void worksTwice(){
    String rootName = "propagationInputs/works_twice_test-root.java";
    String expectedName = "propagationInputs/works_twice_test.java";
    ASTNode expected = TestUtils.getASTNodeFor(this, expectedName);
    ASTNode root = TestUtils.getASTNodeFor(this, rootName);
    ASTNode completed = ConstantPropagation.propagate(root);
    assertTrue(expected.subtreeMatch(new ASTMatcher(), completed));
  }
  @Test
  void worksFiveTimes(){
    String rootName = "propagationInputs/works_five_times_test-root.java";
    String expectedName = "propagationInputs/works_five_times_test.java";
    ASTNode expected = TestUtils.getASTNodeFor(this, expectedName);
    ASTNode root = TestUtils.getASTNodeFor(this, rootName);
    ASTNode completed = ConstantPropagation.propagate(root);
    assertTrue(expected.subtreeMatch(new ASTMatcher(), completed));
  }
}
