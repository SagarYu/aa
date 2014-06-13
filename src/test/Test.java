package test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args) throws IOException {
		/** memcached test 
		MemcachedClient client = new MemcachedClient(new InetSocketAddress(
				"192.168.1.33", 11211));
		// 60是超时数，默认以秒为单位
		client.set("test", 60, "1111测试memcache成功了吧");
		client.set("aa", 60, "abcd");
		System.out.println(client.get("test"));
		client.shutdown();// 关闭连接
*/
//		Long l=1400643722l;
//
//		Date d=new Date(l*1000l);
//		System.out.println(d.toLocaleString());
		String cc="/9j/4AAQSkZJRgABAgAAZABkAAD/7AARRHVja3kAAQAEAAAAPAAA/+4ADkFkb2JlAGTAAAAAAf/bAIQABgQEBAUEBgUFBgkGBQYJCwgGBggLDAoKCwoKDBAMDAwMDAwQDA4PEA8ODBMTFBQTExwbGxscHx8fHx8fHx8fHwEHBwcNDA0YEBAYGhURFRofHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8f/8AAEQgAEgAOAwERAAIRAQMRAf/EAFEAAQEBAAAAAAAAAAAAAAAAAAABBwEBAQEBAAAAAAAAAAAAAAAAAAEFAhABAQAAAAAAAAAAAAAAAAAAABERAQEAAAAAAAAAAAAAAAAAAAAR/9oADAMBAAIRAxEAPwDSK3WPShUVAIK6Af/Z";
		
		String aa="/9j/4AAQSkZJRgABAgAAZABkAAD/7AARRHVja3kAAQAEAAAAPAAA/+4ADkFkb2JlAGTAAAAAAf/bAIQABgQEBAUEBgUFBgkGBQYJCwgGBggLDAoKCwoKDBAMDAwMDAwQDA4PEA8ODBMTFBQTExwbGxscHx8fHx8fHx8fHwEHBwcNDA0YEBAYGhURFRofHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8f/8AAEQgAEgAOAwERAAIRAQMRAf/EAFEAAQEBAAAAAAAAAAAAAAAAAAABBwEBAQEBAAAAAAAAAAAAAAAAAAEFAhABAQAAAAAAAAAAAAAAAAAAABERAQEAAAAAAAAAAAAAAAAAAAAR/9oADAMBAAIRAxEAPwDSK3WPShUVAIK6Af/Z";
		if(cc.equals(aa)){
			System.out.println("ok............");
		}else{
			System.out.println("no.............");
		}
		//String bb="/9j/4AAQSkZJRgABAgAAZABkAAD/7AARRHVja3kAAQAEAAAAPAAA/%2B4ADkFkb2JlAGTAAAAAAf/bAIQABgQEBAUEBgUFBgkGBQYJCwgGBggLDAoKCwoKDBAMDAwMDAwQDA4PEA8ODBMTFBQTExwbGxscHx8fHx8fHx8fHwEHBwcNDA0YEBAYGhURFRofHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8f/8AAEQgAEgAOAwERAAIRAQMRAf/EAFEAAQEBAAAAAAAAAAAAAAAAAAABBwEBAQEBAAAAAAAAAAAAAAAAAAEFAhABAQAAAAAAAAAAAAAAAAAAABERAQEAAAAAAAAAAAAAAAAAAAAR/9oADAMBAAIRAxEAPwDSK3WPShUVAIK6Af/Z";
	}
	public static String convert(long mill){
		Date date=new Date(mill);
		String strs="";
		try {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		strs=sdf.format(date);
		} catch (Exception e) {
		e.printStackTrace();
		}
		return strs;
	}
}
