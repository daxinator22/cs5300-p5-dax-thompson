package submit;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import parser.CminusBaseVisitor;
import parser.CminusParser;
import submit.ast.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ASTVisitor extends CminusBaseVisitor<Node> {
    private final Logger LOGGER;
    private SymbolTable symbolTable;

    public ASTVisitor(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    public SymbolTable getSymbolTable(){return this.symbolTable;}

    private VarType getVarType(CminusParser.TypeSpecifierContext ctx) {
        final String t = ctx.getText();
        return (t.equals("int")) ? VarType.INT : (t.equals("bool")) ? VarType.BOOL : VarType.CHAR;
    }

    private VarType getFunType(CminusParser.TypeSpecifierContext ctx) {
        final String t = ctx.getText();

        if(t.equals("void")){
            return null;
        }
        return getVarType(ctx);
    }

    @Override public Node visitProgram(CminusParser.ProgramContext ctx) {
        symbolTable = new SymbolTable();
        List<Declaration> decls = new ArrayList<>();
//        decls.add((Declaration) visitDeclaration(ctx.declaration(0)));
        for (CminusParser.DeclarationContext d : ctx.declaration()) {
            decls.add((Declaration) visitDeclaration(d));
            System.out.println(d.getText());
        }
        return new Program(decls);
    }

    @Override public Node visitVarDeclaration(CminusParser.VarDeclarationContext ctx) {
        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
            LOGGER.fine("Var ID: " + id);
        }
//        return null;

        VarType type = getVarType(ctx.typeSpecifier());
        List<String> ids = new ArrayList<>();
        List<Integer> arraySizes = new ArrayList<>();
        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
            ids.add(id);
            symbolTable.addSymbol(id, new SymbolInfo(id, type, false));
            if (v.NUMCONST() != null) {
                arraySizes.add(Integer.parseInt(v.NUMCONST().getText()));
            } else {
                arraySizes.add(-1);
            }
        }
        final boolean isStatic = false;
        Node var = new VarDeclaration(type, ids, arraySizes, isStatic);
        StringBuilder test = new StringBuilder();
        var.toCminus(test, "");

        LOGGER.fine(String.format("New varDeclaration: %s", test.toString()));

        return var;
    }

    @Override public Node visitReturnStmt(CminusParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            return new Return((Expression) visitExpression(ctx.expression()));
        }
        return new Return(null);
    }

    @Override public Node visitConstant(CminusParser.ConstantContext ctx) {
        final Node node;
        if (ctx.NUMCONST() != null) {
            node = new NumConstant(Integer.parseInt(ctx.NUMCONST().getText()));
        } else if (ctx.CHARCONST() != null) {
            node = new CharConstant(ctx.CHARCONST().getText().charAt(0));
        } else if (ctx.STRINGCONST() != null) {
            node = new StringConstant(ctx.STRINGCONST().getText());
        } else {
            node = new BoolConstant(ctx.getText().equals("true"));
        }
        return node;
    }

    // TODO Uncomment and implement whatever methods make sense
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitDeclaration(CminusParser.DeclarationContext ctx) {
        if(ctx.getText().contains("(")){
            return visitFunDeclaration(ctx.funDeclaration());
        }
//        LOGGER.fine(ctx.varDeclaration().toString());
        return visitVarDeclaration(ctx.varDeclaration());
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitVarDeclId(CminusParser.VarDeclIdContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitFunDeclaration(CminusParser.FunDeclarationContext ctx) {
        symbolTable.addSymbol(ctx.ID().toString(), new SymbolInfo(ctx.ID().toString(), this.getFunType(ctx.typeSpecifier()), true));

        //Finds the parameters
        ArrayList<Param> params = new ArrayList<>();
        List<CminusParser.ParamContext> parent = ctx.param();
        for(CminusParser.ParamContext c : parent){
            params.add((Param) visitParam(c));
        }

        return new FunDeclaration(getFunType(ctx.typeSpecifier()), ctx.ID().toString(), params);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitTypeSpecifier(CminusParser.TypeSpecifierContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitParam(CminusParser.ParamContext ctx) {

        return new Param(getVarType(ctx.typeSpecifier()), ctx.paramId().ID().toString(), null);
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitParamId(CminusParser.ParamIdContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitStatement(CminusParser.StatementContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitCompoundStmt(CminusParser.CompoundStmtContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitExpressionStmt(CminusParser.ExpressionStmtContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitIfStmt(CminusParser.IfStmtContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitWhileStmt(CminusParser.WhileStmtContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitBreakStmt(CminusParser.BreakStmtContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitExpression(CminusParser.ExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitSimpleExpression(CminusParser.SimpleExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitOrExpression(CminusParser.OrExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitAndExpression(CminusParser.AndExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitUnaryRelExpression(CminusParser.UnaryRelExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitRelExpression(CminusParser.RelExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitRelop(CminusParser.RelopContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitSumExpression(CminusParser.SumExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitSumop(CminusParser.SumopContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitTermExpression(CminusParser.TermExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitMulop(CminusParser.MulopContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitUnaryExpression(CminusParser.UnaryExpressionContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitUnaryop(CminusParser.UnaryopContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitFactor(CminusParser.FactorContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitMutable(CminusParser.MutableContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitImmutable(CminusParser.ImmutableContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitCall(CminusParser.CallContext ctx) { return visitChildren(ctx); }

}
