package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

public class PrefixExpressionFoldingTests {
  PrefixExpressionFolding folderUnderTest = null;

  @BeforeEach
  void beforeEach() {
    folderUnderTest = new PrefixExpressionFolding();
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
  @DisplayName("Should not fold anything when there are no boolean literals")
  void should_NotFoldAnything_when_ThereAreNoBooleanLiterals() {
    String rootName = "foldingInputs/booleanLiterals/should_NotFoldAnything_when_ThereAreNoBooleanLiterals.java";
    String expectedName = "foldingInputs/booleanLiterals/should_NotFoldAnything_when_ThereAreNoBooleanLiterals.java";
    TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("inverts true properly")
  void inverts_true_properly(){
    String rootName = "foldingInputs/booleanLiterals/inverts_true_properly-root.java";
    String expectedName = "foldingInputs/booleanLiterals/inverts_true_properly.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
  @Test
  @DisplayName("inverts false properly")
  void inverts_false_properly(){
    String rootName = "foldingInputs/booleanLiterals/inverts_false_properly-root.java";
    String expectedName = "foldingInputs/booleanLiterals/inverts_false_properly.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
}
