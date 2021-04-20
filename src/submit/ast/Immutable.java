package submit.ast;

import javax.swing.text.html.ImageView;

public class Immutable extends AbstractNode{

    private Node part;

    public Immutable(Node part){
        this.part = part;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
//        builder.append(prefix);

        if(part instanceof Expression){
            builder.append("(");
            part.toCminus(builder, "");
            builder.append(")");
        }
        else{
            part.toCminus(builder, "");
        }
    }
}
