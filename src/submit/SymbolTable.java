package submit;

import submit.ast.VarType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*
 * Code formatter project
 * CS 4481
 */
/**
 *
 */
public class SymbolTable {

  private final HashMap<String, SymbolInfo> table;
  private SymbolTable parent;
  private final List<SymbolTable> children;
  private int size;
  private int methodCalls;

  public SymbolTable() {
    table = new HashMap<>();
    parent = null;
    children = new ArrayList<>();
    this.size = 0;

    //Adding println to symbol table
    this.addSymbol("println", new SymbolInfo("println", null, true));
  }

  public void addSymbol(String id, SymbolInfo symbol) {
    table.put(id, symbol);

    //Functions take up no size on the stack
    if(!symbol.isFunction()) {
      this.size += 4;
    }

    symbol.setOffset(this.size * -1);
  }

  /**
   * Returns null if no symbol with that id is in this symbol table or an
   * ancestor table.
   *
   * @param id
   * @return
   */
  public SymbolInfo find(String id) {
    if (table.containsKey(id)) {
      return table.get(id);
    }
    if (parent != null) {
      return parent.find(id);
    }
    return null;
  }

  /**
   * Returns the new child.
   *
   * @return
   */
  public SymbolTable createChild() {
    SymbolTable child = new SymbolTable();
    children.add(child);
    child.parent = this;
    return child;
  }

  public int addNewMethodCall(VarType type){
    String id = String.format("methodCall%s", methodCalls);
    this.addSymbol(id, new SymbolInfo(id, type, false));

    return methodCalls++;
  }

  public String toString(){
    StringBuilder s = new StringBuilder();

    s.append("SYMBOL TABLE\n");
    s.append("____________________\n");

    for(String key : table.keySet()){
      s.append(String.format("%s\n", table.get(key).toString()));
    }

    return s.toString();
  }

  public Set<String> getKeys(){return this.table.keySet();}
  public int getSize(){return size;}
  public boolean contains(String id){return table.containsKey(id);}

  public SymbolTable getParent() {
    return parent;
  }

}
