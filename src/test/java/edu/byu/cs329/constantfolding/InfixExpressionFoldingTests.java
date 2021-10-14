package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

public class InfixExpressionFoldingTests {
  InfixExpressionFolding folderUnderTest = null;

  @BeforeEach
  void beforeEach() {
    folderUnderTest = new InfixExpressionFolding();
  }
  
  @Test
  @DisplayName("Should throw RuntimeException when root is null")
  void should_ThrowRuntimeException_when_RootIsNull() {
    assertThrows(RuntimeException.class, () -> {
      folderUnderTest.fold(null);
    });
  }
  @Test
  @DisplayName("Should throw RuntimeException when root is not a CompilationUnit and has no parent")
  void should_ThrowRuntimeException_when_RootIsNotACompilationUnitAndHasNoParent() {
    assertThrows(RuntimeException.class, () -> {
      URI uri = TestUtils.getUri(this, "");
      ASTNode compilationUnit = TestUtils.getCompilationUnit(uri);
      ASTNode root = compilationUnit.getAST().newNullLiteral();
      folderUnderTest.fold(root);
    });
  }
  @Test
  @DisplayName("Should not fold anything when there are not two or more Number literals")
  void should_NotFoldAnything_when_ThereAreNoNumberLiterals() {
    String rootName = "foldingInputs/numberLiterals/should_NotFoldAnything_when_ThereAreNoNumberLiterals.java";
    String expectedName = "foldingInputs/numberLiterals/should_NotFoldAnything_when_ThereAreNoNumberLiterals.java";
    TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("First test for two numbers being added together")
  void twoNumbers1(){
    String rootName = "foldingInputs/numberLiterals/adds_two_numbers_test1-root.java";
    String expectedName = "foldingInputs/numberLiterals/adds_two_numbers_test1.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Second test for two numbers being added together")
  void twoNumbers2(){
    String rootName = "foldingInputs/numberLiterals/adds_two_numbers_test2-root.java";
    String expectedName = "foldingInputs/numberLiterals/adds_two_numbers_test2.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Test for three numbers being added together")
  void threeNumbers(){
    String rootName = "foldingInputs/numberLiterals/adds_three_numbers_test-root.java";
    String expectedName = "foldingInputs/numberLiterals/adds_three_numbers_test.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Test for ten numbers being added together")
  void tenNumbers(){
    String rootName = "foldingInputs/numberLiterals/adds_ten_numbers_test-root.java";
    String expectedName = "foldingInputs/numberLiterals/adds_ten_numbers_test.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Test for right being greater than left")
  void rightGreater(){
    String rootName = "foldingInputs/binaryRelational/right_greater_test-root.java";
    String expectedName = "foldingInputs/binaryRelational/right_greater_test.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Test for left being greater than right")
  void leftGreater(){
    String rootName = "foldingInputs/binaryRelational/left_greater_test-root.java";
    String expectedName = "foldingInputs/binaryRelational/left_greater_test.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Test for right being equal to left")
  void bothEqual(){
    String rootName = "foldingInputs/binaryRelational/both_equal_test-root.java";
    String expectedName = "foldingInputs/binaryRelational/both_equal_test.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
}
