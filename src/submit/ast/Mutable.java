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
public class Mutable implements Node {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id);
    if (index != null) {
      builder.append("[");
      index.toCminus(builder, prefix);
      builder.append("]");
    }
  }

  public String getId(){return this.id;}

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String register = regAllocator.getT();
    SymbolInfo symbol = symbolTable.find(this.id);

    code.append(String.format("# Loading value of %s from stack at offset %s\n", this.id, symbol.getOffset()));
    code.append(String.format("lw %s %s($sp)\n", register, symbol.getOffset()));

    return MIPSResult.createRegisterResult(register, symbol.getType());
  }

}
