/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Codec {
    // Decodes your encoded data to tree.
     int idx = 0;
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if(root == null) return "N";
        StringBuilder sb = new StringBuilder(String.valueOf(root.val));
        sb.append(" ");
        sb.append(serialize(root.left));
        sb.append(" ");
        sb.append(serialize(root.right));
        return sb.toString();
    }


    public TreeNode deserialize(String data) {
         String[]str = data.split(" ");
        if(str.length==0)return null;
        return getvalue(str);
    }
    
    public TreeNode getvalue(String[] str){
        if(idx >= str.length) return null;
        String s = str[idx++];
        if(s.equals("N")) return null;
        TreeNode root = new TreeNode(Integer.parseInt(s));
        root.left = getvalue(str);
        root.right = getvalue(str);
        return root;
    }
}

// Your Codec object will be instantiated and called as such:
// Codec ser = new Codec();
// Codec deser = new Codec();
// String tree = ser.serialize(root);
// TreeNode ans = deser.deserialize(tree);
// return ans;