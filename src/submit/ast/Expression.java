package submit.ast;

public class Expression implements Node{

    private Mutable mutable;
    private String operator;
    private Expression expr;

     public Expression(Mutable mutable, String operator, Expression expr){
         this.mutable = mutable;
         this.operator = operator;
         this.expr = expr;
     }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        //Appending mutable and operator
        this.mutable.toCminus(builder, prefix);
        builder.append(String.format(" %s ", this.operator));

        //Appends expression
        if(this.expr != null){

            //If there is a mutable, then there is no need for a prefix
            this.mutable.toCminus(builder, "");
        }
    }
}
