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

    SymbolInfo symbol = null;
    int offset = 0;
    if(symbolTable.contains(this.id)) {
      symbol = symbolTable.find(this.id);
      offset = symbol.getOffset();
    }

    else{
      SymbolTable parent = symbolTable.getParent();
      while(parent != null){
        offset += parent.getSize();
        if(parent.contains(this.id)) {
          symbol = parent.find(this.id);
          offset += symbol.getOffset();
          break;
        }
        else{
          parent = parent.getParent();
        }
      }
    }

    code.append(String.format("# Loading value of %s from stack at offset %s\n", this.id, offset));
    code.append(String.format("lw %s %s($sp)\n", register, offset));

    return MIPSResult.createRegisterResult(register, symbol.getType());
  }

}
