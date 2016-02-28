package joosc.lang;
public interface ObjInterface() {
	public boolean equals(Object other);
	public String toString();
	public int hashCode();
	protected Object clone();
	public final Class getClass();
}