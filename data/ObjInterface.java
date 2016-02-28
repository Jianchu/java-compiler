package joosc.lang;

import java.lang.String;

public interface ObjInterface {
	public boolean equals(Object other);
	public String toString();
	public int hashCode();
//	protected Object clone();
	public Class getClass();
}