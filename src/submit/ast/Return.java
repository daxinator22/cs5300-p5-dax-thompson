/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Return implements Statement {

  private final Expression expr;

  public Return(Expression expr) {
    this.expr = expr;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix);
    if (expr == null) {
      builder.append("return;\n");
    } else {
      builder.append("return ");
      expr.toCminus(builder, "");
      builder.append(";\n");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    //Get value of expression
    MIPSResult result = expr.toMIPS(code, data, symbolTable, regAllocator);

    //Find return value in symbol table
    SymbolInfo returnValue = symbolTable.find("return");

    //Store return value on the stack
    code.append(String.format("# Storing return value to stack at %s offset\n", returnValue.getOffset()));
    code.append(String.format("sw %s %s($sp)\n", result.getRegister(), returnValue.getOffset()));
    code.append("\n");

    return MIPSResult.createVoidResult();
  }

}
