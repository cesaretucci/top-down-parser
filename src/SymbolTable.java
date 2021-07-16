import java.util.*;

public class SymbolTable {

    public HashMap<String, Token> table;
    protected SymbolTable prev;

    public SymbolTable(SymbolTable st){
        table=new HashMap<String, Token>();
        prev=st;
    }

    public SymbolTable(){
        table=new HashMap<String, Token>();
    }

    public boolean containsKey(String key){
        return this.table.containsKey(key);
    }

    public void put(String key, Token token){
        this.table.put(key,token);
    }

    public Token get(String key){
        Token ret=this.table.get(key);
        if (ret!=null) return ret;
        else return null;
    }
}
