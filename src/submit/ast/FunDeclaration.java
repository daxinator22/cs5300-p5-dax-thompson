package submit.ast;

import java.util.List;

public class FunDeclaration implements Declaration, Node {

    private VarType type;
    private String id;
    private List<Param> params;
//    private Statement stmt;

    public FunDeclaration(VarType type, String id, List<Param> params){
        this.type = type;
        this.id = id;
        this.params = params;
//        this.stmt = stmt;
    }
    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        if(type == null){
            builder.append("void ");
        }
        else{
            builder.append(type.toString());
        }

        builder.append(String.format(" %s(", this.id));     //Appends type and id

        for(Param p : this.params){
            p.toCminus(builder, "");
        }

        builder.append(")\n{\n}\n");

    }
}
