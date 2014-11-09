package app.util;

public class BTree<T> {
    private AbstractNode<T> root;

    public BTree(final AbstractNode<T> root) {
        this.root = root;
    }

    public AbstractNode<T> getRoot() {
        return root;
    }
}
