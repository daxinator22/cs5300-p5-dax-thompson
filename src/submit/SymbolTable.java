package submit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    symbol.setOffset(this.size);
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

  public String toString(){
    StringBuilder s = new StringBuilder();

    s.append("SYMBOL TABLE\n");
    s.append("____________________\n");

    for(String key : table.keySet()){
      s.append(String.format("%s\n", table.get(key).toString()));
    }

    return s.toString();
  }

  public int getSize(){return size;}

  public SymbolTable getParent() {
    return parent;
  }

}
