public class Token {

    private String name;
    private String attribute;

    public Token(String nam, String att){
        this.name=nam;
        this.attribute=att;
    }

    public Token(String n){
        this.name=n;
    }

    public String getName(){
        return this.name;
    }

    public String getAttribute(){
        return this.attribute;
    }

    public void setName(String n){
        this.name=n;
    }

    public void setAttribute(String att){
        this.attribute=att;
    }

    public String toString(){
        return this.attribute==null? this.name : "<"+this.name+", \""+this.attribute+"\">";
    }
}
