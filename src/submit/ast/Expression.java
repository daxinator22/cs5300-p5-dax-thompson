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

        if(mutable == null && operator == null){

            //In this case, there is only a SimpleExpression
            this.expr.toCminus(builder, prefix);
        }
        else {
            //Appending mutable and operator
            this.mutable.toCminus(builder, prefix);

            //Appends expression
            if(this.expr != null){

                //If there is a mutable, then there is no need for a prefix
                this.expr.toCminus(builder, "");
            }
        }
    }
}
