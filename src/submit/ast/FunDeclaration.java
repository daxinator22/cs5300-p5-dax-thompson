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
        builder.append("\n");

        if(type == null){
            builder.append("void ");
        }
        else{
            builder.append(type.toString());
        }

        builder.append(String.format(" %s(", this.id));     //Appends type and id

        try {
            //Gets and removes first param
            Param first = params.get(0);
            first.toCminus(builder, "");
            params.remove(0);

            for (Param p : this.params) {
                builder.append(", ");
                p.toCminus(builder, "");
            }
        } catch(IndexOutOfBoundsException e){

        }

        builder.append(")\n{\n}\n");

    }
}
