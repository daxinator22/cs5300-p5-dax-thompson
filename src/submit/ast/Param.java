package submit.ast;

public class Param implements Declaration, Node{

    private VarType type;
    private String id;
    private Integer size;

    public Param(VarType type, String id, Integer size){
        this.type = type;
        this.id = id;
        this.size = size;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        String arrayFomrat = "";

        try{
            if(size.intValue() == 0){
                arrayFomrat = "[]";
            }
            else{
                arrayFomrat = String.format("[%s]", size.intValue());
            }
        } catch (NullPointerException e){

        }

        builder.append(String.format("%s %s%s", type.toString(), id, arrayFomrat));
    }
}
