package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

public class IfStatementFoldingTests {
    IfStatementFolding folderUnderTest = null;

  @BeforeEach
  void beforeEach() {
    folderUnderTest = new IfStatementFolding();
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
  @DisplayName("Should not fold anything when the conditional is not a bloolean literal")
  void should_NotFoldAnything_when_bad_conditional() {
    String rootName = "foldingInputs/ifStatements/should_NotFoldAnything_when_bad_conditional.java";
    String expectedName = "foldingInputs/ifStatements/should_NotFoldAnything_when_bad_conditional.java";
    TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Folds when statement is true")
  void testTrue() {
    String rootName = "foldingInputs/ifStatements/testTrue-root.java";
    String expectedName = "foldingInputs/ifStatements/testTrue.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Folds when statement is false and has an else")
  void testFalse() {
    String rootName = "foldingInputs/ifStatements/testFalse-root.java";
    String expectedName = "foldingInputs/ifStatements/testFalse.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("Folds when statement is false and has no else")
  void testFalseFail() {
    String rootName = "foldingInputs/ifStatements/testFalseFail-root.java";
    String expectedName = "foldingInputs/ifStatements/testFalseFail.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
}