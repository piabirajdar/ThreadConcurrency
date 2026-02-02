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

    // Encodes a tree to a single string.
    // "1 2 3 n n 4 5 n n n n"
    public String serialize (TreeNode root) {
        if(root == null) return "";
        Queue<TreeNode> q =new LinkedList<>();
        q.offer(root);
        StringBuilder sb = new StringBuilder();

        while(!q.isEmpty()) {
            TreeNode node = q.poll();
            if(node == null) {
                sb.append("n ");
                continue;
            }
            sb.append(node.val + " ");
            q.offer(node.left);
            q.offer(node.right);
        }
        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if(data == "") return null;
        Queue<TreeNode> q = new LinkedList<>();
        String[] values = data.split(" ");
        TreeNode root = new TreeNode(Integer.parseInt(values[0]));
        q.offer(root);
        for(int i=1; i < values.length; i++){
            TreeNode node = q.poll();
            System.out.println(node.val);
            if(!values[i].equals("n")){
                TreeNode left = new TreeNode(Integer.parseInt(values[i]));
                node.left = left;
                q.offer(left);
            }

             if(!values[++i].equals("n")){
                TreeNode right = new TreeNode(Integer.parseInt(values[i]));
                node.right = right;
                q.offer(right);
            }
        }
        return root;
    }
}

// Your Codec object will be instantiated and called as such:
// Codec ser = new Codec();
// Codec deser = new Codec();
// TreeNode ans = deser.deserialize(ser.serialize(root));