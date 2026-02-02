class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    public TrieNode(){
        children = new HashMap<Character, TrieNode>();
    }
}   
class Trie {
    TrieNode root=null;
    public Trie() {
    root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode node = root;
        for(char c : word.toCharArray()){
            if(!node.children.containsKey(c)){
                node.children.put(c, new TrieNode());
            }//get the new created node always
            node = node.children.get(c);
        }
        node.isEndOfWord = true;

    }
    
    public boolean search(String word) {
        TrieNode node = root;
        for(char c : word.toCharArray()){
            if(!node.children.containsKey(c)) return false;
            node = node.children.get(c);
        }//end of word should be true if thats the end after strng exhaustion
        return node.isEndOfWord;
    }
    
    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for(char c : prefix.toCharArray()){
            if(!node.children.containsKey(c)) return false;
            node = node.children.get(c);
        }// the word prefix is exhausted
        return true;
    }
}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */