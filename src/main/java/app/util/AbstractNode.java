package app.util;

public abstract class AbstractNode<T> {
    protected T data;
    protected AbstractNode<T> lNode;
    protected AbstractNode<T> rNode;
    protected boolean isRoot;

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public AbstractNode<T> getlNode() {
        return lNode;
    }

    public void setlNode(final AbstractNode<T> lNode) {
        this.lNode = lNode;
    }

    public AbstractNode<T> getrNode() {
        return rNode;
    }

    public void setrNode(final AbstractNode<T> rNode) {
        this.rNode = rNode;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(final boolean isRoot) {
        this.isRoot = isRoot;
    }
}
