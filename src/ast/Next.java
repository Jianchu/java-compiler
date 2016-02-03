package ast;

public interface Next<E> {
	public boolean hasNext();
	public E next();
}
