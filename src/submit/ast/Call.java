package submit.ast;

import java.util.List;

public class Call implements Node{

    private String id;
    private List<Expression> exprs;

    public Call(String id, List<Expression> exprs){
        this.id = id;
        this.exprs = exprs;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
//        builder.append(prefix);

        builder.append(id);
        builder.append("(");

        //Adds expressions as parameters
        try{
            Expression first = exprs.get(0);
            first.toCminus(builder, prefix);
            exprs.remove(0);

        }catch (IndexOutOfBoundsException e){

        }

        for(Expression e : this.exprs){
            builder.append(", ");
            e.toCminus(builder, prefix);
        }

        builder.append(")");
    }
}
