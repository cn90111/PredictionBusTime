package method;

public class MyFormula<T>
{
	public void reverse(T[] a)
	{
		T temp;
		
		for (int i = 0; i < a.length/2; i++)
		{
			temp = a[i];
			a[i] = a[a.length-1-i];
			a[a.length-1-i] = temp;
		}
	}
}
