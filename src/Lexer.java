
import java.io.*;

public class Lexer {

    private RandomAccessFile reader;
    public SymbolTable stringTable;  // la struttura dati potrebbe essere una hash map
    private int state;
    private long file_offset;
    private File input_file;


    public Lexer(String filePath)throws  IOException{

        // la symbol table in questo caso la chiamiamo stringTable
        stringTable = new SymbolTable();
        state = 0;
        file_offset=0;

        stringTable.put("ifexpr", new Token("IFEXPR"));
        stringTable.put("if", new Token("IF"));   // inserimento delle parole chiavi nella stringTable per evitare di scrivere un diagramma di transizione per ciascuna di esse (le parole chiavi verranno "catturate" dal diagramma di transizione e gestite e di conseguenza). IF poteva anche essere associato ad una costante numeric
        stringTable.put("then", new Token("THEN"));
        stringTable.put("else", new Token("ELSE"));
        stringTable.put("do", new Token("DO"));
        stringTable.put("while", new Token("WHILE"));


        input_file=new File(filePath);
        reader=new RandomAccessFile(input_file, "r");
    }



    public Token nextToken()throws Exception{
        reader.seek(file_offset);


        //Ad ogni chiamata del lexer (nextToken())
        //si resettano tutte le variabili utilizzate
        state = 0;
        String lessema = ""; //� il lessema riconosciuto
        char c='I';
        long limit=reader.length();


        while(file_offset<=limit) {

            try {
                c =(char) reader.read();
               // System.out.println("(Lexer) read char:"+c);
                file_offset++;
            } catch (EOFException e) {
                if (lessema != "") {   //in caso di eof restituisco il token, se stavo leggendo qualcosa
                    if (state == 3)
                        return installID(lessema);
                    if (state >= 4 && state <= 10)
                        return installNumber(lessema);
                    retrack();
                }
                else return new Token("EOF");
            }


            //main switch
            switch (state) {

                case 0:
                    switch (c) {
                        case ';':
                            state=0;
                            return new Token("ENDL");


                        case '<':
                            state = 1;
                            break;
                        case '>':
                            state = 2;
                            break;
                        case '=':
                            state = 0;
                            return new Token("RELOP", "EQ");
                        case '+':
                            state =0;
                            return new Token("RELOP", "PLUS");
                        case '-':
                            state=0;
                            return new Token("RELOP", "MINUS");


                        default: {
                            if (Character.isSpaceChar(c)||c=='\n'||c=='\r')
                                state = 0;
                            else if (Character.isLetter(c)) {
                                lessema += c;
                                state = 3;
                            } else if (Character.isDigit(c)) {
                                lessema += c;
                                state = 4;
                            } else {
                                state = 14;  //in classe special i caratteri speciali non riconosciuti
                            }
                        }
                    }
                    break;

                //operators
                case 1:

                    switch (c) {
                        case '-':
                            state = 12;
                            break;
                        case '=':
                            state = 0;
                            return new Token("RELOP", "LE");

                        case '>':
                            state = 0;
                            return new Token("RELOP", "NE");


                        default:
                            state = 0;
                            retrack();
                            return new Token("RELOP", "LT");

                    } break;

                case 2:
                    if (c == '=') {
                        state = 0;
                        return new Token("RELOP", "GE");
                    } else {
                        retrack();
                        return new Token("RELOP", "GT");
                    }


                    //id
                case 3:
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        state = 3;
                        lessema += c;
                        break;
                    } else {
                        state = 0;
                        retrack();
                        return installID(lessema);
                    }
                    //end  id


                    //unsigned number
                case 4:
                    if (Character.isDigit(c)) {
                        state = 4;
                        lessema += c;
                        break;
                    } else if (c == 'e' || c == 'E') {
                        lessema += c;
                        state = 7;
                        break;
                    } else if (c == '.') {
                        lessema += c;
                        state = 5;
                        break;
                    } else {
                        retrack();
                        return installNumber(lessema);
                    }


                //ho letto punto
                case 5:
                    if (Character.isDigit(c)) {
                        lessema += c;
                        state = 6;
                        break;
                    } else state = 11; //lancio errore

                    //leggendo dopo il punto
                case 6:
                    if (Character.isDigit(c)) {
                        lessema += c;
                        break;
                    } else if (c == 'e' || c == 'E') {
                        state = 7;
                        lessema += c;
                        break;
                    } else
                        state = 10;

                case 7: //ho letto "e"
                    if (c == '+' || c == '-') {
                        lessema += c;
                        state = 8;
                        break;
                    } else if (Character.isDigit(c)) {
                        lessema += c;
                        state = 9;
                        break;
                    } else state = 11;


                case 8: //ho letto + o - dopo e
                    if (Character.isDigit(c)) {
                        lessema += c;
                        state = 9;
                        break;
                    } else state = 11;

                case 9: //leggo digits finchè non ho qualcos'altro
                    if (Character.isDigit(c)) {
                        lessema += c;
                        state = 9;
                        break;
                    } else state = 10;

                case 10: //store and retrack
                    retrack();
                    return installNumber(lessema);

                case 11:
                    throw new Exception("Unexpected literal " + lessema + "'" + c + "' following a numerical token, at offset " + file_offset + ". ");

                case 12:
                    if(c=='-'){
                        state = 0;
                        return new Token("ASSIGN");
                    }
                    else state = 14;

                case 14:
                    return null;

                default:
                    break;


            } //end switch
        } //end while

        System.out.println("Reached EOF, lexical analysis stopped.");
        return new Token("EOF");

    }//end method


    private Token installID(String lessema){
        Token token;

        //utilizzo come chiave della hashmap il lessema
        if(stringTable.containsKey(lessema))
        return stringTable .get(lessema);
        else{
        token =  new Token("ID", lessema);
        stringTable.put(lessema, token);
        return token;
        }
    }

    private Token installNumber(String lessema){
        Token token;
        if(stringTable.containsKey(lessema))
            return stringTable .get(lessema);
        else{
            token =  new Token("NUM", lessema);
            stringTable.put(lessema, token);
            return token;
        }
    }

    private void retrack(){
        file_offset--;
        // fa il retract nel file di un carattere
    }

}
