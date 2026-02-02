class TrieNode {
    Map<Character, TrieNode> children = new HashMap();
    boolean isEndOfWord = false;

    public TrieNode() {}
}

class WordDictionary {
    TrieNode root;

    /** Initialize your data structure here. */
    public WordDictionary() {
        root = new TrieNode();
    }

    /** Adds a word into the data structure. */
    public void addWord(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                node.children.put(ch, new TrieNode());
            }
            node = node.children.get(ch);
        }
        node.isEndOfWord = true;
    }

    /** Returns if the word is in the node. */
    public boolean searchInNode(String word, TrieNode node) {
        for(int i=0; i < word.length(); i++){
            char c = word.charAt(i);
            if(!node.children.containsKey(c)) {
                if (c == '.'){
                    for(char ch: node.children.keySet()){
                        TrieNode child = node.children.get(ch);
                        if(searchInNode(word.substring(i+1), child))
                            return true;
                    }
                }
                return false;
            } else {
                node = node.children.get(c);
            }
        }
        return node.isEndOfWord;
    }

    /** Returns if the word is in the data structure. A word could contain the dot character '.' to represent any one letter. */
    public boolean search(String word) {
        return searchInNode(word, root);
    }
}