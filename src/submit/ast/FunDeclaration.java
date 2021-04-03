package submit.ast;

import java.util.List;

public class FunDeclaration implements Declaration, Node {

    private FunType type;
    private String id;
    private List<VarDeclaration> params;
    private Statement stmt;

    public FunDeclaration(FunType type, String id, List<VarDeclaration> params, Statement stmt){
        this.type = type;
        this.id = id;
        this.params = params;
        this.stmt = stmt;
    }
    @Override
    public void toCminus(StringBuilder builder, String prefix) {

    }
}
