import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode  {

    public String data;
    public TreeNode parent;
    public LinkedList<TreeNode> children;

    public TreeNode(String data) {
        this.data = new String(data);
        this.children = new LinkedList<TreeNode>();
    }

    public TreeNode addChild(String child) {
        TreeNode childNode = new TreeNode(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    public TreeNode getNextBrother(){
        boolean found= false;
        Iterator<TreeNode> brothers;
        if(this.parent!= null){
            brothers= this.parent.children.iterator();
            while(brothers.hasNext()){
                if(brothers.next()==this){
                    found=true;
                    break;
                }

            }
            if(found && brothers.hasNext())
                return brothers.next();
            else if(found){
                return this.parent.getNextBrother();
            }
            else return null;

        }
        return this;
    }

    private String print(TreeNode root, int indent){
        String ind =new String(new char[indent]).replace("\0", "   ");
        Iterator<TreeNode> iterator;
        String out="";
        if(root.children.isEmpty()){
            out =ind+(root.data)+"\n";
            System.out.println(out);
        } else {
            iterator = root.children.iterator();
            out = root.data+"\n";
            while (iterator.hasNext()) {
               out = out + print(iterator.next(), indent+1);
            }
        }
        return out;
    }

    public String toString(){
        String out = print(this, 1);


        return out;
    }
}
