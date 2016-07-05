package method;

public class MyFormula<T>
{
	public T[] reverse(T[] a)
	{
		T[] b;

		b = a;
		
		for (int i = 0; i < a.length; i++)
		{
			b[i] = a[a.length - 1 - i];
		}
		return b;
	}
}
