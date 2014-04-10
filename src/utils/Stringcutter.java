package utils;

public class Stringcutter {
	public static String[] cut(String a, int size){
		int max;
		String x = a;
		if(x.length()%size != 0)
		{
			max = (int)x.length() / size + 1;
			for(int i = 0; i < x.length()%size; i++){
			x +=" ";
		}
		}else{
			max = (int)x.length() / size;
		}
		
		String[] result = new String[max];
		for(int i = 0; i < result.length; i++){
			result[i] = x.substring(size*i, size*i + size);
		}
		
		return result;
	}
}