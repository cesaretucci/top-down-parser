/*
	Program to implement Recursive Descent Parser in Java
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
	www.pracspedia.com
	
	Grammar:
	E -> x + T
	T -> (E)
	T -> x
*/

import java.io.FileNotFoundException;
import java.util.*;

class RecDesParser {
    private int ptr;
    public ArrayList<String> input_tokens;
    private List<String> terminals;
    private List<String> non_terms;

    static final String[] NT = {"S", "P", "Stm", "S1", "E1", "Exp"};
    static final String[] T = {"ID", "IF", "THEN", "ELSE", "DO", "WHILE", "RELOP", "NUM", "ASSIGN", "EOF","IFEXPR"};

    static int[][] parse_table = {
            {1,1,0,0,1,0,0,0,0,0,0},
            {2,2,0,0,2,0,0,0,0,0,0},
            {6,5,0,0,7,0,0,0,0,0,0},
            {3,3,0,0,3,0,0,0,0,4,0},
            {9,0,0,0,0,0,0,10,0,0,0},
            {8,0,0,0,0,0,0,8,0,0,11}
    };

    private ArrayList<Production> prods;




    class Production{
        public String sx;
        public ArrayList<String> dx;

        public Production(String s, ArrayList<String> d){
            this.sx=s;
            this.dx=d;
        }


        public boolean matches(ArrayList<String> seq){
            return (this.dx).equals(seq);
        }

    }


    private void initialize(){
        this.ptr=0;
        prods = new ArrayList<Production>();
        input_tokens = new ArrayList<String>();

        ArrayList<String> temp = new ArrayList<String>();
        temp.add("P");
        temp.add("EOF");

        //1
        prods.add(new Production("S", temp));
        temp= new ArrayList<String>();

        temp.add("Stm");
        temp.add("ENDL");
        temp.add("S1");

        //2
        prods.add(new Production("P", temp));
        temp= new ArrayList<String>();

        temp.add("Stm");
        temp.add("ENDL");
        temp.add("S1");

        //3
        prods.add(new Production("S1", temp));
        temp= new ArrayList<String>();
        temp.add("EOF");
        //4
        prods.add(new Production("S1", temp));

        temp= new ArrayList<String>();
        temp.add("IF");
        temp.add("Exp");
        temp.add("THEN");
        temp.add("Stm");
        temp.add("ELSE");
        temp.add("Stm");

        //5
        prods.add(new Production("Stm", temp));
        temp= new ArrayList<String>();

        temp.add("ID");
        temp.add("ASSIGN");
        temp.add("Exp");

        //6
        prods.add(new Production("Stm", temp));
        temp= new ArrayList<String>();

        temp.add("DO");
        temp.add("Stm");
        temp.add("WHILE");
        temp.add("Exp");

        //7
        prods.add(new Production("Stm", temp));
        temp= new ArrayList<String>();

        temp.add("E1");
        temp.add("RELOP");
        temp.add("E1");

        //8
        prods.add(new Production("Exp", temp));
        temp= new ArrayList<String>();

        temp.add("ID");

        //9
        prods.add(new Production("E1", temp));
        temp= new ArrayList<String>();

        temp.add("NUM");
        //10
        prods.add(new Production("E1", temp));

        temp = new ArrayList<String>();
        temp.add("IFEXPR");
        temp.add("Exp");
        temp.add("THEN");
        temp.add("Exp");
        temp.add("ELSE");
        temp.add("Exp");
        //temp.add("ENDL");
        //11
        prods.add(new Production("Exp", temp));


        terminals = Arrays.asList(T);
        non_terms = Arrays.asList(NT);



    }


    public static void main(String args[]) {

        RecDesParser parser = new RecDesParser();
        parser.initialize();

        Lexer lexicalAnalyzer;
        try {
            lexicalAnalyzer = new Lexer(args[0]);
        } catch(Exception fe) {
            fe.printStackTrace();
            lexicalAnalyzer=null;
        }


        Token token;

        try {
            while (!((token = lexicalAnalyzer.nextToken()).getName().equals("EOF"))) {
                if(token ==null){
                    System.out.println("ERROR: Lexical analysis stopped (invalid token)");
                }
                else {
                    parser.input_tokens.add(token.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            parser.input_tokens.add("EOF");
            parser.input_tokens.add("$");
        }

        Iterator i = parser.input_tokens.iterator();
        while(i.hasNext())
            System.out.println(i.next());
        TreeNode root_node = new TreeNode("S");
        int err = parser.run(root_node);
        if(err<0)
            System.out.println("[Parser]: Syntax error, couldn't parse program.");
        else if(err>0) {
            System.out.println("[Parser]: Parsing completed successfully, syntax tree: \n\n");
            System.out.println(root_node.toString());

        }
    }

    public RecDesParser(){
        initialize();
    }


    private int run(TreeNode root){
        Iterator<String> input = input_tokens.iterator();
        String current_sym = input.next();
        int i;
        TreeNode curr_node = root;
        Iterator<String> prod;
        String temp;

        while( input.hasNext()){
            System.out.println("[Test]: current symbol:"+current_sym);
            System.out.println("[Test]: node data:"+curr_node.data);
            if(curr_node.data.equals(current_sym)){
                System.out.println("[Test]: match!");
                if(current_sym.equals("EOF"))
                    return 1;
                //match

                curr_node = curr_node.getNextBrother();
                System.out.println("[Test]: next brother: "+curr_node.data);
                temp = input.next();
                System.out.println("[Test]: next symbol: "+temp);
                current_sym = temp;


            } else if(non_terms.contains(curr_node.data)){
                //letto un non terminale
                i = parse_table[non_terms.indexOf(curr_node.data)][terminals.indexOf(current_sym)];
                System.out.println("[Test]: found production #"+i);
                if(i==0)
                    return -1;


                prod = prods.get(i-1).dx.iterator();
               // System.out.println("[Test]: "+prod.next());
                while(prod.hasNext()){
                    temp= new String(prod.next());
                    curr_node.addChild(temp);
                    System.out.println("[Test]: node childs: "+temp);
                }

                curr_node = curr_node.children.getFirst();
                //System.out.println("[Test]: "+curr_node.data);
                //System.out.println("[Test]: "+curr_node.toString());
            } else {
                System.out.println("[Error]: couldn't reach "+current_sym+" from "+curr_node.data);
                return -1; //errore
            }

        }
        return -1;
    }



/*
    static boolean E() {
        // Check if 'ptr' to 'ptr+2' is 'x + T'
        int fallback = ptr;
        if(input[ptr++] != 'x') {
            ptr = fallback;
            return false;
        }
        if(input[ptr++] != '+') {
            ptr = fallback;
            return false;
        }
        if(T() == false) {
            ptr = fallback;
            return false;
        }
        return true;
    }

    }*/
}