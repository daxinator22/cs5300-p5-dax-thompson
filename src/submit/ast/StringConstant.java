/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class StringConstant implements Node {

  private final String value;

  public StringConstant(String value) {
    this.value = value;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append("\"").append(value).append("\"");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String uniqueLabel = regAllocator.getUniqueLabel();

    data.append(String.format("%s: .asciiz ", uniqueLabel));
    data.append(String.format("%s\n", this.value));

    String register = regAllocator.getT();
    code.append(String.format("# Loading string into register\n"));
    code.append(String.format("la %s %s\n", register, uniqueLabel));
    code.append("\n");

    return MIPSResult.createRegisterResult(register, VarType.CHAR);
  }

}
